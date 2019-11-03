package com.el.common.time.annotation;

import java.lang.annotation.*;

/**
 * 时间计数器
 * 被该注解标记的方法将会统计执行时间
 *
 * 实现原理 -> 通过spring 切面激活注解
 * 2019/10/5
 *
 * @author eddielee
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeCalculation {

    /**
     * 方法描述
     * @return
     */
    String value() default "";

}
