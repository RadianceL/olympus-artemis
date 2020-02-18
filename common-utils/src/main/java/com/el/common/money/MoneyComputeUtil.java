package com.el.common.money;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * 金额计算工具
 * 2019/10/7
 *
 * @author eddielee
 */
public class MoneyComputeUtil {

    private static final Integer DEFAULT_SCALE = 2;

    /**
     * 加法运算
     *
     * @param sourceAmount 加数
     * @param targetAmount 加数
     * @return 和
     */
    public static BigDecimal add(String sourceAmount, String targetAmount) {
        if (checkIsNotMoney(sourceAmount) || checkIsNotMoney(targetAmount)) {
            throw new IllegalArgumentException("参数中含有非金额数值");
        }
        BigDecimal sourceBigDecimal = new BigDecimal(sourceAmount);
        BigDecimal targetBigDecimal = new BigDecimal(targetAmount);
        return add(sourceBigDecimal, targetBigDecimal);
    }

    /**
     * 加法运算
     *
     * @param sourceAmount 加数
     * @param targetAmount 加数
     * @return 和
     */
    public static BigDecimal add(BigDecimal sourceAmount, BigDecimal targetAmount) {
        return sourceAmount.add(targetAmount, MathContext.UNLIMITED);
    }

    /**
     * 减法运算
     *
     * @param sourceAmount 减数
     * @param targetAmount 被减数
     * @return 差
     */
    public static BigDecimal subtract(String sourceAmount, String targetAmount) {
        if (checkIsNotMoney(sourceAmount) || checkIsNotMoney(targetAmount)) {
            throw new IllegalArgumentException("参数中含有非金额数值");
        }
        BigDecimal sourceBigDecimal = new BigDecimal(sourceAmount);
        BigDecimal targetBigDecimal = new BigDecimal(targetAmount);
        return subtract(sourceBigDecimal, targetBigDecimal);
    }

    /**
     * 减法运算
     *
     * @param sourceAmount 减数
     * @param targetAmount 被减数
     * @return 差
     */
    public static BigDecimal subtract(BigDecimal sourceAmount, BigDecimal targetAmount) {
        return sourceAmount.subtract(targetAmount, MathContext.UNLIMITED);
    }

    /**
     * 乘法运算
     *
     * @param sourceAmount 乘数
     * @param targetAmount 乘数
     * @return 积
     */
    public static BigDecimal multiply(String sourceAmount, String targetAmount) {
        if (checkIsNotMoney(sourceAmount) || checkIsNotMoney(targetAmount)) {
            throw new IllegalArgumentException("参数中含有非金额数值");
        }
        BigDecimal sourceBigDecimal = new BigDecimal(sourceAmount);
        BigDecimal targetBigDecimal = new BigDecimal(targetAmount);
        return multiply(sourceBigDecimal, targetBigDecimal);
    }

    /**
     * 乘法运算
     *
     * @param sourceAmount 乘数
     * @param targetAmount 乘数
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal sourceAmount, BigDecimal targetAmount) {
        return sourceAmount.multiply(targetAmount, MathContext.UNLIMITED);
    }

    /**
     * 除法运算
     *
     * @param sourceAmount 被除数
     * @param targetAmount 除数
     * @param scale        小数点后精度
     * @return 商
     */
    public static BigDecimal divide(String sourceAmount, String targetAmount, Integer scale) {
        if (checkIsNotMoney(sourceAmount) || checkIsNotMoney(targetAmount)) {
            throw new IllegalArgumentException("参数中含有非金额数值");
        }
        BigDecimal sourceBigDecimal = new BigDecimal(sourceAmount);
        BigDecimal targetBigDecimal = new BigDecimal(targetAmount);
        return divide(sourceBigDecimal, targetBigDecimal, scale);
    }

    /**
     * 除法运算
     *
     * @param sourceAmount 被除数
     * @param targetAmount 除数
     * @param scale        小数点后精度
     * @return 商
     */
    public static BigDecimal divide(BigDecimal sourceAmount, BigDecimal targetAmount, Integer scale) {
        if (Objects.isNull(scale) || scale == 0) {
            scale = DEFAULT_SCALE;
        }
        return sourceAmount.divide(targetAmount, scale, BigDecimal.ROUND_HALF_UP).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 四舍五入保留小数点后#{scale}个
     *
     * @param sourceAmount
     * @param scale
     * @return
     */
    public static BigDecimal roundHalfUp(String sourceAmount, Integer scale) {
        if (checkIsNotMoney(sourceAmount)) {
            throw new IllegalArgumentException("参数中含有非金额数值");
        }

        return roundHalfUp(new BigDecimal(sourceAmount), scale);
    }

    /**
     * 四舍五入保留小数点后#{scale}个
     *
     * @param sourceAmount
     * @param scale
     * @return
     */
    public static BigDecimal roundHalfUp(BigDecimal sourceAmount, Integer scale) {
        if (Objects.isNull(scale) || scale == 0) {
            scale = DEFAULT_SCALE;
        }
        return sourceAmount.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 校验是否为金额
     *
     * @param source
     * @return
     */
    public static boolean checkIsNotMoney(String source) {
        if (StringUtils.isBlank(source)) {
            return true;
        }
        return !source.matches(Configuration.CURRENCY_YUAN_REGEX);
    }

    public static void main(String[] args) {
        System.out.println(add("10.543", "10.12345678"));
        System.out.println(subtract("10.543", "10.12345678"));
        System.out.println(multiply("10.543", "10.12345678"));
        System.out.println(divide("10.543", "10.12345678", 4));
        System.out.println(roundHalfUp(new BigDecimal("10.545"), 2));
    }
}
