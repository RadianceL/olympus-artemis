package com.el.base.utils.support.globalization;

import com.el.base.utils.support.globalization.context.GlobalizationLocalUtil;
import com.el.base.utils.support.globalization.lang.Local;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.PropertyKey;

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

    private static final String BUNDLE = "i18n.i18n_default";
    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误码描述信息
     */
    private String errorMessage;

    /**
     * 空错误
     */
    public static final ErrorMessage EMPTY_ERROR_MESSAGE = new ErrorMessage("UN_KNOW_EXCEPTION", "SYSTEM_ERROR");

    /**
     * 单语言实例
     *
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     * @return 错误信息对象
     */
    public static ErrorMessage of(@PropertyKey(resourceBundle = BUNDLE) String errorCode, String errorMessage) {
        String message = GlobalMessagePool.getMessage(errorCode, GlobalizationLocalUtil.getLocalLanguage(), errorMessage);
        return new ErrorMessage(errorCode, message);
    }

    /**
     * 默认中文单语言实例
     *
     * @param errorCode 错误码
     * @param args      错误参数
     * @return 错误信息对象
     */
    public static ErrorMessage of(@PropertyKey(resourceBundle = BUNDLE) String errorCode, Object... args) {
        String message = GlobalMessagePool.getMessage(errorCode, GlobalizationLocalUtil.getLocalLanguage(), args);
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
        String message = GlobalMessagePool.getMessage(errorCode, local, args);
        return new ErrorMessage(errorCode, message);
    }

}
