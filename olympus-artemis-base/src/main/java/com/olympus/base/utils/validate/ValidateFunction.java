package com.olympus.base.utils.validate;

/**
 * 校验 <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
public interface ValidateFunction {

    /**
     * 校验
     * @param value 数据
     * @return      是否符合要求
     */
    boolean validate(Object value);

}
