package com.el.common.time;

import com.el.common.constant.UtilsConstants;
import com.el.common.time.entity.DateEntity;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/**
 * 日期工具类
 * 2019/10/5
 *
 * @author eddielee
 */
public class LocalTimeUtils {


    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINESE);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.CHINESE);

    /**
     * 将日期字符串转化为Date类型
     */
    public static Date parseDateString(String dateStr, String pattern) {
        try {
            DateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (Exception ex) {
            return null;
        }
    }

    public static double dateBetween(Date start, Date end) {
        long interval = start.getTime() - end.getTime();
        double days = interval / (1000.0D * 600D * 600D * 240D);
        return days / 24.0D;
    }

    /**
     * 将日期字符串转化为Date类型
     */
    public static Date parseDateString(String dateStr) {
        try {
            return DEFAULT_DATE_FORMATTER.parse(dateStr);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        date = calendar.getTime();
        return date;
    }

    public static Date getNextSecond(Date date) {
        date.setTime(date.getTime() + 1000);
        return date;
    }

    public static String dateToString(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return formatDate(localDateTime);
    }

    public static String dateToString(Date date, String formatter) {
        if (Objects.isNull(date)) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return formatDate(localDateTime, formatter);
    }


    /**
     * 格式化时间
     *
     * @param date 日期对象
     * @return 日期字符串
     */
    public static String formatDate(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return UtilsConstants.EMPTY_STRING;
        } else {
            return DATE_TIME_FORMATTER.format(date);
        }
    }

    /**
     * 格式化时间
     *
     * @param date      日期对象
     * @param formatter 解析格式
     * @return 日期字符串
     */
    public static String formatDate(LocalDateTime date, String formatter) {
        if (Objects.isNull(date)) {
            return UtilsConstants.EMPTY_STRING;
        } else {
            DateTimeFormatter dateTimeFormatter = DATE_TIME_FORMATTER;
            if (StringUtils.isNotBlank(formatter)) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(formatter, Locale.CHINESE);
            }
            return dateTimeFormatter.format(date);
        }
    }

    /**
     * 按照默认解析格式解析
     *
     * @param date 日期字符串
     * @return 日期对象
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
     *
     * @param date      时间字符串
     * @param formatter 格式化
     * @return 日期对象
     */
    public static LocalDateTime formatDate(String date, String formatter) {
        if (StringUtils.isBlank(date)) {
            return null;
        } else {
            DateTimeFormatter dateTimeFormatter = DATE_TIME_FORMATTER;
            if (StringUtils.isNotBlank(formatter)) {
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
     *
     * @return 时间字符串
     */
    public static String nowTime() {
        return formatDate(LocalDateTime.now());
    }

    /**
     * 当前时间
     *
     * @param formatter 解析格式
     * @return 时间字符串
     */
    public static String nowTime(String formatter) {
        return formatDate(LocalDateTime.now(), formatter);
    }

    /**
     * 获取月第一天
     *
     * @return 时间对象
     */
    public static LocalDateTime getMonthFirst() {
        return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取月最后一天
     *
     * @return 时间对象
     */
    public static LocalDateTime getMonthlast() {
        return LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取传入时间的周开始时间和结束时间
     *
     * @param date 时间字符串
     * @return 起止时间对象
     */
    public static DateEntity getWeekStartEndTime(String date) {
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        LocalDateTime localDateTime = formatDate(date, "yyyy-MM-dd");
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        int dayOfWeek = localDateTime.get(weekFields.dayOfWeek()) - 1;
        System.out.println(dayOfWeek);
        int startTimeOffset = -dayOfWeek;
        int endTimeOffset = 7 - dayOfWeek - 1;
        LocalDateTime startTime = localDateTime.plusDays(startTimeOffset);
        LocalDateTime endTime = localDateTime.plusDays(endTimeOffset);
        return new DateEntity(formatDate(startTime), formatDate(endTime));
    }

    public static int getWeekOfMonth(String date) {
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        LocalDateTime localDateTime = formatDate(date, "yyyy-MM-dd");
        if (Objects.isNull(localDateTime)) {
            return 0;
        }
        return localDateTime.get(weekFields.weekOfMonth());
    }
    /**
     * 获取某月最后一天
     *
     * @return 时间对象
     */
    public static String getMonthLast(int year, int month) {
        LocalDateTime of = LocalDateTime.of(year, month, 1, 0, 0);
        return formatDate(of.with(TemporalAdjusters.lastDayOfMonth()), "");
    }

    /**
     * 获取某月第一天
     *
     * @return 时间对象
     */
    public static String getMonthFirst(int year, int month) {
        LocalDateTime of = LocalDateTime.of(year, month, 1, 0, 0);
        return formatDate(of.with(TemporalAdjusters.firstDayOfMonth()), "");
    }

    public static String addMonth(Date date, int addMonth, String format) {
        Calendar c = Calendar.getInstance();

        //过去一月
        c.setTime(date);
        c.add(Calendar.MONTH, addMonth);
        Date m = c.getTime();

        if (StringUtils.isNotBlank(format)) {
            DateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(m);
        }
        return DEFAULT_DATE_FORMATTER.format(m);
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * @return 返回指定月份的第一天的0分0秒
     */
    public static Date getFirstMonthOfDate() {
        Calendar para = Calendar.getInstance(java.util.Locale.CHINA);
        para.setTime(new Date());
        para.set(Calendar.DATE, para.getActualMinimum(Calendar.DAY_OF_MONTH));
        para.set(Calendar.HOUR_OF_DAY, 0);
        para.set(Calendar.MINUTE, 0);
        para.set(Calendar.SECOND, 0);
        return para.getTime();
    }

    public static Date getLastDayOfMonth () {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //获取本月最大天数
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DATE, lastDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
