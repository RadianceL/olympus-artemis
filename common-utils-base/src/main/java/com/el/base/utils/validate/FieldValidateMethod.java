package com.el.base.utils.validate;

import com.google.common.annotations.Beta;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 成员校验方法 <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
@Beta
public enum FieldValidateMethod implements ValidateFunction{

    /**
     * 不校验
     */
    UNCHECK{
        @Override
        public boolean validate(Object value) {
            return true;
        }
    },
    /**
     * 字符串 - 不为空
     * 前置校验 value nonNull
     */
    NOT_BLANK{
        @Override
        public boolean validate(Object value) {
            return StringUtils.isNotBlank(String.valueOf(value));
        }
    }
    ;

    @Override
    public abstract boolean validate(Object value);
}
