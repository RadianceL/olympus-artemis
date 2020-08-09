package com.el.common.support;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 基础日志类
 * since 7/4/20
 *
 * @author eddie
 */
public class ErrorsEventLogger {

    private static final String ROLLING_PATTERN = ".%d{yyyy-MM-dd}.%i";

    private static final String DEFAULT_LOGGER_PATTERN = "%d{yyyy-MM-dd HH:mm:ss} - %msg%n";

    public static Logger getInstance() {
        return SingletonHolder.instance;
    }

    private static Logger build() {
        String logFile = buildLogPath();

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = (Logger) LoggerFactory.getLogger(Constant.LOGGER_NAME);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(DEFAULT_LOGGER_PATTERN);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();

        // rollingAppender
        RollingFileAppender<ILoggingEvent> rollingAppender = new RollingFileAppender<>();
        rollingAppender.setContext(context);
        rollingAppender.setEncoder(encoder);
        String logFilePath = logFile + File.separator + Constant.LOGGER_NAME + ".log";
        rollingAppender.setFile(logFilePath);

        // 滚动策略
        SizeAndTimeBasedRollingPolicy<?> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setFileNamePattern(logFile + ROLLING_PATTERN);
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.setMaxFileSize(FileSize.valueOf("200mb"));
        rollingPolicy.setTotalSizeCap(FileSize.valueOf("2048mb"));
        rollingPolicy.setParent(rollingAppender);
        rollingPolicy.start();

        rollingAppender.setRollingPolicy(rollingPolicy);
        rollingAppender.start();

        // 异步Appender
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(context);
        asyncAppender.setName(Constant.LOGGER_NAME);

        asyncAppender.setQueueSize(512);
        asyncAppender.setDiscardingThreshold(0);

        asyncAppender.addAppender(rollingAppender);
        asyncAppender.start();

        logger.setLevel(Level.ERROR);
        logger.addAppender(asyncAppender);

        return logger;
    }

    private static String buildLogPath() {
        String property = System.getProperty("user.dir");
        return property.concat(File.separator).concat("errors.log");
    }

    private static class SingletonHolder {
        private static final Logger instance = build();
    }
}
