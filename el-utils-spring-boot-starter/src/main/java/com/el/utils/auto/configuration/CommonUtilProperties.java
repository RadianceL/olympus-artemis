package com.el.utils.auto.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
     * 是否开启
     */
    private Boolean enableTimeCalculation;

}
