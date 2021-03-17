package com.emdata.messagewarningscore.core.score.handler;

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;

/**
 * @author: zhangshaohu
 * @date: 2021/1/27
 * @description: 处理短时数据
 */
public interface ShortForecastData {
    /**
     * 短时评分
     * 如果实况出现了短时情况 获得百分之15 的分
     * 如果满足在一般时间一下 且小于1小时 则获得百分之30
     *
     * @param forcastData 预测数据
     * @return
     */
    double shortScoreHandler(ForcastData forcastData);

    /**
     * 是否有短时 需要短时评估
     *只有低云低能见度才会出现短时
     * @param forcastData 预测数据
     * @return
     */
    ForcastWeather isShortScore(ForcastData forcastData);
}