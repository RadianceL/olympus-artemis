package com.el.common.support.exception.data;

import com.el.common.io.local.FileUtil;
import com.el.common.io.local.PropertiesReadUtil;
import com.el.common.support.Constant;
import com.el.common.support.data.Local;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    private static final Map<String, Map<String,String>> PROPERTIES;

    public static final ErrorMessage EMPTY_ERROR_MESSAGE = new ErrorMessage("ZERO", "EMPTY_ERROR_MESSAGE");

    static {
        PROPERTIES = new HashMap<>();
        URL resource = ErrorMessage.class.getClassLoader().getResource("message/");
        if (!Objects.isNull(resource)){
            List<String> fileNames = FileUtil.getFiles(resource.getPath());
            for (String fileName : fileNames) {
                Map<String, String> defaultProperties = PropertiesReadUtil.getProperties("message/".concat(fileName));
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

    public static ErrorMessage of(String errorCode, Object... args){
        String message = getMessage(errorCode, Local.CN, args);
        return new ErrorMessage(errorCode, message);
    }

    public static ErrorMessage of(String errorCode, Local local, Object... args){
        String message = getMessage(errorCode, local, args);
        return new ErrorMessage(errorCode, message);
    }

    private static String getMessage(String errorCode, Local local, Object... args){
        if (Local.CN.equals(local)){
            Map<String, String> defaultProperties = PROPERTIES.get(Local.CN.getLocalName());
            String messagePattern = defaultProperties.get(errorCode);
            if (StringUtils.isNotBlank(messagePattern)){
                return MessageFormatter.format(messagePattern, args).getMessage();
            }else {
                return Constant.EMPTY_STRING;
            }
        }
        Map<String, String> properties = PROPERTIES.get(local.getLocalName());
        String messagePattern = properties.get(errorCode);
        if (StringUtils.isNotBlank(messagePattern)){
            return MessageFormatter.format(messagePattern, args).getMessage();
        }else {
            return Constant.EMPTY_STRING;
        }
    }

}
