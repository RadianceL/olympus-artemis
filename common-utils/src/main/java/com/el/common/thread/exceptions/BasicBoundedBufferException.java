package com.el.common.thread.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author eddie
 * @createTime 2019-05-27
 * @description 生产者消费者基础异常
 */
@Slf4j
public class BasicBoundedBufferException extends RuntimeException {

    public BasicBoundedBufferException() {
        super();
        log.error(this.getMessage());
    }

    public BasicBoundedBufferException(String format, Object... args) {
        super(MessageFormatter.arrayFormat(format, args).getMessage());
        log.error(this.getMessage());
    }

    public BasicBoundedBufferException(String message) {
        super(message);
        log.error(this.getMessage());
    }


    public BasicBoundedBufferException(String message, Throwable cause) {
        super(message, cause);
        log.error(this.getMessage());
    }

    public BasicBoundedBufferException(Throwable cause) {
        super(cause);
        log.error(this.getMessage());
    }

    public BasicBoundedBufferException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        log.error(this.getMessage());
    }

}
