package com.emdata.messagewarningscore.common.accuracy.entity;/**
 * Created by zhangshaohu on 2020/12/29.
 */

import com.emdata.messagewarningscore.common.accuracy.enums.WeatherType;
import com.emdata.messagewarningscore.common.dao.entity.WeatherMapDO;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/12/29
 * @description: 预测数据 预测元素信息 当前数据提供
 */
public class ForecastData {

    /**
     * 先写一个静态天气现象与code的映射关系 到时候去数据库查询
     */
    public static List<WeatherMapDO> weatherMapDOS = new ArrayList<WeatherMapDO>() {
        {
            // 弱降水
            add(new WeatherMapDO("弱降水|弱降雨|小雨", "-RA", WeatherType.RA.getCode(), 1));
            add(new WeatherMapDO("短时小阵雨|小阵雨", "-SHRA", WeatherType.RA.getCode(), 1));
            add(new WeatherMapDO("小雷雨|弱雷雨|小雷阵雨|弱雷阵雨", "-TSRA", WeatherType.RA.getCode(), 1));
            // 低云排序
            add(new WeatherMapDO("低云([^\\d])+(\\d+)-(\\d+)|低云[^\\d](\\d+)", "cloud", WeatherType.CLOUD.getCode(), 0));
            add(new WeatherMapDO("龙卷", "FC", WeatherType.FC.getCode(), 0));
            // 冰雹
            add(new WeatherMapDO("雹", "GR", WeatherType.GRGS.getCode(), 1));
            add(new WeatherMapDO("雷伴雹", "GR", WeatherType.GRGS.getCode(), 0));
            // 小冰雹
            add(new WeatherMapDO("小冰雹|霰", "GS", WeatherType.GRGS.getCode(), 0));
            // 风
            add(new WeatherMapDO("大风", "wind", WeatherType.WIND.getCode(), 0));
            // 对流
            add(new WeatherMapDO("对流|成片对流|分散对流", "TS", WeatherType.TS.getCode(), 1));

            // 以下是大强度的天气现象
            add(new WeatherMapDO("强降水|强降雨|大雨", "+RA", WeatherType.RA.getCode(), 0));
            add(new WeatherMapDO("短时大阵雨|大阵雨", "+SHRA", WeatherType.RA.getCode(), 0));
            add(new WeatherMapDO("大雷雨|强雷雨|大雷阵雨|强雷阵雨", "+TSRA", WeatherType.RA.getCode(), 0));
            // 中强度的天气现象
            add(new WeatherMapDO("降水|中间降水", "RA", WeatherType.RA.getCode(), 2));
            add(new WeatherMapDO("短时阵雨", "SHRA", WeatherType.RA.getCode(), 2));
        }
    };

    public static void main(String[] args) {
        ForecastData weatherMap = new ForecastData();
        List<Value> wea = weatherMap.map("雷雨02-9米/秒，02-9米/秒的小阵雨", weatherMapDOS);
        Value<MaxAndMin> handeler = weatherMap.handeler(wea, WeatherType.CLOUD, new MaxAndMin(), ForecastData::integerHandeler);
        /**
         * 降雨
         */
        Value<MaxAndMin> RA = weatherMap.handeler(wea, WeatherType.RA, new MaxAndMin(), ForecastData::integerHandeler);
        /**
         * 风
         */
        Value<MaxAndMin> wind = weatherMap.handeler(wea, WeatherType.WIND, new MaxAndMin(), ForecastData::integerHandeler);
        /**
         * 冰雹
         */
        Value<MaxAndMin> GR = weatherMap.handeler(wea, WeatherType.GRGS, new MaxAndMin(), ForecastData::integerHandeler);
        MaxAndMin data = handeler.getData();
    }

    /**
     * 首先预测数据分为很多 就目前来讲 分为String类型数据 以及Integer数据
     * 目前考虑 当前类就放原始的元素字符串
     */
    private String forecastData;


    /**
     * 根据天气现象对天气现象进行映射
     *
     * @param weather
     * @return
     */
    public List<Value> map(String weather, List<WeatherMapDO> weatherMapDOS) {
        //
        weatherMapDOS = weatherMapDOS.stream().sorted(Comparator.comparing(WeatherMapDO::getWeatherType).thenComparing(WeatherMapDO::getOrder)).collect(Collectors.toList());
        String[] findWeather = {weather};
        List<Value> collect = weatherMapDOS.stream().map(s -> {
            Value value = new Value();
            String va = MatchUtil.get(findWeather[0], s.getWeatherPattern());
            if (StringUtils.isNotEmpty(va)) {
                findWeather[0] = findWeather[0].replace(va, "");
                value.setType(s.getWeatherType());
                value.setWeather(s.getWeatherCode());
                value.setChineseWeather(va);
                return value;
            }
            return null;
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 天气现象处理器  处理不用类型的天气现象
     *
     * @param values      所有的预测天气现象
     * @param weatherType 天气现象类型
     * @param t           需要解析成什么类型的天气现象 class
     * @param handler     处理器封装不同天气的处理器
     * @param <T>         代表着不同天气类型
     * @return
     */
    public <T> Value<T> handeler(List<Value> values, WeatherType weatherType, T t, Function<Value<T>, Value<T>> handler) {
        Optional<Value> first = values.stream().filter(s -> {
            return s.getType().equals(weatherType.getCode());
        }).findFirst();
        if (first.isPresent()) {
            Value<T> value = first.get();
            value.setData(t);
            return handler.apply(value);
        }
        return null;
    }

    /**
     * 数值处理器
     * 根据预报 如  低云 20-30 解析当前对象
     *
     * @param value 当前值
     * @param <T>   类型
     * @return
     */
    public static <T extends MaxAndMin> Value<T> integerHandeler(Value<T> value) {
        MaxAndMin data = value.getData();
        List<Integer> collect = MatchUtil.getList(value.getChineseWeather(), "\\d+", new ArrayList<>()).stream().map(e -> {
            return new Integer(e);
        }).collect(Collectors.toList());
        data.setCode(value.getWeather());
        if (CollectionUtils.isEmpty(collect)) {
            return value;
        }
        data.setMax(collect.stream().max(Integer::compareTo).get());
        data.setMin(collect.stream().min(Integer::compareTo).get());
        return value;
    }

    /**
     * 天气现象值
     *
     * @param <T>
     */
    @Data
    public static class Value<T> {
        /**
         * 原始值
         */
        String chineseWeather;
        /**
         *
         */
        String weather;
        /**
         * 当前天气现象类型 降雨 雷暴 降雪 冻降水
         */
        String type;

        T data;
    }

    /**
     * 含有数据的最大值最小值
     */
    @Data
    static class MaxAndMin {
        /**
         * 当前天气code
         */
        String code;
        /**
         * 当前天气如果有数值 则有最大值最小值
         */
        Integer max;
        Integer min;

        /**
         * 当前天气是否有最大值最小值
         *
         * @return
         */
        public boolean isInteger() {
            return max != null && min != null;
        }
    }
}