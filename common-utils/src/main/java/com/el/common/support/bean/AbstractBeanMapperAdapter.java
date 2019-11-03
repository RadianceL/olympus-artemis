package com.el.common.support.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 对象映射工具
 * 2019/10/17
 *
 * @author eddielee
 */
public abstract class AbstractBeanMapperAdapter {

    /**
     * 内部对象差异字段映射
     */
    private Map<String, Map<String, String>> innerObjectDiffMapper = new HashMap<>(8);

    /**
     * 对象差异字段映射
     */
    private Map<String, String> objectDiffMapper = new HashMap<>(16);

    public static <T>  T conver(Object sourceObject, Class<T> targetClass, Map<String, String> diff){
        return null;
    }

}
