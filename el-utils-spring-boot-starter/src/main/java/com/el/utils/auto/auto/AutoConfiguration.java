package com.el.utils.auto.auto;

import com.el.common.time.annotation.active.aspect.TimeCalculationProcess;
import lombok.extern.slf4j.Slf4j;
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
public class AutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.el-util", name = "enable-time-calculation")
    public TimeCalculationProcess timeCalculationProcess() {
        log.debug("加载接口时间统计器");
        return new TimeCalculationProcess();
    }

}
