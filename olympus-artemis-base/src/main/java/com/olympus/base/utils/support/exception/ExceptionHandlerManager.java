package com.olympus.base.utils.support.exception;

import com.olympus.base.utils.support.globalization.ErrorMessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异常处理中心
 * 继承自{@link AbstractDomainRuntimeException}的异常
 * 可以根据Domain来决定处理方式
 * 举例：
 *  全系统内所有P1级别异常，发送邮件告警
 *  全系统内所有P2级别异常，发送通知部分人员
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
