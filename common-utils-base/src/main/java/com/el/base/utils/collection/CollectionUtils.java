package com.el.base.utils.collection;

/**
 * <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
public class CollectionUtils {

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }
}
