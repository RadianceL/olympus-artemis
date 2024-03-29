package com.olympus.common.thread.delay;

import com.olympus.common.thread.core.ExecutorPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务队列控制器
 * since 5/1/22
 *
 * @author eddie
 */
@Slf4j
public class DelayMissionQueueControl {

    /**
     * 任务收集器
     */
    private final Map<String, DelayMissionService<DelayMission<?>>> concurrentHashMapMap = new ConcurrentHashMap<>(8);
    /**
     * 线程池
     */
    private final ExecutorPool<DelayMissionService<DelayMission<?>>, Void> executorPool = new ExecutorPool<>(2, 8, "poolName");

    /**
     * 对外暴露 任务注册
     *
     * @param delayMissionService 延时服务
     */
    public void registerDelayMission(DelayMissionService<DelayMission<?>> delayMissionService) throws IllegalArgumentException{
        if (concurrentHashMapMap.containsKey(delayMissionService.getMissionName())) {
            throw new IllegalArgumentException("this mission already registered");
        }
        DelayMissionService<DelayMission<?>> delayMissionDelayMissionService =
                this.concurrentHashMapMap.putIfAbsent(delayMissionService.getMissionName(), delayMissionService);
        if (Objects.nonNull(delayMissionDelayMissionService)) {
            throw new IllegalArgumentException("this mission already registered");
        }
        delayMissionOnMonitor(delayMissionService);
    }

    public DelayMissionService<DelayMission<?>> getDelayMissionService(String missionName) {
        return this.concurrentHashMapMap.get(missionName);
    }

    /**
     * 触发任务监听
     * @param delayMissionService       延时任务服务
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private void delayMissionOnMonitor(DelayMissionService<DelayMission<?>> delayMissionService) {
        executorPool.executeWork(source -> {
            DelayQueue<DelayMission<?>> coreDelayBlockingQueue = source.getCoreDelayBlockingQueue();
            while (true) {
                DelayMission<?> delayMission = coreDelayBlockingQueue.poll();
                if (Objects.isNull(delayMission)) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error("mission on monitor thread sleep error", e);
                    }
                }else {
                    try {
                        boolean handleMission = delayMission.handleMission();
                        if (!handleMission) {
                            delayMission.handlerFalse();
                            if (delayMission.isNeedRollBack()) {
                                delayMission.defaultRollback(null);
                            }
                        }
                    }catch (Throwable throwable) {
                        delayMission.handlerException(throwable);
                        if (delayMission.isNeedRollBack()) {
                            delayMission.defaultRollback(throwable);
                        }

                    }
                }
            }
        }, delayMissionService);
    }

}
