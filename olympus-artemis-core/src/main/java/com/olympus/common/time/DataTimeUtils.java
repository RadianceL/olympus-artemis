package com.olympus.common.time;

import com.olympus.base.utils.support.globalization.context.GlobalizationLocalUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 日期工具类 <br/>
 * since 2021/6/4
 *
 * @author eddie.lys
 */
public class DataTimeUtils {


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
        return LocalTimeUtils.formatDate(localDateTime);
    }

    public static String dateToString(Date date, String formatter) {
        if (Objects.isNull(date)) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return LocalTimeUtils.formatDate(localDateTime, formatter);
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
        para.setTime(GlobalizationLocalUtil.getCurrentLocalDate());
        para.set(Calendar.DATE, para.getActualMinimum(Calendar.DAY_OF_MONTH));
        para.set(Calendar.HOUR_OF_DAY, 0);
        para.set(Calendar.MINUTE, 0);
        para.set(Calendar.SECOND, 0);
        return para.getTime();
    }

    public static Date getLastDayOfMonth () {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(GlobalizationLocalUtil.getCurrentLocalDate());
        //获取本月最大天数
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DATE, lastDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
