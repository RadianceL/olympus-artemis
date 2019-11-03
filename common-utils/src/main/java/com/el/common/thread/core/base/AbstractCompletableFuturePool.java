package com.el.common.thread.core.base;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * 步骤执行线程池
 * 2019/10/5
 *
 * @author eddielee
 */
public abstract class AbstractCompletableFuturePool<T extends Comparable<T>> {

    /**
     * MAX线程因子
     */
    private static final int DEFAULT_FACTOR = 5;

    /**
     * 存活线程因子
     */
    private static final long KEEP_ALIVE_FACTOR = 2L;

    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;

    /**
     * 线程池
     */
    private ExecutorService executorService;

    private static final int AVAILABLE_PROCESSORS;

    static {
        AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    }

    public AbstractCompletableFuturePool(int corePoolSize, String poolName){
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(poolName.concat("-Thread-%d")).build();

        if (corePoolSize <= AVAILABLE_PROCESSORS){
            corePoolSize = DEFAULT_CORE_POOL_SIZE;
        }
        executorService = new ThreadPoolExecutor(corePoolSize, AVAILABLE_PROCESSORS * DEFAULT_FACTOR, corePoolSize * KEEP_ALIVE_FACTOR,
                TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(256), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    @SuppressWarnings("rawtypes")
    public void doCompletableFutures(List<T> conditions){
        final List<T> syncConditions = Collections.synchronizedList(conditions);
        CompletableFuture[] futures = syncConditions.stream()
                .map(this::initCompletableFuture)
                .toArray(CompletableFuture[]::new);
        //阻塞，直到所有任务结束。
        CompletableFuture.allOf(futures).join();
        completableFuturesFinishCallback(futures);
    }

    /**
     * 初始化线程池
     * @param data  需要处理的数据
     * @return
     */
    public abstract CompletableFuture<T> initCompletableFuture(T data);

    /**
     * 执行完毕后回调
     * @param futures
     */
    public abstract void completableFuturesFinishCallback(CompletableFuture<T>[] futures);

    /**
     * 可选方法 手动覆写 在initCompletableFuture中调用
     * @param source        本次处理的参数
     * @param throwable     如对该参数的处理出现异常
     */
    public void abstractCallback(T source, Throwable throwable){
        /*
            do something
         */
    }

    /**
     * 关停线程池 注意关闭会等待线程执行完毕
     */
    public void shutdown() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ExecutorService getExecutorService(){
        return this.executorService;
    }
}
