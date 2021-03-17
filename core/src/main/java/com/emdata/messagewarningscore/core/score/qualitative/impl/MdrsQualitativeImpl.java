package com.emdata.messagewarningscore.core.score.qualitative.impl;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.accuracy.config.OtherConfig;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
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
;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description: mdirs 的  空报 漏报 命中判断
 */
@Service("MdrsQualitativeImpl")
public class MdrsQualitativeImpl extends QualitativeAbs implements Qualitative, AlertcontentMainWeatherHandler {


    /**
     * 实况出现了某种天气，实况时段前后 4h 内预测没有出现则为漏报
     *
     * @param seriousDO
     * @return
     */
    @Override
    public NatureEnum runLeak(SeriousDO seriousDO) {
        // 查询到预测所有重要天气
        List<AlertContentResolveDO> alertContentResolveDOS = iAlertContentService.selectAlertByTime(seriousDO.getStartTime(), seriousDO.getEndTime(), WarningTypeEnum.MDRS_WARNING, seriousDO.getLocationId(), Calendar.HOUR_OF_DAY, 4);
        // 判断是否漏报
        Optional<AlertContentResolveDO> first = isHit(seriousDO, alertContentResolveDOS);
        if (first.isPresent()) {
            return NatureEnum.HIT;
        }
        // 查看是否在繁忙时段 是否达到漏报要求
        if (isNotScore(seriousDO)) {
            return NatureEnum.NOT_SCORE;
        }
        return NatureEnum.LEAK;
    }

    /**
     * *实际在机场相对繁忙时段
     * （06:00-22:00）出现持续 1 小时以上，
     * 其他时段出现持续 2
     * 小时以上该天气则为漏报
     *
     * @param seriousDO
     * @return
     */
    private boolean isNotScore(SeriousDO seriousDO) {
        OtherConfig otherConfig = new OtherConfig();
        Date startTime = seriousDO.getStartTime();
        Date endTime = seriousDO.getEndTime();
        OtherConfig.TimeInterval peakTime = otherConfig.getPeakTime();
        Calendar instance = Calendar.getInstance();
        instance.setTime(startTime);
        int startHour = instance.get(Calendar.HOUR_OF_DAY);
        int startMin = instance.get(Calendar.MINUTE);
        int startSecond = instance.get(Calendar.SECOND);
        instance.setTime(endTime);
        int endHour = instance.get(Calendar.HOUR_OF_DAY);
        int endMin = instance.get(Calendar.MINUTE);
        int endSecond = instance.get(Calendar.SECOND);
        boolean inStart = in(startHour, startMin, startSecond, endHour, endMin, endSecond, peakTime.getStartHour(), peakTime.getStartMin(), peakTime.getStartSecond());
        boolean inEnd = in(startHour, startMin, startSecond, endHour, endMin, endSecond, peakTime.getEndHour(), peakTime.getEndMin(), peakTime.getEndSecond());
        if (inStart || inEnd) {
            return true;
        }
        return false;
    }

    public boolean in(int startHour, int startMin, int startSecond, int endHour, int endMin, int endSecond, int hour, int min, int second) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, startHour);
        start.set(Calendar.MINUTE, startMin);
        start.set(Calendar.SECOND, startSecond);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, endHour);
        end.set(Calendar.MINUTE, endMin);
        end.set(Calendar.SECOND, endSecond);

        Calendar thisC = Calendar.getInstance();
        thisC.set(Calendar.HOUR_OF_DAY, hour);
        thisC.set(Calendar.MINUTE, min);
        thisC.set(Calendar.SECOND, second);
        if (start.getTimeInMillis() <= thisC.getTimeInMillis() && end.getTimeInMillis() >= thisC.getTimeInMillis()) {
            return true;
        }
        return false;
    }

    /**
     * 预报了某种天气，预报时段前后 4h 内实际没有出
     * 现该天气为空报；
     *
     * @param forcastData
     * @return
     */
    @Override
    public HitData runHitOrEmpty(ForcastData forcastData) {
        EvaluationWeather type = forcastData.getEvaluationWeather();
        // 获得所有重要天气现象
        List<SeriousDO> seriousDOS = seriousService.selectAlertByTime(forcastData.getStartTime(), forcastData.getEndTime(), forcastData.getLocationId(), Calendar.HOUR_OF_DAY, 4);
        List<SeriousDO> live = seriousDOS.stream().filter(s -> {
            /**
             *实际在机场相对繁忙时段
             （06:00-22:00）出现持续 1 小时以上，其他时段出现持续 2
             小时以上该天气则为漏报
             */
            return s.getSeriousWeather().equals(type);
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(live)) {
            return HitData.buildHit(forcastData, live);
        }
        return runPartHit(forcastData);
    }

    @Override
    public HitData runPartHit(ForcastData forcastData) {
        EvaluationWeather type = forcastData.getEvaluationWeather();
        Date start = JudgeUtil.add(forcastData.getStartTime(), Calendar.HOUR_OF_DAY, -4);
        Date end = JudgeUtil.add(forcastData.getEndTime(), Calendar.HOUR_OF_DAY, 4);
        List<SeriousBO> seriousBOS = new ArrayList<>();
        // 获得实况
        if (type.equals(EvaluationWeather.TS)) {
            seriousBOS = radarDataService.getTsToSerious(forcastData.getLocationId(), start, end, true);

        } else {
            seriousBOS = metarSourceService.getMetarSourceToSerious(forcastData.getAirportCode(), start, end, true);
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