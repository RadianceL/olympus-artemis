package com.el.common.time.sliding;

import com.el.base.utils.support.globalization.context.GlobalizationLocalUtil;
import com.el.common.time.sliding.data.TimeWindowSlidingDataSource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间窗滑块
 * since 2019/12/8
 *
 * @author eddie
 */
public class TimeWindowSliding {

    /**
     * 队列的总长度
     */
    private final int timeSliceSize;

    /**
     * 每个时间片的时长，以毫秒为单位
     */
    private final int timeMillisPerSlice;

    /**
     * 当前所使用的时间片位置
     */
    private final AtomicInteger cursor = new AtomicInteger(0);

    /**
     * 在一个完整窗口期内允许通过的最大阈值
     */
    private final int threshold;

    private final int windowSize;

    /**
     * 最小每个时间片的时长，以毫秒为单位
     */
    private static final int MIN_TIME_MILLIS_PER_SLICE = 50;

    /**
     * 最小窗口数量
     */
    private static final int DEFAULT_WINDOW_SIZE = 5;

    /**
     * 数据存储
     */
    private final TimeWindowSlidingDataSource timeWindowSlidingDataSource;

    public TimeWindowSliding(TimeWindowSlidingDataSource timeWindowSlidingDataSource, int windowSize, int timeMillisPerSlice, int threshold) {
        this.timeWindowSlidingDataSource = timeWindowSlidingDataSource;
        this.timeMillisPerSlice = timeMillisPerSlice;
        this.threshold = threshold;
        /* 低于一定窗口个数会丢失精准度 */
        this.windowSize = Math.max(windowSize, DEFAULT_WINDOW_SIZE);
        /* 保证每个时间窗至少有2个窗口存储 不会重叠 */
        this.timeSliceSize = this.windowSize * 2 + 1;
        /* 可以忽略这个操作 数据存储结构中定义的生命周期函数 如果接口有实现会调用 没有实现走默认实现直接return */
        timeWindowSlidingDataSource.initTimeSlices();
        /* 初始化参数校验 */
        this.verifier();
    }

    private void verifier() {
        if (Objects.isNull(timeWindowSlidingDataSource) || timeMillisPerSlice < MIN_TIME_MILLIS_PER_SLICE || threshold <= 0) {
            throw new RuntimeException("初始化异常，参数不正确");
        }
    }


    public static void main(String[] args) {
        //0.2秒一个时间片，窗口共5个
        TimeWindowSliding window = new TimeWindowSliding(TimeWindowSlidingDataSource.defaultDataSource(), 5, 200, 5);
        for (int i = 0; i < 1000; i++) {
            boolean allow = window.allowLimitTimes("a1");
            System.out.println(allow);
        }
    }

    /**
     * 判断是否允许进行访问，未超过阈值的话才会对某个时间片+1
     */
    public synchronized boolean allowLimitTimes(String key) {
        int index = locationIndex();
        int sum = 0;
        // cursor不等于index，将cursor设置为index
        int oldCursor = cursor.getAndSet(index);
        if (oldCursor != index) {
            // 清零，访问量不大时会有时间片跳跃的情况
            clearBetween(oldCursor, index);
        }
        for (int i = 1; i < timeSliceSize; i++) {
            sum += timeWindowSlidingDataSource.getAllocAdoptRecordTimes(i, key);
        }

        // 阈值判断
        if (sum < threshold) {
            // 未超过阈值才+1
            this.timeWindowSlidingDataSource.allocAdoptRecord(index, key);
            return true;
        }
        return false;
    }

    /**
     * 返回平均每秒访问次数
     */
    public int allowNotLimitPerMin(String key) {
        int index = locationIndex();
        int sum = 0;
        int nextIndex = index + 1;
        this.timeWindowSlidingDataSource.clearSingle(nextIndex);
        int from = index, to = index;
        if (index < windowSize) {
            from += windowSize + 1;
            to += 2 * windowSize;
        } else {
            from = index - windowSize + 1;
        }
        while (from <= to) {
            int targetIndex = from;
            if (from >= timeSliceSize) {
                targetIndex = from - 2 * windowSize;
            }
            sum += timeWindowSlidingDataSource.getAllocAdoptRecordTimes(targetIndex, key);
            from++;
        }
        this.timeWindowSlidingDataSource.allocAdoptRecord(index, key);
        return (sum + 1) / windowSize;
    }

    /**
     * 返回每秒访问次数
     */
    public int allowNotLimit(String key) {
        int index = locationIndex();
        int sum = 0;
        // cursor不等于index，将cursor设置为index
        int oldCursor = cursor.getAndSet(index);
        if (oldCursor != index) {
            // 清零，访问量不大时会有时间片跳跃的情况
            clearBetween(oldCursor, index);
        }
        for (int i = 0; i <= timeSliceSize; i++) {
            sum += timeWindowSlidingDataSource.getAllocAdoptRecordTimes(i, key);
        }
        this.timeWindowSlidingDataSource.allocAdoptRecord(index, key);
        return sum + 1;
    }

    /**
     * <p>将fromIndex~toIndex之间的时间片计数都清零
     * <p>极端情况下，当循环队列已经走了超过1个timeSliceSize以上，这里的清零并不能如期望的进行
     */
    private void clearBetween(int fromIndex, int toIndex) {
        this.timeWindowSlidingDataSource.clearBetween(fromIndex, toIndex, timeSliceSize);
    }

    private int locationIndex() {
        long time = GlobalizationLocalUtil.getCurrentLocalTimeMillis();
        return (int) ((time / timeMillisPerSlice) % timeSliceSize);
    }
}
