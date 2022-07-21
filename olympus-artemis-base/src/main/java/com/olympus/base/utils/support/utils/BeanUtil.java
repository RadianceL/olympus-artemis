package com.olympus.base.utils.support.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 配置读取工具
 *
 * since 2019/03/13
 * @author eddie
 */
public class BeanUtil {

    /**
     * Bean对象是否包含空Field
     */
    public static boolean hasNull(Object obj) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(obj) == null) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 哪个Field值为null
     */
    private static List<String> whereIsNull(Object obj) {
        List<String> fields = new ArrayList<>();
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object o = f.get(obj);
                if (o == null) {
                    String fieldName = f.getName();
                    fields.add(fieldName);
                }
                if (o instanceof String) {
                    if (StringUtils.isBlank((String) o)) {
                        String fieldName = f.getName();
                        fields.add(fieldName);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fields;
    }

    /**
     * 哪个Field值为null
     */
    public static List<String> whereJsonIsNull(JSONObject obj, List<String> notNullField) {
        Set<String> keys = obj.keySet();
        notNullField.removeAll(keys);
        return notNullField;
    }

    /**
     * 空值中是否包含必填项
     *
     * @param object       需要被校验的实体
     * @param notNullField 不能为空的字段名\
     */
    public static List<String> notNullField(Object object, List<String> notNullField) {
        List<String> nullFields = whereIsNull(object);
        nullFields.retainAll(notNullField);
        return nullFields;
    }

    public static <T> T mapToObject(Map<String, String> map, Class<T> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                field.set(obj, map.get(field.getName()));
            }
        }
        return (T) obj;
    }

}
