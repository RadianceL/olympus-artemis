package com.olympus.base.utils.collection;

import java.util.*;

/**
 * <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
public class CollectionUtils {

    public static boolean isEmpty(Map<?, ?> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T> List<List<T>> averageAssign(List<T> sourceList, int len) {
        if (isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        List<List<T>> result = new ArrayList<>();
        int remaider = sourceList.size() % len;
        int number = sourceList.size() / len;
        int offset = 0;
        for (int i = 0; i < len; i++) {
            List<T> value;
            if (remaider > 0) {
                value = sourceList.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = sourceList.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }
}
