package com.el.common.support.exception;

import com.el.common.support.exception.data.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 重要异常
 * since 2019/12/7
 *
 * @author eddie
 */
@Slf4j
public class ImportantErrorException extends Exception {

    private ErrorMessage errorMessage;

    public ImportantErrorException() {
        this(ErrorMessage.EMPTY_ERROR_MESSAGE);
        this.errorMessage = ErrorMessage.EMPTY_ERROR_MESSAGE;
        log.error("无法定义的异常, {} : {}", getErrorCode(), getErrorMessage());
    }

    public ImportantErrorException(ErrorMessage errorMessage) {
        super(errorMessage.getErrorMessage());
        this.errorMessage = errorMessage;
    }

    public ImportantErrorException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getErrorMessage(), cause);
        this.errorMessage = errorMessage;
        log.error("{} : {}", getErrorCode(), getErrorMessage());
    }

    public ImportantErrorException(ErrorMessage errorMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorMessage.getErrorMessage(), cause, enableSuppression, writableStackTrace);
        this.errorMessage = errorMessage;
        log.error("{} : {}", getErrorCode(), getErrorMessage());
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
