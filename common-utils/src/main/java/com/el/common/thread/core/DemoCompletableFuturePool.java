package com.el.common.thread.core;

import com.el.common.thread.core.base.AbstractCompletableFuturePool;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 使用样例
 * 2019/10/7
 *
 * @author eddielee
 */
@Slf4j
public class DemoCompletableFuturePool extends AbstractCompletableFuturePool<String> {

    public DemoCompletableFuturePool(int corePoolSize, String poolName) {
        super(corePoolSize, poolName);
    }

    @Override
    public CompletableFuture<String> initCompletableFuture(String data) {
        return CompletableFuture.supplyAsync(() -> data, super.getExecutorService())
                .thenApply(
                    source -> {
                        source = source.concat("进入第一个流程执行、");
                        System.out.println(source);
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return source;
                    })
                    //交还给ExecutorService线程池重新调度 异步调度
                .thenApplyAsync(source -> {
                        String s = source;
                        s = s.concat("进入第二个流程执行");
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(s);
                        return s;
                    })
                .whenCompleteAsync(this::abstractCallback);
    }

    @Override
    public void completableFuturesFinishCallback(CompletableFuture<String>[] futures) {
        for (CompletableFuture<String> future:futures){
            try {
                String s = future.get();
                System.out.println("最终完成:" + s);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void abstractCallback(String source, Throwable throwable) {
        if (!Objects.isNull(throwable)){
            log.error("任务执行异常", throwable);
        }
        System.out.println("每次任务执行回调" + source);
    }
}
