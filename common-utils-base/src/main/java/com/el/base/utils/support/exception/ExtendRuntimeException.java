package com.el.base.utils.support.exception;

import com.el.base.utils.support.ErrorsEventLogger;
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

    private final Logger log = ErrorsEventLogger.getInstance();

    public ExtendRuntimeException() {
        this(ErrorMessage.EMPTY_ERROR_MESSAGE);
        this.errorMessage = ErrorMessage.EMPTY_ERROR_MESSAGE;
        log.error("Runtime un-know type exception", this);
    }

    public ExtendRuntimeException(String error) {
        super(error);
        log.error("Runtime normal error string: [{}]", error, this);
    }

    public ExtendRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage.getErrorMessage());
        this.errorMessage = errorMessage;
        log.error("Runtime normal error message exception, key -> [{}] value -> [{}]", getErrorCode(), getErrorMessage(), this);
    }

    public ExtendRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getErrorMessage(), cause);
        this.errorMessage = errorMessage;
        log.error("Runtime normal error message exception, key -> [{}] value -> [{}]", getErrorCode(), getErrorMessage(), this);
    }

    public ExtendRuntimeException(ErrorMessage errorMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorMessage.getErrorMessage(), cause, enableSuppression, writableStackTrace);
        this.errorMessage = errorMessage;
        log.error("Runtime normal error message exception, key -> [{}] value -> [{}]", getErrorCode(), getErrorMessage(), this);
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
