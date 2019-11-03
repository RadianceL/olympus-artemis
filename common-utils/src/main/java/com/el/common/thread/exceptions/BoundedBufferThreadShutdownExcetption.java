package com.el.common.thread.exceptions;

/**
 * @author eddie
 * @createTime 2019-05-30
 * @description 线程关闭异常
 */
public class BoundedBufferThreadShutdownExcetption extends BasicBoundedBufferException{
    public BoundedBufferThreadShutdownExcetption() {
    }

    public BoundedBufferThreadShutdownExcetption(String format, Object... args) {
        super(format, args);
    }

    public BoundedBufferThreadShutdownExcetption(String message) {
        super(message);
    }
}
