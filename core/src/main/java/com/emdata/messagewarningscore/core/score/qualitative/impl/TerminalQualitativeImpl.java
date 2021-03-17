package com.emdata.messagewarningscore.core.score.qualitative.impl;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.common.utils.JudgeUtil;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;
import com.emdata.messagewarningscore.common.warning.entity.HitData;
import com.emdata.messagewarningscore.core.score.handler.AlertcontentMainWeatherHandler;
import com.emdata.messagewarningscore.core.score.qualitative.Qualitative;
import com.emdata.messagewarningscore.core.score.qualitative.abs.QualitativeAbs;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description: 终端区 空报 漏报 终端区只有终端区雷暴
 */
@Service("TerminalQualitativeImpl")
public class TerminalQualitativeImpl extends QualitativeAbs implements Qualitative, AlertcontentMainWeatherHandler {
    @Override
    public NatureEnum runLeak(SeriousDO seriousDO) {
        // 查询到预测所有重要天气
        List<AlertContentResolveDO> alertContentResolveDOS = iAlertContentService.selectAlertByTime(seriousDO.getStartTime(), seriousDO.getEndTime(), WarningTypeEnum.MDRS_WARNING, seriousDO.getLocationId(), Calendar.HOUR_OF_DAY, 4);
        // 判断是否漏报
        Optional<AlertContentResolveDO> first = isHit(seriousDO, alertContentResolveDOS);
        if (first.isPresent()) {
            return NatureEnum.HIT;
        }
        return NatureEnum.LEAK;
    }


    @Override
    public HitData runHitOrEmpty(ForcastData forcastData) {
        // 获得所有重要天气现象
        List<SeriousDO> seriousDOS = seriousService.selectAlertByTime(forcastData.getStartTime(), forcastData.getEndTime(), forcastData.getLocationInfoId(), Calendar.HOUR_OF_DAY, 2);
        List<SeriousDO> live = seriousDOS.stream().filter(s -> {
            return s.getSeriousWeather().equals(forcastData.getEvaluationWeather());
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(live)) {
            return HitData.buildHit(forcastData, live);
        }
        return runPartHit(forcastData);
    }

    @Override
    public HitData runPartHit(ForcastData forcastData) {
        // 获得重要雷暴天气
        List<SeriousBO> tsToSerious = radarDataService.getTsToSerious(forcastData.getLocationId(), JudgeUtil.add(forcastData.getStartTime(), Calendar.HOUR_OF_DAY, -2),
                JudgeUtil.add(forcastData.getEndTime(), Calendar.HOUR_OF_DAY, 2), true);
        List<SeriousDO> live = tsToSerious.stream().filter(s -> {
            return s.getSeriousWeather().equals(forcastData.getEvaluationWeather());
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