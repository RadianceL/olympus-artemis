package com.olympus.common.thread.core.model;

/**
 * 无返回值的任务
 * 2019-01-18
 *
 * @author eddielee
 */
@FunctionalInterface
public interface WorkAction<T> {

    /**
     * 执行工作
     */
    void execute(T source);
}
