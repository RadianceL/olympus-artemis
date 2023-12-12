package com.olympus.utils.auto.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * 核心配置容器
 * 2019/10/7
 *
 * @author eddielee
 */
@Slf4j
@Data
@Component
@ConfigurationProperties("spring.el-util")
public class CommonUtilProperties {
    /**
     * 是否开启
     */
    private Boolean enableAutoConfig;
    /**
     * 是否开启接口执行时间统计
     */
    private Boolean enableTimeCalculation;
    /**
     * 是否开启线程池
     */
    private Boolean enableThreadPool;
    /**
     * 线程池配置
     */
    @NestedConfigurationProperty
    private final ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();

    @Data
    public static class ThreadPoolConfig {
        private int coreThreadTotal;
        private int maxThreadLimit;
        private String poolName;
    }
}
