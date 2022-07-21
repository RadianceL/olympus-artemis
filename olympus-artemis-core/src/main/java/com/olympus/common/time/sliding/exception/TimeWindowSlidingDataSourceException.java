package com.olympus.common.time.sliding.exception;

/**
 * 时间分片数据源操作异常
 * since 2019/12/8
 *
 * @author eddie
 */
public class TimeWindowSlidingDataSourceException extends RuntimeException {

    public TimeWindowSlidingDataSourceException() {
    }

    public TimeWindowSlidingDataSourceException(String message) {
        super(message);
    }

    public TimeWindowSlidingDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
