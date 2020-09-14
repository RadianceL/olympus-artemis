package com.el.common.money;

/**
 * 2019/10/5
 *
 * @author eddielee
 */
public class MoneyConstant {

    /**
     * 金额为元的格式，小数点后两位
     */
    public static final String CURRENCY_YUAN_REGEX = "^(^-?[1-9](\\d+)?(\\.\\d{1,8})?$)|(^-?0$)|(^-?\\d\\.\\d{1,8}$)";
}
