package com.emdata.messagewarningscore.common.common.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Created by zhangshaohu on 2019/11/2.
 */
@Slf4j
public class JudgeUtil {
    public static Calendar getCalendar(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }

    /**
     * 获取当前报文时间
     *
     * @param metar  metar或者taf报文
     * @param format 返回的时间字符串格式
     * @param type   taf或者metar
     * @return
     */
    public static String getMessageTime(String metar, String format, String type) {
        return DateUtil.format(getMessageTime(metar, type), format);
    }

    /**
     * 获取当前报文时间
     *
     * @param metar  metar或者taf报文
     * @param format 返回的时间字符串格式
     * @param type   taf或者metar
     * @return
     */
    public static String getMessageTime(String metar, String format, String type, BiFunction<String, String, String> getMessageMetarTime) {
        return DateUtil.format(getMessageTime(metar, type, getMessageMetarTime), format);
    }


    /**
     * 获取当前报文时间
     *
     * @param yyyyMM 年月
     * @param metar  metar或者taf报文
     * @param format 返回的时间字符串格式
     * @param type   taf或者metar
     * @return
     */
    public static String getMessageTime(String yyyyMM, String metar, String format, String type) {
        return DateUtil.format(getMessageTimeChild(metar, type, yyyyMM), format);
    }

    public static Date getMessageTime(String metar, String type, BiFunction<String, String, String> getMessageMetarTime) {
        String metarTime = getMessageMetarTime.apply(metar, type);
        return getDate(metarTime);
    }

    /**
     * 返回带z的时间
     *
     * @return
     */
    public static String getMessageMetarTime(String metar, String type) {
        String[] split = metar.split(" ");
        if (type.equals("metar")) {
            Optional<String> first = Arrays.stream(split).filter(s -> {
                return time(s);
            }).findFirst();
            return first.get();
        } else {
            for (String s : split) {
                String trim = s.trim();
                if (regionalTime(trim)) {
                    return trim.split("/")[0] + "00";
                }
            }
            for (String s : split) {
                String trim = s.trim();
                if (historyTafTime(trim)) {
                    return trim.substring(0, 4) + "00";
                }
            }
        }
        return null;
    }

    /**
     * @param str 判断当前字符串是否是时间
     * @return
     */
    public static boolean time(String str) {
        boolean matches = str.matches("^[0-9]{6}Z$");
        return matches;
    }

    /**
     * @param str 判断当前字符串是否是时间区间
     * @return
     */
    public static boolean regionalTime(String str) {
        boolean regionalTime = false;
        try {
            String[] split = str.split("/");
            Integer.parseInt(split[0]);
            Integer.parseInt(split[1]);
            regionalTime = true;
        } catch (Exception e) {
            regionalTime = false;
        }
        return regionalTime;
    }

    public static boolean historyTafTime(String s) {
        // 090312
        if (s.length() == 6) {
            try {
                Integer.parseInt(s);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 得到当前metar报文时间
     *
     * @param metar
     * @return
     */
    public static Date getMessageTime(String metar, String type) {
        // 得到带Z的时间
        String metarTime = getMessageMetarTime(metar, type);
        return getDate(metarTime);
    }

    private static Date getDate(String metarTime) {
        String dd = metarTime.substring(0, 2);
        String hh = metarTime.substring(2, 4);
        String mm = metarTime.substring(4, 6);
        Date thisDate = new Date();
        Date metarDate = getMetarDate((s, s2, s3) -> {
            String thisyear = DateUtil.format(thisDate, "yyyyMM");
            String splitJointTime = thisyear + dd + hh + mm;
            return splitJointTime;
        }, dd, hh, mm, thisDate);
        return metarDate;
    }

    /**
     * 得到当前metar报文时间
     *
     * @param metar
     * @return
     */
    public static Date getMessageTimeChild(String metar, String type, String yearMm) {
        // 得到带Z的时间
        String metarTime = getMessageMetarTime(metar, type);
        String dd = metarTime.substring(0, 2);
        String hh = metarTime.substring(2, 4);
        String mm = metarTime.substring(4, 6);
        Date thisDate = new Date();
        Date metarDate = getMetarDate((s, s2, s3) -> {
            if (StringUtils.isNotEmpty(yearMm)) {
                String splitJointTime = yearMm + dd + hh + mm;
                return splitJointTime;
            } else {
                String thisyear = DateUtil.format(thisDate, "yyyyMM");
                String splitJointTime = thisyear + dd + hh + mm;
                return splitJointTime;
            }
        }, dd, hh, mm, thisDate);
        return metarDate;
    }

    /**
     * 空管局使用这个方法
     *
     * @return
     */
    public static Date getMetarDate(BiFunctionP<String, String, String, String> spiltTime, String dd, String hh, String mm, Date thisDate) {
        if (StringUtils.isEmpty(mm)) {
            mm = "00";
        }
        String splitJointTime = spiltTime.apply(dd, hh, mm);
        // 如果拼接的时间大于前一个时间
        Date splitJointDate = DateUtil.parse(splitJointTime, "yyyyMMddHHmm");
        // 说明是上一年的时间
        while (splitJointDate.getTime() > thisDate.getTime()) {
            long date = 1000L * 60 * 60 * 24 * 10;
            splitJointDate = new Date(splitJointDate.getTime() - date);
        }
        String upYearyyyymm = DateUtil.format(splitJointDate, "yyyyMM");
        DateTime parse = DateUtil.parse(upYearyyyymm.substring(0, 6) + dd + " " + hh + ":" + mm + ":00", "yyyyMMdd HH:mm:ss");
        return parse;
    }

    /**
     * 时间计算
     *
     * @param startTime 开始时间
     * @param field     时间单位
     * @param num       时间值
     * @return
     */
    public static Date add(Date startTime, Integer field, Integer num) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(startTime);
        instance.add(field, num);
        return instance.getTime();
    }
}
