package com.el.base.utils.support.globalization;

import com.el.base.utils.support.Constant;
import com.el.base.utils.support.globalization.lang.Local;
import com.el.base.utils.support.io.local.LocalFileUtil;
import com.el.base.utils.support.io.local.PropertiesReadUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文案库
 *
 * @author eddie.lys
 * @since 3/30/2022
 */
public class GlobalMessagePool {


    /**
     * 读取多语言配置
     */
    private static final Map<String, Map<String, String>> PROPERTIES;

    static {
        /* 初始化多语言配置 */
        PROPERTIES = new HashMap<>();
        URL resource = ErrorMessage.class.getClassLoader().getResource("i18n/");
        if (!Objects.isNull(resource)) {
            List<String> fileNames = LocalFileUtil.getFiles(resource.getPath());
            for (String fileName : fileNames) {
                Map<String, String> defaultProperties = PropertiesReadUtil.getProperties("i18n/".concat(fileName));
                String localName = Local.findLocalName(fileName);
                PROPERTIES.put(localName, defaultProperties);
            }
        }
    }

    /**
     * 获取
     *
     * @param messageCode   错误码
     * @param local         语言
     * @param args          参数
     * @return 错误信息
     */
    public static String getMessage(String messageCode, Local local, Object... args) {
        Map<String, String> properties = PROPERTIES.get(local.getLocalName());
        String messagePattern = properties.get(messageCode);
        if (StringUtils.isNotBlank(messagePattern)) {
            return MessageFormatter.format(messagePattern, args).getMessage();
        } else {
            return Constant.EMPTY_STRING;
        }
    }
}
