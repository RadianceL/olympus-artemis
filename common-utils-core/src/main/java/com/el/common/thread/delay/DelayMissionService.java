package com.el.common.thread.delay;

import java.util.concurrent.DelayQueue;

/**
 * 延时任务
 * since 5/2/22
 *
 * @author eddie
 */
public abstract class DelayMissionService<D extends DelayMission<?>> {

    /**
     * 延时队列
     */
    private final DelayQueue<D> coreDelayBlockingQueue = new DelayQueue<>();

    public DelayQueue<D> getCoreDelayBlockingQueue() {
        return this.coreDelayBlockingQueue;
    }

    public boolean addMission(D delayMission) {
        return coreDelayBlockingQueue.add(delayMission);
    }

    abstract boolean rollback();

    abstract String getMissionName();
}
