package com.el.common.support.exception.handler;

import com.el.common.support.exception.AbstractDomainRuntimeException;
import com.el.common.support.exception.ExceptionHandler;
import com.el.common.support.exception.data.ErrorMessage;

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

    private static Map<String, ExceptionHandler> exceptionHandlerMap = new ConcurrentHashMap<>(8);

    public static void registerExceptionHandler(ExceptionHandler exceptionHandler){
        exceptionHandlerMap.put(exceptionHandler.ofExceptionDomain(), exceptionHandler);
    }

    public static ErrorMessage handlerException(AbstractDomainRuntimeException e){
        ExceptionHandler exceptionHandler = exceptionHandlerMap.get(e.getDomian());
        if (Objects.isNull(exceptionHandler)) {
            throw e;
        }

        return exceptionHandler.handleException(e);
    }

}
