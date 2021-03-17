package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2020/12/22
 * @description:
 */
@Data
@TableName("weather_map")
public class WeatherMapDO {
    private Integer id;
    /**
     * 天气现象正则
     */
    private String weatherPattern;
    /**
     * 对应的天气现象code码
     */
    private String weatherCode;
    /**
     * 对应的天气现象类型
     */
    private String weatherType;

    /**
     * 同类天气排序 如 同样是降水天气现象 强类型天气现象比弱类型天气现象的前面
     */
    private Integer order;

    public WeatherMapDO() {


    }

    public WeatherMapDO(String weatherPattern, String weatherCode, String weatherType, Integer order) {
        this.weatherPattern = weatherPattern;
        this.weatherCode = weatherCode;
        this.weatherType = weatherType;
        this.order = order;
    }
}