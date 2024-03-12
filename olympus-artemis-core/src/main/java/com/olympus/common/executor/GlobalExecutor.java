package com.olympus.common.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局任务提交执行器
 */
public class GlobalExecutor {

    public static void submitDistroNotifyTask(List<Runnable> runnableTasks, long timeout) throws InterruptedException {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("GLOBAL_EXECUTOR".concat("-thread-%d")).build();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(availableProcessors, availableProcessors, 5L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(16), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        try {
            runnableTasks.forEach(threadPoolExecutor::submit);
            threadPoolExecutor.shutdown();
            if (!threadPoolExecutor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow();
            } else {
                threadPoolExecutor.shutdown();
            }
        } finally {
            threadPoolExecutor.shutdown();
            Thread.currentThread().interrupt();
        }
    }
}
