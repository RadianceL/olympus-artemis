package com.el.base.utils.support.exception.data;

import com.el.base.utils.support.io.local.LocalFileUtil;
import com.el.base.utils.support.io.local.PropertiesReadUtil;
import com.el.base.utils.support.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.PropertyKey;
import org.slf4j.helpers.MessageFormatter;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 错误信息解析
 * since 2019/12/5
 *
 * @author eddie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {

    private static final String BUNDLE = "i18n.errors";

    /**
     * 读取多语言配置
     */
    private static final Map<String, Map<String, String>> PROPERTIES;

    /**
     * 空错误
     */
    public static final ErrorMessage EMPTY_ERROR_MESSAGE = new ErrorMessage("UN_KNOW_EXCEPTION", "SYSTEM_ERROR");

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
     * 错误码
     */
    private String errorCode;

    /**
     * 错误码描述信息
     */
    private String errorMessage;

    /**
     * 单语言实例
     *
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     * @return 错误信息对象
     */
    public static ErrorMessage of(@PropertyKey(resourceBundle = BUNDLE) String errorCode, String errorMessage) {
        return new ErrorMessage(errorCode, errorMessage);
    }

    /**
     * 默认中文单语言实例
     *
     * @param errorCode 错误码
     * @param args      错误参数
     * @return 错误信息对象
     */
    public static ErrorMessage of(@PropertyKey(resourceBundle = BUNDLE) String errorCode, Object... args) {
        String message = getMessage(errorCode, Local.CN, args);
        return new ErrorMessage(errorCode, message);
    }

    /**
     * 多语言版本实例
     *
     * @param errorCode 错误码
     * @param local     语言
     * @param args      错误参数
     * @return 错误信息对象
     */
    public static ErrorMessage of(@PropertyKey(resourceBundle = BUNDLE) String errorCode, Local local, Object... args) {
        String message = getMessage(errorCode, local, args);
        return new ErrorMessage(errorCode, message);
    }

    /**
     * 获取
     *
     * @param errorCode 错误码
     * @param local     语言
     * @param args      错误参数
     * @return 错误信息
     */
    private static String getMessage(String errorCode, Local local, Object... args) {
        Map<String, String> properties = PROPERTIES.get(local.getLocalName());
        String messagePattern = properties.get(errorCode);
        if (StringUtils.isNotBlank(messagePattern)) {
            return MessageFormatter.format(messagePattern, args).getMessage();
        } else {
            return Constant.EMPTY_STRING;
        }
    }

}
