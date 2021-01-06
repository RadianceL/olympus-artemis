package com.el.base.utils.support.utils;


import org.mockito.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <br/>
 * since 2020/10/14
 *
 * @author eddie.lys
 */
public class BeanCopierUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanCopierUtil.class);

    /**
     * BeanCopier的缓存
     */
    static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();

    /**
     * BeanCopier的copy
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copy(Object source, Object target) {
        String key = genKey(source.getClass(), target.getClass());
        BeanCopier beanCopier;
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            beanCopier = BEAN_COPIER_CACHE.get(key);
        } else {
            beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            BEAN_COPIER_CACHE.put(key, beanCopier);
        }
        beanCopier.copy(source, target, null);
    }


    /**
     * BeanCopier的copy
     * @param source 源对象
     * @param targetClass 目标对象
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        String key = genKey(source.getClass(), targetClass);
        BeanCopier beanCopier;
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            beanCopier = BEAN_COPIER_CACHE.get(key);
        } else {
            beanCopier = BeanCopier.create(source.getClass(), targetClass, false);
            BEAN_COPIER_CACHE.put(key, beanCopier);
        }
        try {
            T o = targetClass.newInstance();
            beanCopier.copy(source, o, null);
            return o;
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Bean拷贝异常", e);
        }
        return null;
    }

    /**
     * 生成key
     * @param srcClazz 源文件的class
     * @param tgtClazz 目标文件的class
     * @return string
     */
    private static String genKey(Class<?> srcClazz, Class<?> tgtClazz) {
        return srcClazz.getName() + tgtClazz.getName();
    }
}
