package com.emdata.messagewarningscore.common.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: sunming
 * @date: 2020/3/31
 * @description: java8时间工具类
 */
public class Java8DateUtils {

    /**
     * 例如:2018-12-28
     */
    public static final String DATE = "yyyy-MM-dd";
    /**
     * 例如:2018-12-28 10:00:00
     */
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 例如：20200331 02:00:00
     */
    public static final String FULL_PATTERN_1 = "yyyyMMdd HH:mm:ss";

    /**
     * 例如：20200331
     */
    public static final String DATE_PATTERN_1 = "yyyyMMdd";

    /**
     * 例如：202003
     */
    public static final String MONTH_PATTERN_1 = "yyyyMM";

    /**
     * 例如：2020-03
     */
    public static final String MONTH_PATTERN_2 = "yyyy-MM";

    /**
     * 例如：2020
     */
    public static final String YEAR = "yyyy";

    /**
     * 10
     */
    public static final String MONTH = "MM";

    public static final String DAY_HOUR = "ddHH";

    /**
     * 例如:10:00:00
     */
    public static final String TIME = "HHmmss";
    /**
     * 例如:10:00
     */
    public static final String TIME_WITHOUT_SECOND = "HH:mm";

    /**
     * 例如:2日10:00
     */
    public static final String DAY_HOUR_MINUTE = "dd日HH:mm";

    /**
     * 例如:2018-12-28 10:00
     */
    public static final String DATE_TIME_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm";


    public static final ZoneId SYSTEM_DEFAULT = ZoneId.systemDefault();

    public static final ZoneId UTC = ZoneId.of("UTC");


    /**
     * 获取年
     *
     * @return 年
     */
    public static int getYear() {
        LocalDate localDate = LocalDate.now();
        return localDate.getYear();
    }

    /**
     * 获取月份
     *
     * @return 月份
     */
    public static int getMonth() {
        LocalDate localDate = LocalDate.now();
        return localDate.getMonthValue();
    }

    /**
     * 获取某月的第几天
     *
     * @return 几号
     */
    public static int getMonthOfDay() {
        LocalDate localDate = LocalDate.now();
        return localDate.getDayOfMonth();
    }

    /**
     * 格式化日期为字符串
     *
     * @param date    date
     * @param pattern 格式
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {

        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化日期为字符串
     *
     * @param date    date
     * @param pattern 格式
     * @return 日期字符串
     */
    public static String formatByZoneId(Date date, String pattern, ZoneId zoneId) {
        if (zoneId == null) {
            zoneId = SYSTEM_DEFAULT;
        }
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析字符串日期时间为Date
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     格式
     * @return Date
     */
    public static Date parseDateTimeStr(String dateTimeStr, String pattern) {

        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * 解析字符串日期时间为Date
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     格式
     * @return Date
     */
    public static Date parseDateTimeStrByZoneId(String dateTimeStr, String pattern, ZoneId zoneId) {
        if (zoneId == null) {
            zoneId = SYSTEM_DEFAULT;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        return Date.from(instant);
    }

    /**
     * 解析字符串日期为Date
     *
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return Date
     */
    public static Date parseDateStr(String dateStr, String pattern) {

        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        Date date = localDate2Date(localDate);
        return date;
    }

    /**
     * 将某时间字符串转为自定义时间格式的LocalDateTime
     *
     * @param time   时间字符串
     * @param format 格式
     * @return LocalDateTime
     */
    public static LocalDateTime parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }


    /**
     * 为Date增加分钟,减传负数
     *
     * @param date        日期
     * @param plusMinutes 要增加的分钟数
     * @return 新的日期
     */
    public static Date addMinutes(Date date, Long plusMinutes) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime newDateTime = dateTime.plusMinutes(plusMinutes);
        return Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 增加时间
     *
     * @param date date
     * @param hour 要增加的小时数
     * @return new date
     */
    public static Date addHour(Date date, Long hour) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime localDateTime = dateTime.plusHours(hour);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @return 返回当天的起始时间
     */
    public static Date getStartTime() {

        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return localDateTime2Date(now);
    }


    /**
     * @return 返回当天的结束时间
     */
    public static Date getEndTime() {
        LocalDateTime now = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999);
        return localDateTime2Date(now);
    }

    /**
     * 减月份
     *
     * @param monthsToSubtract 月份
     * @return Date
     */
    public static Date minusMonths(long monthsToSubtract) {
        LocalDate localDate = LocalDate.now().minusMonths(monthsToSubtract);

        return localDate2Date(localDate);
    }

    /**
     * LocalDate类型转为Date
     *
     * @param localDate LocalDate object
     * @return Date object
     */
    public static Date localDate2Date(LocalDate localDate) {

        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());

        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Date类型转为LocalDate
     *
     * @param date object
     * @return object
     */
    public static LocalDate date2LocalDate(Date date) {
        if (null == date) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    /**
     * LocalDateTime类型转为Date
     *
     * @param localDateTime LocalDateTime object
     * @return Date object
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将LocalDateTime转为long类型的时间戳
     *
     * @param localDateTime LocalDateTime
     * @return 时间戳
     */
    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }


    /**
     * 将long类型的timestamp转为LocalDateTime
     *
     * @param timestamp 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }


    /**
     * 查询当前年的第一天
     *
     * @param pattern 格式，默认格式yyyyMMdd
     * @return 20190101
     */
    public static String getFirstDayOfCurrentYear(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now().withMonth(1).withDayOfMonth(1);

        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyyMMdd";
        }

        return format(localDateTime2Date(localDateTime), pattern);
    }

    /**
     * 查询前一年最后一个月第一天
     *
     * @param pattern 格式，默认格式yyyyMMdd
     * @return 20190101
     */
    public static String getLastMonthFirstDayOfPreviousYear(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now().minusYears(1L).withMonth(12).withDayOfMonth(1);

        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyyMMdd";
        }

        return format(localDateTime2Date(localDateTime), pattern);
    }

    /**
     * 查询前一年最后一个月第一天
     *
     * @param pattern 格式，默认格式yyyyMMdd
     * @return 20190101
     */
    public static String getLastMonthLastDayOfPreviousYear(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now().minusYears(1L).with(TemporalAdjusters.lastDayOfYear());

        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyyMMdd";
        }

        return format(localDateTime2Date(localDateTime), pattern);
    }

    /**
     * 获取当前日期
     *
     * @param pattern 格式，默认格式yyyyMMdd
     * @return 20190101
     */
    public static String getCurrentDay(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now();

        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyyMMdd";
        }

        return format(localDateTime2Date(localDateTime), pattern);
    }

    /**
     * 获取当前月的 天数
     */
    public static int getCurrentMonthDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int maxDate = calendar.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据年 月 获取对应的月份 天数
     *
     * @param year  年
     * @param month 月
     * @return 天数
     */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int maxDate = calendar.get(Calendar.DATE);
        return maxDate;
    }
}
