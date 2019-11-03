package com.el.utils.auto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 2019/10/7
 *
 * @author eddielee
 */
@SpringBootApplication
@EnableConfigurationProperties
public class ElUtilApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElUtilApplication.class, args);
    }

}
