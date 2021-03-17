package com.emdata.messagewarningscore.core.score.qualitative.impl;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.common.utils.JudgeUtil;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;
import com.emdata.messagewarningscore.common.warning.entity.HitData;
import com.emdata.messagewarningscore.core.score.qualitative.Qualitative;
import com.emdata.messagewarningscore.core.score.qualitative.abs.QualitativeAbs;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description: 机场警报  漏报 空报 命中判断
 */
@Service("AirportQualitativeImpl")
public class AirportQualitativeImpl extends QualitativeAbs implements Qualitative {

    /**
     * 判断是否是漏报 或者命中
     * SeriousDO 属性只有中到大阵雨 低云低能见度 降雪冻降水
     *
     * @param seriousDO
     * @return
     */
    @Override
    public NatureEnum runLeak(SeriousDO seriousDO) {

        // 查询到预测所有重要天气
        List<AlertContentResolveDO> alertContentResolveDOS = iAlertContentService.selectAlertByTime(seriousDO.getStartTime(),
                seriousDO.getEndTime(),
                WarningTypeEnum.AIRPORT_WARNING,
                seriousDO.getLocationId(),
                Calendar.HOUR_OF_DAY, 2);
        // 判断是否漏报
        Optional<AlertContentResolveDO> first = isHit(seriousDO, alertContentResolveDOS);
        if (first.isPresent()) {
            return NatureEnum.HIT;
        }
        return NatureEnum.LEAK;
    }


    /**
     * 判断是否是空报 或者命中
     * 预报了某种天气，预报时段前后 2h 内实际出现了
     * 该天气为命中。
     *
     * @param forcastData
     * @return
     */
    @Override
    public HitData runHitOrEmpty(ForcastData forcastData) {
        EvaluationWeather type = forcastData.getEvaluationWeather();
        // 获得所有重要天气现象
        List<SeriousDO> seriousDOS = seriousService.selectAlertByTime(forcastData.getStartTime(), forcastData.getEndTime(), forcastData.getLocationInfoId(), Calendar.HOUR_OF_DAY, 2);
        List<SeriousDO> live = seriousDOS.stream().filter(s -> {
            return s.getSeriousWeather().equals(type);
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(live)) {
            return HitData.buildHit(forcastData, live);
        }
        // 判断部分准确
        return runPartHit(forcastData);
    }

    @Override
    public HitData runPartHit(ForcastData forcastData) {
        EvaluationWeather type = forcastData.getEvaluationWeather();
        Date start = JudgeUtil.add(forcastData.getStartTime(), Calendar.HOUR_OF_DAY, -2);
        Date end = JudgeUtil.add(forcastData.getEndTime(), Calendar.HOUR_OF_DAY, 2);
        List<SeriousBO> seriousBOS = new ArrayList<>();
        if (type.equals(EvaluationWeather.TS)) {
            seriousBOS = radarDataService.getTsToSerious(forcastData.getLocationId(), start, end, true);
        } else {
            // 获得所有重要天气现象
            seriousBOS = metarSourceService.getMetarSourceToSerious(forcastData.getAirportCode(), forcastData.getStartTime(), forcastData.getEndTime(), true);
        }
        List<SeriousDO> live = seriousBOS.stream().filter(s -> {
            return s.getSeriousWeather().equals(type);
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(live)) {
            return HitData.buildPartHit(forcastData, live);
        }
        return HitData.buildEmpty(forcastData);
    }


    @Override
    public HitData runHitOrEmpty(AlertContentResolveDO alertContentResolveDO) {
        ForcastData forcastData = resetAlertContent(alertContentResolveDO);
        return runHitOrEmpty(forcastData);
    }
}