package com.olympus.common.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.List;
import java.util.concurrent.*;

/**
 * 全局任务提交执行器
 */
public class GlobalExecutor {

    public static void submitDistroNotifyTask(List<Runnable> runnableTasks, long timeout) {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("GLOBAL_EXECUTOR".concat("-thread-%d")).build();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(availableProcessors, availableProcessors, 5L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(16), factory, new ThreadPoolExecutor.AbortPolicy());

        runnableTasks.forEach(threadPoolExecutor::submit);
        threadPoolExecutor.shutdown();
        try {
            if (!threadPoolExecutor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow();
            }else {
                threadPoolExecutor.shutdown();
            }
        } catch (InterruptedException e) {
            threadPoolExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
