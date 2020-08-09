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
public abstract class AbstractDomainRuntimeException extends ExtendRuntimeException {

    public AbstractDomainRuntimeException() {
    }

    public AbstractDomainRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

    public AbstractDomainRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public abstract String getDomain();
}
