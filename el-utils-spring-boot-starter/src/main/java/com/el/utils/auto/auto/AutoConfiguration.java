package com.el.utils.auto.auto;

import com.el.common.thread.core.ExecutorPool;
import com.el.common.time.annotation.active.aspect.TimeCalculationProcess;
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
    @ConditionalOnProperty(prefix = "spring.el-util", name = "enable-time-calculation")
    public TimeCalculationProcess timeCalculationProcess() {
        log.info("启用 - 接口调用时间统计器");
        return new TimeCalculationProcess();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.el-util", name = "enable-thread-pool")
    public ExecutorPool executorPool(CommonUtilProperties commonUtilProperties) {
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

        return new ExecutorPool(coreThreadTotal, maxThreadLimit, poolName);
    }
}
