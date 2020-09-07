package com.el.base.utils.support.exception.handler;

import com.el.base.utils.support.exception.AbstractDomainRuntimeException;
import com.el.base.utils.support.exception.ExceptionHandler;
import com.el.base.utils.support.exception.data.ErrorMessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异常处理中心
 * since 2020/2/22
 *
 * @author eddie
 */
public class ExceptionHandlerManager {

    private static final Map<String, ExceptionHandler> EXCEPTION_HANDLER_MAP = new ConcurrentHashMap<>(16);

    public static void registerExceptionHandler(ExceptionHandler exceptionHandler){
        EXCEPTION_HANDLER_MAP.put(exceptionHandler.ofExceptionDomain(), exceptionHandler);
    }

    public static ErrorMessage handlerException(AbstractDomainRuntimeException e){
        ExceptionHandler exceptionHandler = EXCEPTION_HANDLER_MAP.get(e.getDomain());
        if (Objects.isNull(exceptionHandler)) {
            throw e;
        }

        return exceptionHandler.handleException(e);
    }

}
