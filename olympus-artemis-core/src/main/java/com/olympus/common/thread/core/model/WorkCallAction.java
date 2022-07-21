package com.olympus.common.thread.core.model;

/**
 * 有返回值的任务
 * 2019/10/7
 *
 * @author eddielee
 */
@FunctionalInterface
public interface WorkCallAction<T, R> {

    /**
     * 执行工作 有返回值
     *
     * @param source 参数
     * @return 定义的返回对象
     */
    R execute(T source);
}
