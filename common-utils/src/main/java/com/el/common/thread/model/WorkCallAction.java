package com.el.common.thread.model;

/**
 * 有返回值的任务
 * 2019/10/7
 *
 * @author eddielee
 */
@FunctionalInterface
public interface WorkCallAction {

    /**
     * 执行工作 有返回值
     *
     * @param source 参数
     * @return 定义的返回对象
     */
    Object execute(Object source);
}
