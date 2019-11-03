package com.el.common.thread.model.action;

/**
 * 无返回值的工作
 * 2019-01-18
 *
 * @author eddielee
 */
@FunctionalInterface
public interface WorkAction<T> {

    /**
     * 执行工作
     * @param source
     */
    void execute(T source);
}
