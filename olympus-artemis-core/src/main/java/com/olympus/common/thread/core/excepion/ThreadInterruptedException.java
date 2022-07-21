package com.olympus.common.thread.core.excepion;

import com.olympus.base.utils.support.exception.ExtendRuntimeException;
import com.olympus.base.utils.support.globalization.ErrorMessage;

/**
 * 线程中断异常
 * since 2020/2/21
 *
 * @author eddie
 */
public class ThreadInterruptedException extends ExtendRuntimeException {

    public ThreadInterruptedException() {
    }

    public ThreadInterruptedException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

    public ThreadInterruptedException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
