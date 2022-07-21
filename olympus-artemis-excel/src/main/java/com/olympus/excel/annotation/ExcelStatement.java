package com.olympus.excel.annotation;

import java.lang.annotation.*;

/**
 * 声明该对象为Excel对象 </br>
 * since 2020/8/9
 * @author eddie.lys
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelStatement {

    String fileName() default "";

    String[] sheet() default {""};

}
