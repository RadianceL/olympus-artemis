package com.el.common.thread.exceptions;

/**
 * @author eddie
 * @createTime 2019-05-30
 * @description 当前状态不允许更改
 */
public class BoundedBufferStateUnsupportedException extends BasicBoundedBufferException {

    public BoundedBufferStateUnsupportedException() {
    }

    public BoundedBufferStateUnsupportedException(String format, Object... args) {
        super(format, args);
    }

    public BoundedBufferStateUnsupportedException(String message) {
        super(message);
    }
}
