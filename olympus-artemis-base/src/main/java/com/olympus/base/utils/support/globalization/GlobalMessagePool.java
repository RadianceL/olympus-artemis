package com.olympus.base.utils.support.globalization;

import com.olympus.base.utils.support.Constant;
import com.olympus.base.utils.support.globalization.lang.Local;
import com.olympus.base.utils.support.io.local.LocalFileUtil;
import com.olympus.base.utils.support.io.local.PropertiesReadUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

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
@Component
public class GlobalMessagePool {

    /**
     * 读取多语言配置
     */
    private static final Map<String, Map<String, String>> PROPERTIES;

    private static final Map<String, Map<String, String>> COUNTRY_ISO_MAP = new HashMap<>(8);

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

    public static void addCountryIsoDocument(String countryIsoMap, Map<String, String> countryDocument) {
        if (Objects.nonNull(COUNTRY_ISO_MAP.get(countryIsoMap))) {
            COUNTRY_ISO_MAP.get(countryIsoMap).putAll(countryDocument);
            return;
        }
        COUNTRY_ISO_MAP.put(countryIsoMap, countryDocument);
    }


    /**
     * 获取
     *
     * @param messageCode   错误码
     * @param local         语言
     * @return 错误信息
     */
    public static String getMessage(String messageCode, Local local) {
        String messagePattern;
        Map<String, String> countryIsoMap = COUNTRY_ISO_MAP.get(local.getLocalName());
        if (Objects.nonNull(countryIsoMap)) {
            messagePattern = countryIsoMap.get(messageCode);
            if (StringUtils.isNotBlank(messagePattern)) {
                return messagePattern;
            }else {
                return getDefaultMessage(messageCode, local);
            }
        }else {
            return getDefaultMessage(messageCode, local);
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
        String messagePattern;
        Map<String, String> countryIsoMap = COUNTRY_ISO_MAP.get(local.getLocalName());
        if (Objects.nonNull(countryIsoMap)) {
            messagePattern = countryIsoMap.get(messageCode);
            if (StringUtils.isNotBlank(messagePattern)) {
                return MessageFormatter.format(messagePattern, args).getMessage();
            }else {
               return getDefaultMessage(messageCode, local, args);
            }
        }else {
            return getDefaultMessage(messageCode, local, args);
        }
    }


    private static String getDefaultMessage(String messageCode, Local local) {
        if (Local.CN.equals(local)) {
            String messagePattern;
            Map<String, String> properties = PROPERTIES.get(local.getLocalName());
            if (Objects.isNull(properties)) {
                return Constant.EMPTY_STRING;
            }
            messagePattern = properties.get(messageCode);
            if (StringUtils.isNotBlank(messagePattern)) {
                return messagePattern;
            } else {
                return messageCode + "：无对应文言";
            }
        }else {
            String messagePattern = "unknown message";
            Map<String, String> countryIsoMap = COUNTRY_ISO_MAP.get(Local.EN.getLocalName());
            if (Objects.nonNull(countryIsoMap)) {
                messagePattern = countryIsoMap.get(messageCode);
                if (StringUtils.isNotBlank(messagePattern)) {
                    return messagePattern;
                }else {
                    return messageCode + "：no message provide";
                }
            }
            return messagePattern;
        }
    }

    private static String getDefaultMessage(String messageCode, Local local, Object[] args) {
        if (Local.CN.equals(local)) {
            String messagePattern;
            Map<String, String> properties = PROPERTIES.get(local.getLocalName());
            if (Objects.isNull(properties)) {
                return Constant.EMPTY_STRING;
            }
            messagePattern = properties.get(messageCode);
            if (StringUtils.isNotBlank(messagePattern)) {
                return MessageFormatter.format(messagePattern, args).getMessage();
            } else {
                return Constant.EMPTY_STRING;
            }
        }else {
            String messagePattern = "unknown message";
            Map<String, String> countryIsoMap = COUNTRY_ISO_MAP.get(Local.EN.getLocalName());
            if (Objects.nonNull(countryIsoMap)) {
                messagePattern = countryIsoMap.get(messageCode);
                if (StringUtils.isNotBlank(messagePattern)) {
                    return MessageFormatter.format(messagePattern, args).getMessage();
                }else {
                    return messageCode + "：no message provide";
                }
            }
            return messagePattern;
        }
    }
}
