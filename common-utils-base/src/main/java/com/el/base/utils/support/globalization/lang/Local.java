package com.el.base.utils.support.globalization.lang;

import com.el.base.utils.support.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 地区数据
 * since 2019/12/7
 *
 * @author eddie
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Local {

    /**
     * 中国
     */
    CN(100L, "CN", Local.AREA_TYPE_COUNTRY),
    /**
     * 俄罗斯
     */
    RU(101L, "RU", Local.AREA_TYPE_COUNTRY),
    /**
     * 法国
     */
    FR(102L, "FR", Local.AREA_TYPE_COUNTRY),
    /**
     * 美国
     */
    US(103L, "EN", Local.AREA_TYPE_COUNTRY),
    /**
     * 日本
     */
    JP(104L, "JP", Local.AREA_TYPE_COUNTRY);

    /**
     * 地区类型 国家
     */
    private static final String AREA_TYPE_COUNTRY = "COUNTRY";

    /**
     * 地区类型 城市
     */
    private static final String AREA_TYPE_CITY = "CITY";

    /**
     * 默认错误码配置文件名称
     */
    private static final String DEFAULT_LOCAL_NAME = "DEFAULT";

    /**
     * 地址ID
     */
    private Long areaId;

    /**
     * 地址名称
     */
    private String localName;

    /**
     * 地址类型
     */
    private String areaType;

    public static Local findLocal(String localCode) {
        if (StringUtils.isBlank(localCode)) {
            return null;
        }
        for (Local local : values()) {
            String fileNameUpperCase = localCode.toUpperCase();
            if (fileNameUpperCase.equals(local.getLocalName())) {
                return local;
            }
        }
        return null;
    }

    public static String findLocalName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return Constant.EMPTY_STRING;
        }

        for (Local e : values()) {
            String fileNameUpperCase = fileName.toUpperCase();
            if (fileNameUpperCase.contains(DEFAULT_LOCAL_NAME)) {
                return Local.CN.getLocalName();
            }
            if (fileNameUpperCase.contains(e.getLocalName())) {
                return e.getLocalName();
            }
        }
        return Constant.EMPTY_STRING;
    }

}
