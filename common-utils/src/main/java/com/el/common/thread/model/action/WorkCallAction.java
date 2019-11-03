package com.el.common.thread.model.action;

/**
 * 有返回值的工作
 * 2019/10/7
 *
 * @author eddielee
 */
@FunctionalInterface
public interface WorkCallAction<T, V> {

    /**
     * 执行工作 有返回值
     * @param source    参数
     * @return          定义的返回对象
     */
    V execute(T source);
}
