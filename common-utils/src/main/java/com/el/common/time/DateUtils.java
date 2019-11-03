package com.el.common.time;

import com.el.common.time.configuration.Configuration;
import com.el.common.time.entity.DateEntity;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;


/**
 * 日期工具类
 * 2019/10/5
 *
 * @author eddielee
 */
public class DateUtils {


    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINESE);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.CHINESE);

    /**
     * 格式化时间
     * @param date          日期对象
     * @return              日期字符串
     */
    public static String formatDate(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return Configuration.EMPTY_STRING;
        } else {
            return DATE_TIME_FORMATTER.format(date);
        }
    }

    /**
     * 格式化时间
     * @param date          日期对象
     * @param formatter     解析格式
     * @return              日期字符串
     */
    public static String formatDate(LocalDateTime date, String formatter) {
        if (Objects.isNull(date)) {
            return Configuration.EMPTY_STRING;
        } else {
            DateTimeFormatter dateTimeFormatter = DATE_TIME_FORMATTER;
            if (StringUtils.isNotBlank(formatter)){
                dateTimeFormatter = DateTimeFormatter.ofPattern(formatter, Locale.CHINESE);
            }
            return dateTimeFormatter.format(date);
        }
    }

    /**
     * 按照默认解析格式解析
     * @param date          日期字符串
     * @return              日期对象
     */
    public static LocalDateTime formatDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        } else {
            return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        }
    }

    /**
     * 格式化时间
     * @param date          时间字符串
     * @param formatter     格式化
     * @return              日期对象
     */
    public static LocalDateTime formatDate(String date, String formatter) {
        if (StringUtils.isBlank(date)) {
            return null;
        } else {
            DateTimeFormatter dateTimeFormatter = DATE_TIME_FORMATTER;
            if (StringUtils.isNotBlank(formatter)){
                dateTimeFormatter = DateTimeFormatter.ofPattern(formatter, Locale.CHINESE);
            }
            DateTimeFormatter customDateTimeFormatter = new DateTimeFormatterBuilder().append(dateTimeFormatter)
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
            return LocalDateTime.parse(date, customDateTimeFormatter);
        }
    }

    /**
     * 当前时间
     * @return      时间字符串
     */
    public static String nowTime() {
        return formatDate(LocalDateTime.now());
    }

    /**
     * 当前时间
     * @param formatter     解析格式
     * @return              时间字符串
     */
    public static String nowTime(String formatter){
        return formatDate(LocalDateTime.now(), formatter);
    }

    /**
     * 获取月第一天
     * @return              时间对象
     */
    public static LocalDateTime getMonthFirst(){
        return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取月最后一天
     * @return              时间对象
     */
    public static LocalDateTime getMonthlast(){
        return LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取传入时间的周开始时间和结束时间
     * @param date          时间字符串
     * @return              起止时间对象
     */
    public static DateEntity getWeekStartEndTime(String date){
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY,1);
        LocalDateTime localDateTime = formatDate(date, "yyyy-MM-dd");
        if (Objects.isNull(localDateTime)){
            return null;
        }
        int dayOfWeek = localDateTime.get(weekFields.dayOfWeek()) - 1;
        System.out.println(dayOfWeek);
        int startTimeOffset = - dayOfWeek;
        int endTimeOffset = 7 - dayOfWeek - 1;
        LocalDateTime startTime = localDateTime.plusDays(startTimeOffset);
        LocalDateTime endTime = localDateTime.plusDays(endTimeOffset);
        return new DateEntity(formatDate(startTime), formatDate(endTime));
    }

    public static int getWeekOfMonth(String date){
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY,1);
        LocalDateTime localDateTime = formatDate(date, "yyyy-MM-dd");
        if (Objects.isNull(localDateTime)){
            return 0;
        }
        return localDateTime.get(weekFields.weekOfMonth());
    }
}
