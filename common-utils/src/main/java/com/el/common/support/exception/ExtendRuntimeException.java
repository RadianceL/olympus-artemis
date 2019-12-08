package com.el.common.support.exception;

import com.el.common.support.exception.data.ErrorMessage;

import java.util.Objects;

/**
 * 扩展运行时异常
 * since 2019/12/7
 *
 * @author eddie
 */
public class ExtendRuntimeException extends RuntimeException{

    private ErrorMessage errorMessage;

    public ExtendRuntimeException() {
        this(ErrorMessage.EMPTY_ERROR_MESSAGE);
        this.errorMessage = ErrorMessage.EMPTY_ERROR_MESSAGE;
    }

    public ExtendRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage.getErrorMessage());
        this.errorMessage = errorMessage;
    }

    public ExtendRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getErrorMessage(), cause);
        this.errorMessage = errorMessage;
    }


    public String getErrorCode(){
        if (Objects.isNull(errorMessage)){
            return "";
        }
        return errorMessage.getErrorCode();
    }
}
