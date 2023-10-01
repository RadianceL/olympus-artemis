package com.olympus.base.utils.support.utils;


import com.olympus.base.utils.collection.CollectionUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 * since 2020/10/14
 *
 * @author eddie.lys
 */
public class BeanCopierUtil {

    /**
     * BeanCopier的copy
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copy(Object source, Object target) {
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        beanCopier.copy(source, target, null);
    }

    /**
     * 拷贝List
     * @param sourceList        源list
     * @param targetClass       目标list 对象体
     * @return                  目标对象list
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }

        List<T> resultList = new ArrayList<>();
        for (Object source : sourceList) {
            T copy = BeanCopierUtil.copy(source, targetClass);
            resultList.add(copy);
        }

        return resultList;
    }

    /**
     * BeanCopier的copy
     * @param source 源对象
     * @param targetClass 目标对象
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), targetClass, false);
        try {
            T o = targetClass.getDeclaredConstructor().newInstance();
            beanCopier.copy(source, o, null);
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
