package com.el.common.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author ${USER}
 * @since ${DATE}
 */
public class GlobalExecutor {

    /**
     * 线程池
     */
    private static final ExecutorService EXECUTOR_SERVICE;

    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("GLOBAL_EXECUTOR".concat("-thread-%d")).build();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        EXECUTOR_SERVICE = new ThreadPoolExecutor(availableProcessors, availableProcessors, 5L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(16), factory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static void submitDistroNotifyTask(Runnable runnable) {
        EXECUTOR_SERVICE.submit(runnable);
    }

}
