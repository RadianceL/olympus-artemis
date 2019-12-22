package com.el.common.support.exception;

import com.el.common.support.exception.data.ErrorMessage;

/**
 * 异常处理器负责的领域
 * since 2019/12/22
 *
 * @author eddie
 */
public interface ExceptionHandler {

    /**
     * 返回异常处理器所在领域
     * @return  异常处理器所在领域
     */
    String ofExceptionDomain();

    /**
     * 处理运行时异常
     * @param e     异常
     * @return      错误信息
     */
    ErrorMessage handleException(ExtendRuntimeException e);

    /**
     * 处理 <p>checked</p> 异常
     * @param e     异常
     * @return      错误信息
     */
    ErrorMessage handleException(ImportantErrorException e);

}
