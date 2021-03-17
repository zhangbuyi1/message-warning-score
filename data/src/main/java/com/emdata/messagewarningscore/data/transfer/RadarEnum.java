package com.emdata.messagewarningscore.data.transfer;

import cn.hutool.core.date.DateUtil;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Created by zhangshaohu on 2021/1/25.
 */

public enum RadarEnum {
    /**
     * 厦门机场
     * 20200904.000449.00.37.592
     */
    ZSAM("ZSAM", "\\d{8}.\\d{4}", false, "yyyyMMdd.HHmm", "", RadarEnum::getDate),
    /**
     * 福州长乐机场
     * QZSFZBVT200908115452.009
     */
    ZSFZ("ZSFZ", "\\d{10}", true, "yyyyMMddHHmm", "", RadarEnum::getDate),
    /**
     * 南昌昌北机场
     * QZSCNBPT200707000150.019
     */
    ZSCN("ZSCN", "\\d{10}", true, "yyyyMMddHHmm", "", RadarEnum::getDate),
    /**
     * 济南遥墙机场
     * QZSJNBVT200815175454.010
     */
    ZSJN("ZSJN", "\\d{10}", true, "yyyyMMddHHmm", "", RadarEnum::getDate),
    /**
     * 宁波
     * Z9574_20200826050001Z_CR_00_37
     */
    ZSNB("ZSNB", "_\\d{12}", false, "yyyyMMddHHmm", "_", RadarEnum::getDate),
    /**
     * ZSNJ
     * QZSNJBPT20200609200010.015
     */
    ZSNJ("ZSNJ", "\\d{12}", false, "yyyyMMddHHmm", "", RadarEnum::getDate),
    /**
     * ZSOF
     * QZSOFBPT200410113516.013
     */
    ZSOF("ZSOF", "\\d{10}", true, "yyyyMMddHHmm", "", RadarEnum::getDate),
    /**
     * ZSQD
     * 2020091617010.11V
     */
    ZSQD("ZSQD", "\\d{12}", false, "yyyyMMddHHmm", "", RadarEnum::getDate),
    /**
     * ZSSS
     * SHA210120012505.RAWS90N
     */
    ZSSS("ZSSS", "\\d{10}", true, "yyyyMMddHHmm", "", RadarEnum::getDate),
    /**
     * ZSWZ
     * Z9577_20201223070918Z_CR_00_37
     */
    ZSWZ("ZSWZ", "_\\d{12}", false, "yyyyMMddHHmm", "_", RadarEnum::getDate),
    /**
     * ZYTX
     * 2020080104230.05V
     */
    ZYTX("ZYTX", "\\d{12}", false, "yyyyMMddHHmm", "", RadarEnum::getDate);
    /**
     * 机场code
     */
    private String airportCode;
    /**
     * 正则
     */
    private String pattern;
    /**
     * 是否补年
     */
    private boolean isRepairYear;
    /**
     * 字符串转时间表达式
     */
    private String toDatePattern;
    /**
     * 特殊字符
     */
    private String beltSpecial;
    /**
     * 返回时间方法
     */
    private BiFunction<String, RadarEnum, Date> dataFun;

    RadarEnum(String airportCode, String pattern, boolean isRepairYear, String toDatePattern, String beltSpecial, BiFunction<String, RadarEnum, Date> getDate) {
        this.airportCode = airportCode;
        this.pattern = pattern;
        this.isRepairYear = isRepairYear;
        this.toDatePattern = toDatePattern;
        this.beltSpecial = beltSpecial;
        this.dataFun = getDate;
    }

    /**
     * @param fileName 文件名称
     * @return
     */
    public static Date getDate(String fileName, RadarEnum radarEnum) {
        return getDate(fileName, radarEnum.pattern, radarEnum.isRepairYear, radarEnum.beltSpecial, radarEnum.toDatePattern);
    }


    /**
     * 得到时间
     *
     * @param fileName      文件名称
     * @param pattern       正则
     * @param isRepairYear  是否需要加年
     * @param beltSpecial   特殊字符
     * @param toDatePattern 到时间的正则
     * @return
     */
    public static Date getDate(String fileName, String pattern, boolean isRepairYear, String beltSpecial, String toDatePattern) {
        String dateString = getDateString(fileName, pattern, isRepairYear, beltSpecial);
        if (Optional.ofNullable(dateString).isPresent()) {
            return DateUtil.parse(dateString, toDatePattern);
        }
        return null;
    }

    /**
     * 得到时间字符串
     *
     * @param fileName
     * @param pattern
     * @param isRepairYear
     * @param beltSpecial
     * @return
     */
    public static String getDateString(String fileName, String pattern, boolean isRepairYear, String beltSpecial) {
        String fileter = MatchUtil.get(fileName, pattern);
        if (StringUtils.isEmpty(fileter)) {
            return null;
        }
        // 去掉特殊字符
        String replaceBeltSpe = fileter.replace(beltSpecial, "");
        if (isRepairYear) {
            return "20" + replaceBeltSpe;
        } else {
            return replaceBeltSpe;
        }
    }

    public static RadarEnum get(String airportCode) {
        Optional<RadarEnum> first = Arrays.stream(RadarEnum.values()).filter(s -> {
            return s.airportCode.equals(airportCode);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isRepairYear() {
        return isRepairYear;
    }

    public String getToDatePattern() {
        return toDatePattern;
    }

    public String getBeltSpecial() {
        return beltSpecial;
    }

    public BiFunction<String, RadarEnum, Date> getDataFun() {
        return dataFun;
    }
}
