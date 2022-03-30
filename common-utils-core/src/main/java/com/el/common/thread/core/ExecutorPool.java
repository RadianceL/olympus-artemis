package com.el.common.thread.core;

import com.el.base.utils.support.globalization.ErrorMessage;
import com.el.common.thread.excepion.ThreadInterruptedException;
import com.el.common.thread.model.WorkAction;
import com.el.common.thread.model.WorkCallAction;
import com.el.common.thread.model.WorkWithoutSourceAction;
import com.el.common.thread.status.LifeCycle;
import com.el.common.thread.status.LifeCycleStatus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 线程池封装方法
 * 2019-01-17
 *
 * @author eddielee
 */
@Slf4j
public class ExecutorPool<T, R> implements LifeCycle {

    /**
     * 线程池
     */
    private final ExecutorService service;

    /**
     * 信号锁
     */
    private final Semaphore semaphore;

    /**
     * MAX线程因子
     */
    private static final int FACTOR = 5;

    /**
     * 私有构造函数
     *
     * @param threadTotal 核心线程数
     * @param threadLimit 同时运行线程数
     * @param poolName    线程池名称
     */
    public ExecutorPool(int threadTotal, int threadLimit, String poolName) {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(poolName.concat("-thread-%d")).build();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.service = new ThreadPoolExecutor(threadTotal, availableProcessors * FACTOR, 5L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(256), factory, new ThreadPoolExecutor.AbortPolicy());
        //初始化信号量
        this.semaphore = new Semaphore(threadLimit);
    }

    /**
     * 执行工作
     *
     * @param command lambda表达式 -> 任务
     */
    public void executeWork(WorkWithoutSourceAction command) {
        service.execute(() -> {
            try {
                semaphore.acquire();
                command.execute();
            } catch (InterruptedException e) {
                this.shutDown();
                throw new ThreadInterruptedException(ErrorMessage.of("UTIL-THREAD-000-01", e.getMessage()));
            } finally {
                semaphore.release();
            }
        });
    }

    /**
     * 执行工作
     *
     * @param command lambda表达式 -> 任务
     * @param source  任务内容
     */
    public void executeWork(WorkAction<T> command, T source) {
        service.execute(() -> {
            try {
                semaphore.acquire();
                command.execute(source);
            } catch (InterruptedException e) {
                this.shutDown();
                throw new ThreadInterruptedException(ErrorMessage.of("UTIL-THREAD-000-01", e.getMessage()));
            } finally {
                semaphore.release();
            }
        });
    }

    /**
     * 有返回值的线程模型
     * 这个设计模式 我觉得主要的使用场景是在bio上面
     * 用来解决网络io和cpu之间的阻抗不匹配的问题
     * @param action            执行任务逻辑
     * @param sources           需要处理的对象
     * @return                  Future结果集
     */
    public List<Future<R>> submitWork(WorkCallAction<T, R> action, List<T> sources) {
        List<Future<R>> resultObjects = new ArrayList<>();
        for (T o : sources) {
            Future<R> submit = service.submit(() -> {
                try {
                    semaphore.acquire();
                    return action.execute(o);
                } catch (InterruptedException e) {
                    this.shutDown();
                    throw new ThreadInterruptedException(ErrorMessage.of("UTIL-THREAD-000-01", e.getMessage()));
                } finally {
                    semaphore.release();
                }
            });
            resultObjects.add(submit);
        }
        return resultObjects;
    }

    /**
     * 每个Object都是单独线程来执行
     * @param action        执行逻辑
     * @param sources       需要处理的对象
     * @return              结果集
     */
    @SneakyThrows
    public List<R> runBackgroundCommand(WorkCallAction<T, R> action, List<T> sources) {
        final CountDownLatch countDownLatch = new CountDownLatch(sources.size());
        List<R> resultObjects = new CopyOnWriteArrayList<>();
        for (T o : sources) {
            service.execute(() -> {
                try {
                    semaphore.acquire();
                    resultObjects.add(action.execute(o));
                } catch (InterruptedException e) {
                    this.shutDown();
                    throw new ThreadInterruptedException(ErrorMessage.of("UTIL-THREAD-000-01", e.getMessage()));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        return resultObjects;
    }

    /**
     * 关闭线程池
     */
    @Override
    public void shutDown() {
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
     *
     * @return
     */
    @Override
    public LifeCycleStatus getStatus() {
        return service.isShutdown() ? LifeCycleStatus.STOPED : LifeCycleStatus.RUNNING;
    }

}
