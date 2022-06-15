package com.el.utils.auto.auto;

import com.el.base.utils.support.globalization.configuration.GlobalizationApplicationConfig;
import com.el.base.utils.support.globalization.configuration.GlobalizationAutoConfiguration;
import com.el.common.thread.core.ExecutorPool;
import com.el.utils.auto.configuration.CommonUtilProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置中心
 * since 2020/2/18
 *
 * @author eddie
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.el-util", name = "enable-auto-config")
public class AutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.el-util", name = "enable-thread-pool")
    public <T, R> ExecutorPool<T, R> executorPool(CommonUtilProperties commonUtilProperties) {
        log.info("启用 - 工程线程池");
        int coreThreadTotal = commonUtilProperties.getThreadPoolConfig().getCoreThreadTotal();
        int maxThreadLimit = commonUtilProperties.getThreadPoolConfig().getMaxThreadLimit();
        String poolName = commonUtilProperties.getThreadPoolConfig().getPoolName();

        if (coreThreadTotal <= 0) {
            coreThreadTotal = 1;
        }

        if (maxThreadLimit < 4) {
            maxThreadLimit = 4;
        }

        if (StringUtils.isBlank(poolName)) {
            poolName = "application";
        }

        return new ExecutorPool<>(coreThreadTotal, maxThreadLimit, poolName);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.application.globalization", name = "enableGlobalizationCenter")
    public GlobalizationAutoConfiguration globalizationAutoConfiguration(GlobalizationApplicationConfig globalizationApplicationConfig) {
        return new GlobalizationAutoConfiguration(globalizationApplicationConfig);
    }
}
