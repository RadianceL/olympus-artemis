package com.el.common.thread.delay;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Delayed;

/**
 * 延时任务
 * since 5/2/22
 *
 * @author eddie
 */
@Slf4j
public abstract class DelayMission<D> implements Delayed {

    /**
     * 任务清单
     */
    private final List<D> missionDataList;

    public DelayMission(List<D> missionDataList) {
        this.missionDataList = missionDataList;
    }

    /**
     * 获取任务列表
     * @return      任务列表
     */
    public List<D> getMissionDataList() {
        return this.missionDataList;
    }

    /**
     * 任务处理
     * @return  任务处理结论
     */
    abstract boolean handleMission();

    /**
     * 处理handleMission任务处理失败结果
     */
    public void handlerFalse(){}

    /**
     * 处理发生异常情况，复写自定义
     * @param e             异常
     */
    public void handlerException(Throwable e) {
        log.error("Exception occurred during mission execution", e);
    }
}
