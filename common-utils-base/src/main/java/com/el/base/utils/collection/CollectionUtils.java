package com.el.base.utils.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T> List<List<T>> splitList(List<T> splitList, int len) {
        if (isEmpty(splitList)) {
            return null;
        }

        List<List<T>> resultList = new ArrayList<>();
        if (len < 1) {
            resultList.add(splitList);
            return resultList;
        }

        int size = splitList.size();
        int count = size / len -1;

        for (int i = 0; i < count; i++) {
            List<T> subList = splitList.subList(i * len, (Math.min((i + 1) * len, size)));
            resultList.add(subList);
        }

        return resultList;
    }
}
