package com.el.common.support.bean.exceptions;

/**
 * Bean字段描述为空异常
 * 2019/10/20
 *
 * @author eddielee
 */
public class BeanFieldDescriptionIsEmptyException extends RuntimeException {

    public BeanFieldDescriptionIsEmptyException() {
    }

    public BeanFieldDescriptionIsEmptyException(String message) {
        super(message);
    }

    public BeanFieldDescriptionIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
