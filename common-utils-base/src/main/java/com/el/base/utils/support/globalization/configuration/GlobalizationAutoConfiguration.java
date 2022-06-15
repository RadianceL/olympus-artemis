package com.el.base.utils.support.globalization.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.el.base.utils.support.globalization.GlobalMessagePool;
import com.el.base.utils.web.HttpsClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 多语言全球化自动配置类
 * since 4/11/22
 *
 * @author eddie
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GlobalizationAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    private final GlobalizationApplicationConfig globalizationApplicationConfig;

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String APPLICATION_DOCUMENT_PATH = "/document/api/application/application/document/iso/list";

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        log.info("init global document message pool");
        String globalDocumentSystemUrl = globalizationApplicationConfig.getGlobalDocumentSystemUrl();
        if (StringUtils.isNotBlank(globalDocumentSystemUrl)) {
            try {
                globalDocumentSync(globalDocumentSystemUrl, "pass-saas-basics");
                globalDocumentSync(globalDocumentSystemUrl, applicationName);
            } catch (Exception e) {
                log.error("init global document message pool error", e);
                throw new RuntimeException("init global document message pool error", e);
            }
        }
    }

    private void globalDocumentSync(String globalDocumentSystemUrl, String applicationName) throws Exception {
        Map<String, String> requestBody = new HashMap<>(4);
        requestBody.put("applicationPath", "/" + applicationName);
        String result = HttpsClientUtil.singletonHttpsPostRequest(globalDocumentSystemUrl.concat(APPLICATION_DOCUMENT_PATH),null, JSON.toJSONString(requestBody));
        JSONObject obj = JSON.parseObject(result);
        if ("0000".equalsIgnoreCase(obj.getString("code")) && "OK".equalsIgnoreCase(obj.getString("message"))) {
            JSONObject data = obj.getJSONObject("data");
            Set<String> countryIsoSet = data.keySet();
            for (String countryIso : countryIsoSet) {
                JSONObject countryDocument = data.getJSONObject(countryIso);
                Map<String, String> countryDocumentMap = new HashMap<>(32);
                for (Map.Entry<String, Object> entry : countryDocument.entrySet()) {
                    if (Objects.nonNull(entry.getValue())) {
                        countryDocumentMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }
                GlobalMessagePool.addCountryIsoDocument(countryIso, countryDocumentMap);
            }
        }else {
            log.error("init global document message pool error, server result error : {}", result);
            throw new RuntimeException("init global document message pool error, server result error : " + result);
        }
    }
}
