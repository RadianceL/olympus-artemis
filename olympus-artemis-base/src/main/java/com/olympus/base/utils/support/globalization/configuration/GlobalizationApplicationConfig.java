package com.olympus.base.utils.support.globalization.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * since 4/11/22
 *
 * @author eddie
 */
@Data
@Component
@ConfigurationProperties("spring.application.globalization")
public class GlobalizationApplicationConfig {

    /**
     * 多语言文案系统路径
     */
    private Boolean enableGlobalizationCenter;
    /**
     * 多语言文案系统路径
     */
    private String globalDocumentSystemUrl;

}
