package com.el.common.encryption;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取随机数
 * 2019-03-12
 *
 * @author 牛国凯
 */
public class RandomUtil {

    private static final Pattern pattern = Pattern.compile("[^0-9]");

    /**
     * 获取随机数
     *
     * @param length
     * @return
     */
    public static Integer getRandomNumber(Integer length) {
        Double rand = Math.random();
        Double arg = Math.pow(10, length);
        Double result = Math.floor(rand * arg);
        return result.intValue();
    }

    /**
     * 获取随机数
     *
     * @param length
     * @return
     */
    public static String getRandomNumberString(Integer length) {
        return formatNumber(getRandomNumber(length), length);
    }

    /**
     * 格式化数字
     *
     * @param value
     * @param length
     * @return
     */
    private static String formatNumber(Integer value, Integer length) {
        if (value == null) {
            value = 0;
        }
        return String.format("%0" + length + "d", value);
    }

    /**
     * 获取字符串中的数字
     *
     * @param content
     * @return
     */
    public static String getNumbers(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("").trim();
    }

    /**
     * 获取指定长度的随机字符串（小写字母加数字）
     *
     * @param len
     * @return
     */
    public static String getRandomStr(int len) {
        String randomPassword = RandomStringUtils.random(len, "abcdefghijklmnopqrstuvwxyz1234567890");
        return randomPassword;
    }

    /**
     * 创建一个随机字符串，其长度介于包含最小值和最大最大值之间,字符将从拉丁字母（a-z、A-Z）和数字0-9中选择。
     *
     * @param min 要生成的字符串的包含最小长度
     * @param max 要生成的字符串的包含最大长度
     * @return
     */
    public static String getRandomAlphanumeric(int min, int max) {
        String randomPassword = RandomStringUtils.randomAlphanumeric(min, max);
        return randomPassword;
    }


}
