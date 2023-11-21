package com.olympus.common.thread.status;


import java.util.List;

/**
 * 容器生命周期方法
 * 2019-05-26
 *
 * @author eddielee
 */
public interface LifeCycle {

    /**
     * 停止服务 停止生产 等待消费完成
     */
    void shutDown();

    /**
     * 获取当前运行状态
     */
    LifeCycleStatus getStatus();

    /**
     * 停止服务 停止生产 立即结束 返回剩余待执行任务
     */
    List<Runnable> shutdownNow() throws InterruptedException;
}
