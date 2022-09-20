package com.olympus.base.utils.support.globalization.lang;

import com.olympus.base.utils.support.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

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
    CN(100L, "CN", Local.AREA_TYPE_COUNTRY) {
        @Override
        public Date getCurrentLocalTime() {
            return new Date();
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT+8";
        }
    },
    /**
     * 德国
     */
    DE(101L, "DE", Local.AREA_TYPE_COUNTRY){
        @Override
        public Date getCurrentLocalTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = changeTimeZone(now, "GMT+8", "GMT+1");
            return Date.from(localDateTime.atZone(ZoneId.of("GMT+8")).toInstant());
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT+1";
        }
    },
    /**
     * 泰国
     */
    TH(102L, "TH", Local.AREA_TYPE_COUNTRY){
        @Override
        public Date getCurrentLocalTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = changeTimeZone(now, "GMT+8", "GMT+7");
            return Date.from(localDateTime.atZone(ZoneId.of("GMT+8")).toInstant());
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT+7";
        }
    },
    /**
     * 越南
     */
    VI(103L, "VI", Local.AREA_TYPE_COUNTRY){
        @Override
        public Date getCurrentLocalTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = changeTimeZone(now, "GMT+8", "GMT+7");
            return Date.from(localDateTime.atZone(ZoneId.of("GMT+8")).toInstant());
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT+7";
        }
    },
    /**
     * 俄罗斯
     */
    RU(104L, "RU", Local.AREA_TYPE_COUNTRY){
        @Override
        public Date getCurrentLocalTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = changeTimeZone(now, "GMT+8", "GMT+3");
            return Date.from(localDateTime.atZone(ZoneId.of("GMT+8")).toInstant());
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT+3";
        }
    },
    /**
     * 法国
     */
    FR(105L, "FR", Local.AREA_TYPE_COUNTRY){
        @Override
        public Date getCurrentLocalTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = changeTimeZone(now, "GMT+8", "GMT+1");
            return Date.from(localDateTime.atZone(ZoneId.of("GMT+8")).toInstant());
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT+1";
        }
    },
    /**
     * 美国
     */
    EN(106L, "EN", Local.AREA_TYPE_COUNTRY){
        @Override
        public Date getCurrentLocalTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = changeTimeZone(now, "GMT+8", "GMT-4");
            return Date.from(localDateTime.atZone(ZoneId.of("GMT+8")).toInstant());
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT-4";
        }
    },
    /**
     * 日本
     */
    JP(107L, "JP", Local.AREA_TYPE_COUNTRY){
        @Override
        public Date getCurrentLocalTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = changeTimeZone(now, "GMT+8", "GMT+9");
            return Date.from(localDateTime.atZone(ZoneId.of("GMT+8")).toInstant());
        }

        @Override
        public String getCurrentTimeZone() {
            return "GMT+9";
        }
    };

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

    public abstract Date getCurrentLocalTime();

    public abstract String getCurrentTimeZone();

    /**
     *
     * @param time 需要转换的时间
     * @param fromZone 需要转换的时间的时区，即time的时区
     * @param toZone 需要转成的时区
     * @return 转换时区之后的时间
     */
    public static LocalDateTime changeTimeZone(LocalDateTime time, String fromZone, String toZone) {
        ZonedDateTime zonedTime = time.atZone(ZoneId.of(fromZone));
        ZonedDateTime converted = zonedTime.withZoneSameInstant(ZoneId.of(toZone));
        return converted.toLocalDateTime();
    }
}

