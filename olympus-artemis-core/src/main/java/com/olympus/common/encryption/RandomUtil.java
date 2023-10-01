package com.olympus.common.encryption;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取随机数
 * 2019-03-12
 *
 * @author eddie
 */
public class RandomUtil {
    /**
     * 保留数字正则表达式
     */
    private static final Pattern PATTERN = Pattern.compile("[^0-9]");
    /**
     * 随机字符串范围
     */
    private static final String RANDOM_CHAR_SCOPE = "abcdefghijklmnopqrstuvwxyz1234567890<>{}[]-+";

    /**
     * 获取随机数
     */
    public static Integer getRandomNumber(Integer length) {
        Double rand = Math.random();
        Double arg = Math.pow(10, length);
        double result = Math.floor(rand * arg);
        return (int) result;
    }

    /**
     * 获取随机数
     */
    public static String getRandomNumberString(Integer length) {
        return formatNumber(getRandomNumber(length), length);
    }

    /**
     * 格式化数字
     */
    private static String formatNumber(Integer value, Integer length) {
        if (value == null) {
            value = 0;
        }
        return String.format("%0" + length + "d", value);
    }

    /**
     * 获取字符串中的数字
     */
    public static String getNumbers(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        Matcher matcher = PATTERN.matcher(content);
        return matcher.replaceAll("").trim();
    }

    /**
     * 获取指定长度的随机字符串（小写字母加数字）
     */
    public static String getRandomStr(int len) {
        return RandomStringUtils.random(len, RANDOM_CHAR_SCOPE);
    }

    /**
     * 获取指定长度的随机字符串（小写字母加数字）
     */
    public static String getRandomStr(int len, String scope) {
        return RandomStringUtils.random(len, scope);
    }

    /**
     * 创建一个随机字符串，其长度介于包含最小值和最大最大值之间,字符将从拉丁字母（a-z、A-Z）和数字0-9中选择。
     *
     * @param min 要生成的字符串的包含最小长度
     * @param max 要生成的字符串的包含最大长度
     */
    public static String getRandomAlphanumeric(int min, int max) {
        return RandomStringUtils.randomAlphanumeric(min, max);
    }


}
