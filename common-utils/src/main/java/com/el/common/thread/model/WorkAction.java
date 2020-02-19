package com.el.common.thread.model;

/**
 * 无返回值的任务
 * 2019-01-18
 *
 * @author eddielee
 */
@FunctionalInterface
public interface WorkAction {

    /**
     * 执行工作
     *
     * @param source
     */
    void execute(Object source);
}
