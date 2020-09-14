package com.el.common.thread.model;

/**
 * 无数据源线程
 * since 2020/2/22
 *
 * @author eddie
 */
@FunctionalInterface
public interface WorkWithoutSourceAction {

    /**
     * 执行工作
     */
    void execute();
}
