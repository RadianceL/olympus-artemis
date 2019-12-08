package com.el.common.time.sliding;

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

    /** 循环队列，就是装多个窗口用 */
    private volatile AtomicInteger[] timeSlices;

    /** 队列的总长度  */
    private volatile int timeSliceSize;

    /** 每个时间片的时长，以毫秒为单位 */
    private volatile int timeMillisPerSlice;

    /** 共有多少个时间片（即窗口长度） */
    private volatile int windowSize;

    /** 当前所使用的时间片位置 */
    private AtomicInteger cursor = new AtomicInteger(0);
    /**
     * 在一个完整窗口期内允许通过的最大阈值
     */
    private int threshold;

    private static final int MIN_TIME_MILLIS_PER_SLICE = 100;

    private TimeWindowSlidingDataSource timeWindowSlidingDataSource;

    public TimeWindowSliding(TimeWindowSlidingDataSource timeWindowSlidingDataSource, int timeMillisPerSlice, int windowSize, int threshold){
        this.timeWindowSlidingDataSource = timeWindowSlidingDataSource;
        this.timeMillisPerSlice = timeMillisPerSlice;
        this.windowSize = windowSize;
        this.threshold = threshold;
        // 保证存储在至少两个window
        this.timeSliceSize = windowSize * 2 + 1;
        timeWindowSlidingDataSource.initTimeSlices();
        this.verifier();
    }

    private void verifier(){
        if (Objects.isNull(timeWindowSlidingDataSource) || timeMillisPerSlice < MIN_TIME_MILLIS_PER_SLICE || windowSize > 0) {
            throw new RuntimeException("初始化异常，参数不正确");
        }
    }


    public static void main(String[] args) {
        //0.2秒一个时间片，窗口共5个
        TimeWindowSliding window = new TimeWindowSliding(TimeWindowSlidingDataSource.defaultDataSource(), 2000, 5, 5);
        for (int i = 0; i < 100; i++) {
            boolean allow = window.allow();
            System.out.println(allow);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化队列，由于此初始化会申请一些内容空间，为了节省空间，延迟初始化
     */
    private void initTimeSlices() {
        if (timeSlices != null) {
            return;
        }
        // 在多线程的情况下，会出现多次初始化的情况，没关系
        // 我们只需要保证，获取到的值一定是一个稳定的，所有这里使用先初始化，最后赋值的方法
        AtomicInteger[] localTimeSlices = new AtomicInteger[timeSliceSize];
        for (int i = 0; i < timeSliceSize; i++) {
            localTimeSlices[i] = new AtomicInteger(0);
        }
        timeSlices = localTimeSlices;
    }

    private int locationIndex() {
        long time = System.currentTimeMillis();
        return (int) ((time / timeMillisPerSlice) % timeSliceSize);
    }

    /**
     * <p>对时间片计数+1，并返回窗口中所有的计数总和
     * <p>该方法只要调用就一定会对某个时间片进行+1
     */
    public int incrementAndSum() {
        initTimeSlices();
        int index = locationIndex();
        int sum = 0;
        // cursor等于index，返回true
        // cursor不等于index，返回false，并会将cursor设置为index
        int oldCursor = cursor.getAndSet(index);
        if (oldCursor == index) {
            // 在当前时间片里继续+1
            sum += timeSlices[index].incrementAndGet();
        } else {
            // 可能有其他thread已经置过1，问题不大
            timeSlices[index].set(1);
            // 清零，访问量不大时会有时间片跳跃的情况
            clearBetween(oldCursor, index);
            // sum += 0;
        }
        for (int i = 1; i < windowSize; i++) {
            sum += timeSlices[(index - i + timeSliceSize) % timeSliceSize].get();
        }
        return sum;
    }

    /**
     * 判断是否允许进行访问，未超过阈值的话才会对某个时间片+1
     */
    public boolean allow() {
        initTimeSlices();
        int index = locationIndex();
        int sum = 0;
        // cursor不等于index，将cursor设置为index
        int oldCursor = cursor.getAndSet(index);
        if (oldCursor != index) {
            // 可能有其他thread已经置过1，问题不大
            timeSlices[index].set(0);
            // 清零，访问量不大时会有时间片跳跃的情况
            clearBetween(oldCursor, index);
        }
        for (int i = 1; i < windowSize; i++) {
            sum += timeSlices[(index - i + timeSliceSize) % timeSliceSize].get();
        }

        // 阈值判断
        if (sum <= threshold) {
            // 未超过阈值才+1
            timeSlices[index].incrementAndGet();
            return true;
        }
        return false;
    }

    /**
     * <p>将fromIndex~toIndex之间的时间片计数都清零
     * <p>极端情况下，当循环队列已经走了超过1个timeSliceSize以上，这里的清零并不能如期望的进行
     *
     * @param fromIndex
     *         不包含
     * @param toIndex
     *         不包含
     */
    private void clearBetween(int fromIndex, int toIndex) {
        for (int index = (fromIndex + 1) % timeSliceSize; index != toIndex; index = (index + 1) % timeSliceSize) {
            timeSlices[index].set(0);
        }
    }
}
