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

    /**
     *  拆分集合
     *  @param <T>      元数据
     *  @param resList  要拆分的集合
     *  @param count    每个集合的元素个数
     *  @return         返回拆分后的各个集合
     **/
    public <T> List<List<T>> split(List<T> resList, int count) {
        if (resList == null || count < 1){
            return null;
        }
        List<List<T>> ret = new ArrayList<>();
        int size = resList.size();
        if (size <= count) {
            // 数据量不足count指定的大小
            ret.add(resList);
        } else {
            int pre = size / count;
            int last = size % count;
            // 前面pre个集合，每个大小都是count个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<T>();
                for (int j = 0; j < count; j++) {
                    itemList.add(resList.get(i * count + j));
                }
                ret.add(itemList);
            }
            // last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<T>();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * count + i));
                }
                ret.add(itemList);
            }
        }
        return ret;
    }

    /**
     * array拆分
     * @param sourceList        元数据
     * @param len               拆分长度
     * @return                  二维列表
     */
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
