package com.el.common.support.exception;

import com.el.common.support.exception.data.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 扩展运行时异常
 * since 2019/12/7
 *
 * @author eddie
 */
@Slf4j
public class ExtendRuntimeException extends RuntimeException {

    private ErrorMessage errorMessage;


    public ExtendRuntimeException() {
        this(ErrorMessage.EMPTY_ERROR_MESSAGE);
        this.errorMessage = ErrorMessage.EMPTY_ERROR_MESSAGE;
        log.error("无法定义的异常, {} : {}", getErrorCode(), getErrorMessage());
    }

    public ExtendRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage.getErrorMessage());
        this.errorMessage = errorMessage;
        log.error("{} : {}", getErrorCode(), getErrorMessage());
    }

    public ExtendRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getErrorMessage(), cause);
        this.errorMessage = errorMessage;
        log.error("{} : {}", getErrorCode(), getErrorMessage(), cause);
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
