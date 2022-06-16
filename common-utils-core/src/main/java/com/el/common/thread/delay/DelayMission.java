package com.el.common.thread.delay;

import com.el.base.utils.support.globalization.context.GlobalizationLocalUtil;
import com.google.common.primitives.Ints;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务
 * since 5/2/22
 *
 * @author eddie
 */
@Slf4j
public abstract class DelayMission<D> implements Delayed {

    private final Long startTime;
    /**
     * 任务清单
     */
    private final D missionData;

    @Getter
    private final boolean isNeedRollBack;

    public DelayMission(D missionData, boolean isNeedRollBack,long delayInMilliseconds) {
        this.missionData = missionData;
        this.startTime = GlobalizationLocalUtil.getCurrentLocalTimeMillis() + delayInMilliseconds;
        this.isNeedRollBack = isNeedRollBack;
    }

    /**
     * 获取任务列表
     * @return      任务列表
     */
    public D getMissionData() {
        return this.missionData;
    }

    /**
     * 任务处理
     * @return  任务处理结论
     */
    protected abstract boolean handleMission();

    /**
     * 处理handleMission任务处理失败结果
     */
    public void handlerFalse(){}

    public void defaultRollback(Throwable throwable) {
        try {
            this.rollback(throwable);
        }catch (Throwable rollbackThrowable) {
            log.error("rollback exception", rollbackThrowable);
        }
    }

    public abstract void rollback(Throwable throwable);

    /**
     * 处理发生异常情况，复写自定义
     * @param e             异常
     */
    public void handlerException(Throwable e) {
        log.error("Exception occurred during mission execution", e);
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        long diff = this.startTime - GlobalizationLocalUtil.getCurrentLocalTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return Ints.saturatedCast(this.startTime - ((DelayMission<?>) o).startTime);
    }
}
