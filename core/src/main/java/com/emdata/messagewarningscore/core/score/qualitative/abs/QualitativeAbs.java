package com.emdata.messagewarningscore.core.score.qualitative.abs;/**
 * Created by zhangshaohu on 2021/1/11.
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.service.*;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;
import com.emdata.messagewarningscore.core.score.handler.AlertcontentMainWeatherHandler;
import com.emdata.messagewarningscore.core.score.qualitative.Qualitative;
import com.emdata.messagewarningscore.data.service.IMetarSourceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/11
 * @description:
 */
public abstract class QualitativeAbs implements Qualitative, AlertcontentMainWeatherHandler {

    /**
     * 解析数据查询mapper
     */
    @Autowired
    protected IAlertContentService iAlertContentService;
    @Autowired
    protected ISeriousService seriousService;
    @Autowired
    protected IRadarDataService radarDataService;
    @Autowired
    protected ILocationInfoService locationInfoService;
    @Autowired
    protected IMetarSourceService metarSourceService;
    @Autowired
    private ILocationService locationService;


    /**
     * 判断是否漏报
     *
     * @param seriousDO
     * @param alertContentResolveDOS
     * @return
     */
    protected Optional<AlertContentResolveDO> isHit(SeriousDO seriousDO, List<AlertContentResolveDO> alertContentResolveDOS) {
        return alertContentResolveDOS.stream().filter(s -> {
            if (s.getPredictStartTime().getTime() <= seriousDO.getStartTime().getTime() && s.getPredictEndTime().getTime() >= seriousDO.getStartTime().getTime()) {
                return true;
            }
            if (s.getPredictStartTime().getTime() <= seriousDO.getEndTime().getTime() && s.getPredictEndTime().getTime() >= seriousDO.getEndTime().getTime()) {
                return true;
            }
            return false;
        }).findFirst();
    }

    @Override
    public List<ForcastData> resetAlertContent(List<AlertContentResolveDO> alertContentResolveDOS) {
        List<ForcastData> collect = alertContentResolveDOS.stream().map(s -> {
            return resetAlertContent(s);
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public ForcastData resetAlertContent(AlertContentResolveDO alertContentResolveDO) {
        LocationInfoDO locationInfoDO = locationInfoService.getOne(new LambdaQueryWrapper<LocationInfoDO>().eq(LocationInfoDO::getId, alertContentResolveDO.getLocationInfoId()));
        LocationDO locationDO = locationService.getOne(new LambdaQueryWrapper<LocationDO>().eq(LocationDO::getId, locationInfoDO.getLocationId()));
        ForcastData forcastData = new ForcastData(alertContentResolveDO, locationInfoDO, locationDO);

        Integer locationInfoId = forcastData.getLocationInfoId();
        LocationInfoDO one = locationInfoService.getOne(new LambdaQueryWrapper<LocationInfoDO>().eq(LocationInfoDO::getId, locationInfoId));

        List<SeriousBO> seriousDOS = radarDataService.getTsToSerious(one.getLocationId(), forcastData.getStartTime(), forcastData.getEndTime(), true);

        // 处理TS 和RA
        if (forcastData.getEvaluationWeather().equals(EvaluationWeather.TS)) {
            // 设置 默认重要天气为雷暴 如果当前实况雷暴大于35dbz则为雷暴 反之则为阵雨
            Optional<SeriousBO> first = seriousDOS.stream().filter(c -> {
                return c.getSeriousWeather().equals(EvaluationWeather.TS);
            }).findFirst();
            if (first.isPresent()) {
                forcastData.setEvaluationWeather(EvaluationWeather.TS);
            } else {
                forcastData.setEvaluationWeather(EvaluationWeather.RA);
            }
        }
        return forcastData;
    }


}