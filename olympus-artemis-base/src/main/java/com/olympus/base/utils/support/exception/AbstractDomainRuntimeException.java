package com.olympus.base.utils.support.exception;

import com.olympus.base.utils.support.globalization.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 扩展运行时异常
 * since 2019/12/7
 *
 * @author eddie
 */
@Slf4j
public abstract class AbstractDomainRuntimeException extends ExtendRuntimeException{

    public AbstractDomainRuntimeException() {
        super(ErrorMessage.EMPTY_ERROR_MESSAGE);
    }

    public AbstractDomainRuntimeException(String error) {
        super(error);
    }

    public AbstractDomainRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage.getErrorMessage());
    }

    public AbstractDomainRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public AbstractDomainRuntimeException(ErrorMessage errorMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorMessage, cause, enableSuppression, writableStackTrace);
    }

    /**
     * 获取当前领域
     * @return     返回当前领域
     */
    public abstract String getDomain();
}
