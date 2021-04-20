package com.el.base.utils.support.exception;

import com.el.base.utils.support.exception.data.ErrorMessage;
import org.slf4j.Logger;

import java.util.Objects;

/**
 * 扩展运行时异常
 * since 2019/12/7
 *
 * @author eddie
 */
public class ExtendRuntimeException extends RuntimeException {

    private ErrorMessage errorMessage;

    public ExtendRuntimeException() {
        this(ErrorMessage.EMPTY_ERROR_MESSAGE);
        this.errorMessage = ErrorMessage.EMPTY_ERROR_MESSAGE;
    }

    public ExtendRuntimeException(String error) {
        super(error);
    }

    public ExtendRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage.getErrorMessage());
        this.errorMessage = errorMessage;
    }

    public ExtendRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getErrorMessage(), cause);
        this.errorMessage = errorMessage;
    }

    public ExtendRuntimeException(ErrorMessage errorMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorMessage.getErrorMessage(), cause, enableSuppression, writableStackTrace);
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        if (Objects.isNull(errorMessage)) {
            return "";
        }
        return errorMessage.getErrorCode();
    }

    public String getErrorMessage() {
        if (Objects.isNull(errorMessage)) {
            return "";
        }
        return errorMessage.getErrorMessage();
    }
}
