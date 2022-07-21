package com.olympus.base.utils.validate;

import lombok.Getter;

/**
 * 成员校验不匹配异常 <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
public class ValidateMismatchException extends Exception{

    @Getter
    private final String targetField;

    @Getter
    private final String message;

    @Getter
    private final Class<?> clazz;

    public ValidateMismatchException(Class<?> clazz, String targetField, String message) {
        super(targetField);
        this.targetField = targetField;
        this.message = message;
        this.clazz = clazz;
    }

}
