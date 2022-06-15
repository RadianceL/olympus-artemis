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

    public void addMission(D delayMission) {
        boolean addDelayBlockingQueue = coreDelayBlockingQueue.add(delayMission);
        if (!addDelayBlockingQueue) {
            delayMission.handlerException(new RuntimeException(""));
        }
    }

    public abstract String getMissionName();
}
