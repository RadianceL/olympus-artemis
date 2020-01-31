package com.el.common.thread.core;

import com.el.common.thread.exceptions.BasicBoundedBufferException;
import com.el.common.thread.model.action.WorkAction;
import com.el.common.thread.model.action.WorkCallAction;
import com.el.common.thread.status.LifeCycle;
import com.el.common.thread.status.LifeCycleStatus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * 线程池封装方法
 * 2019-01-17
 *
 * @author eddielee
 */
@Slf4j
public class ExecutorPool<T, V> implements LifeCycle {

    /**
     * 线程池
     */
    private ExecutorService service;

    /**
     * 信号锁
     */
    private Semaphore semaphore;

    /**
     * MAX线程因子
     */
    private static final int FACTOR = 5;

    /**
     * 私有构造函数
     * @param threadTotal   核心线程数
     * @param poolName      线程池名称
     */
    public ExecutorPool(int threadTotal, String poolName){
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(poolName.concat("-Thread-%d")).build();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.service = new ThreadPoolExecutor(threadTotal, availableProcessors * FACTOR, 5L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(256), factory, new ThreadPoolExecutor.AbortPolicy());
        //初始化信号量
        this.semaphore = new Semaphore(threadTotal);
    }

    /**
     * 执行工作
     * @param action    lambda表达式 -> 任务
     * @param source    任务内容
     */
    public void executeWork(WorkAction<T> action, T source) {
        try {
            service.execute(() -> {
                try {
                    semaphore.acquire();
                    action.execute(source);
                } catch (InterruptedException e) {
                    throw new BasicBoundedBufferException("线程异常: {}", e.getMessage());
                }finally {
                    semaphore.release();
                }
            });
        }catch (BasicBoundedBufferException e){
            this.shutDown();
            throw new BasicBoundedBufferException(e);
        }
    }

    public Future<V> submitWork(WorkCallAction<T, V> action, T source) {
        try {
            return service.submit(() -> {
                try {
                    semaphore.acquire();
                    return action.execute(source);
                } catch (InterruptedException e) {
                    throw new BasicBoundedBufferException("线程异常: {}", e.getMessage());
                } finally {
                    semaphore.release();
                }
            });
        }catch (BasicBoundedBufferException e){
            this.shutDown();
            throw new BasicBoundedBufferException(e);
        }
    }

    /**
     * 关闭线程池
     */
    @Override
    public void shutDown(){
        try {
            service.shutdown();
            service.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("线程池关闭异常", e);
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        return service.shutdownNow();
    }

    /**
     * 线程池是否关闭状态
     * @return
     */
    @Override
    public LifeCycleStatus getStatus() {
        return service.isShutdown() ? LifeCycleStatus.STOPED : LifeCycleStatus.RUNNING;
    }

    @Override
    public void start() {
        //无此方法此方法
    }

    @Override
    public void stop() {
        //无此方法
    }
}
