package com.el.common.support.bean.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段定义
 * 2019/10/20
 *
 * @author eddielee
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinition {

    /**
     * 类型
     */
    private Class<?> typeClassName;

    /**
     * 名称
     */
    private String fieldName;

    public FieldDefinition(String fieldName) {
        this.fieldName = fieldName;
    }
}
