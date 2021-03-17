package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2020/10/9.
 */

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author: zhangshaohu
 * @date: 2020/10/9
 * @description: 提供天气现象便捷类
 */
public class WeatherDO extends Index implements Cloneable {
    /**
     * 天气现象最多出现3组
     */
    private static final Integer maxWeatherNum = 3;

    private static volatile WeatherDO weatherDO = null;


    public static WeatherDO buildWeather() {
        if (weatherDO == null) {
            synchronized (String.class) {
                if (weatherDO == null) {
                    WeatherDO.weatherDO = new WeatherDO();
                }
            }
        }
        return WeatherDO.weatherDO;
    }


    /**
     * ========================================测试程序==============================================================================
     *
     * @param args
     */
    public static void main(String[] args) {
        WeatherDO weatherDO = new WeatherDO("METAR ZSPD 110330Z 16007MPS 7000 -RA FEW007 OVC033 10/09 Q1024 NOSIG=");
        System.out.println(weatherDO);

    }

    private final Integer start = 6;
    private final Integer end = 7;


    /**
     * ========================================配置属性===========================================================================
     * 整个天气现象正则从报文中取
     */

    private static String pattern = "((\\+|\\-|\\s)(TS|SH|FZ|DR|MI|BC|PR|BL)(DZ|RA|SN|SG|PL|GR|GS|FG|BR|SA|DU|HZ|FU|VA|PO|SQ|FC|DS|SS)+)|((\\+|\\-|\\s)(DZ|RA|SN|SG|PL|GR|GS|FG|BR|SA|DU|HZ|FU|VA|PO|SQ|FC|DS|SS)+)|(NSW)|(TS)";
    private static String scorePattern = "";
    /**
     * ==============================================天气现象属性========================================================================
     */
    /**
     * 天气现象
     */
    private String weather = "";


    /**
     * ================================================天气判断方法======================================================================
     */


    /**
     * ===================================================构造器========================================================
     */
    public WeatherDO() {
        this.startIndex = start;
        this.endIndex = end;
    }

    public WeatherDO(String message) {
        this.startIndex = start;
        this.endIndex = end;
        String s = get(message, pattern, new StringBuilder());
        if (StringUtils.isNotEmpty(s)) {
            this.weather = s;
        }
    }


    /**
     * 得到需要评分的天气现象
     *
     * @return
     */
    public List<String> isScore(String weather) {
        String s = get(weather, scorePattern, new StringBuilder());
        if (StringUtils.isNotEmpty(s)) {
            // 转换为集合
            return Arrays.stream(s.split(" ")).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public String getWeather() {
        return weather;
    }

    @Override
    public String toString() {
        return this.weather;
    }
}