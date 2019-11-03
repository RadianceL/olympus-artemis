package com.el.common.thread.exceptions;

/**
 * @author eddie
 * @createTime 2019-06-05
 * @description 容器状态异常
 */
public class BoundedBufferStatusException extends BasicBoundedBufferException {

    public BoundedBufferStatusException() {
    }

    public BoundedBufferStatusException(String format, Object... args) {
        super(format, args);
    }

    public BoundedBufferStatusException(String message) {
        super(message);
    }
}
