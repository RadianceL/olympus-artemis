package com.el.base.utils.validate;

import com.google.common.annotations.Beta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 成员校验声明 <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
@Beta
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldValidate {

    /**
     * 默认不校验
     * @return 校验方法 {@link FieldValidateMethod}
     */
    FieldValidateMethod value() default FieldValidateMethod.UNCHECK;

}
