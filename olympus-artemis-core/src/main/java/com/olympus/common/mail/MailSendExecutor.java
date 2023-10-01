package com.olympus.common.mail;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.olympus.common.executor.GlobalExecutor;
import com.olympus.common.mail.data.MailContext;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;

import java.util.concurrent.*;

/**
 * @author eddie
 * @since 2020/12/20
 */
@Slf4j
public abstract class MailSendExecutor implements Runnable {

    private final BlockingQueue<Pair<String, MailContext>> tasks = new ArrayBlockingQueue<>(1024 * 1024);

    public MailSendExecutor() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("Mail-Send-Executor".concat("-thread-%d")).build();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(availableProcessors, availableProcessors, 5L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(16), factory, new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.submit(this);
    }

    public boolean addMission(boolean sync, String traceId, MailContext mission) {
        Pair<String, MailContext> pair = new Pair<>(traceId, mission);
        if (sync) {
            handle(pair);
        } else {
            tasks.add(pair);
        }
        return true;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                Pair<String, MailContext> pair = tasks.take();
                handle(pair);
            } catch (Throwable e) {
                log.error("异常", e);
            }
        }
    }

    /**
     * 邮件发送
     *
     * @param mission 邮件内容
     */
    public abstract void handle(Pair<String, MailContext> mission);
}
