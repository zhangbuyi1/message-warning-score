package com.emdata.messagewarningscore.core.score.evaluate.entity;/**
 * Created by zhangshaohu on 2020/12/22.
 */


import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.accuracy.entity.AccuracyEntity;
import com.emdata.messagewarningscore.common.accuracy.entity.ForecastData;
import lombok.Data;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/22
 * @description: 评分数据
 */
@Data
public class Accompany extends AccuracyEntity {
    /**
     * 预测的天气现象
     */
    private List<ForecastData.Value> forcastWeather;
    /**
     * 实况的天气现象
     */
    private List<Metar> metars;

    /**
     * 实况雷达回波数据
     * 还未接入
     */
    private String 雷达数据占位;

    public Accompany(AccuracyEntity accuracyEntity) {

    }

}