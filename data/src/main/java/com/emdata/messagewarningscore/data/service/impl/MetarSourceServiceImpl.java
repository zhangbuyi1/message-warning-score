package com.emdata.messagewarningscore.data.service.impl;/**
 * Created by zhangshaohu on 2020/12/29.
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.accuracy.config.OtherConfig;
import com.emdata.messagewarningscore.common.accuracy.config.ThresholdConfig;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.MetarSourceMapper;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.service.ILocationInfoService;
import com.emdata.messagewarningscore.common.service.ILocationService;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;
import com.emdata.messagewarningscore.data.service.IMetarSourceService;
import com.emdata.messagewarningscore.common.common.utils.SeriousUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/12/29
 * @description:
 */
@Service
public class MetarSourceServiceImpl extends ServiceImpl<MetarSourceMapper, MetarSourceDO> implements IMetarSourceService {
    @Autowired
    private ILocationService locationService;
    @Autowired
    private ILocationInfoService locationInfoService;

    @Override
    public List<Metar> getMetarSourceByTime(String airport, Date startTime, Date endTime) {
        List<MetarSourceDO> metarSource = getMetarSource(airport, startTime, endTime);
        return metarSource.stream().map(s -> {
            Metar metar = new Metar(s.getMetarSource(), s.getMetarTime());
            metar.setMetarTime(s.getMetarTime());
            return metar;
        }).collect(Collectors.toList());
    }

    /**
     * 查询到metar报文
     *
     * @param airport
     * @param startTime
     * @param endTime
     * @return
     */
    public List<MetarSourceDO> getMetarSource(String airport, Date startTime, Date endTime) {
        LambdaQueryWrapper<MetarSourceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MetarSourceDO::getAirportCode, airport);
        lqw.ge(MetarSourceDO::getMetarTime, startTime);
        lqw.le(MetarSourceDO::getMetarTime, endTime);
        List<MetarSourceDO> list = this.list(lqw);
        return list;
    }

    @Override
    public List<SeriousBO> getMetarSourceToSerious(String airport, Date startTime, Date endTime, boolean ifFloating) {
        List<SeriousBO> seriousBOS = new ArrayList<>();
        LocationDO one = locationService.getOne(new LambdaQueryWrapper<LocationDO>().eq(LocationDO::getAirportCode, airport).eq(LocationDO::getLocationCode, airport));
        // 获得该时间段内所有的实况报文
        List<MetarSourceDO> metarSource = getMetarSource(airport, startTime, endTime);
        // 排序
        metarSource = metarSource.stream().sorted(Comparator.comparing(MetarSourceDO::getMetarTime)).collect(Collectors.toList());
        // 顺序执行
        metarSource.stream().map(s -> {
            OtherConfig otherConfig = ThresholdConfig.getOtherConfig(airport);
            // 查看历史是否存在
            List<SeriousBO> history = getSeriousBO(airport, seriousBOS);
            // 当前重要天气
            List<SeriousBO> thisSocreThreshold = SeriousUtils.isSocreThreshold(s, one.getId(), ifFloating);
            SeriousUtils.runHandler(seriousBOS, history, s, thisSocreThreshold, () -> {
                return EvaluationWeather.LOW_VIS_LOWCLOUD;
            }, otherConfig.getSeriousInterval());
            SeriousUtils.runHandler(seriousBOS, history, s, thisSocreThreshold, () -> {
                return EvaluationWeather.RN_FZRA;
            }, otherConfig.getSeriousInterval());
            SeriousUtils.runHandler(seriousBOS, history, s, thisSocreThreshold, () -> {
                return EvaluationWeather.RA;
            }, otherConfig.getSeriousInterval());
            return s;
        }).collect(Collectors.toList());

        return seriousBOS;
    }


    /**
     * 返回重要天气
     *
     * @param airportCode
     * @param seriousBOS
     * @return
     */
    public static List<SeriousBO> getSeriousBO(String airportCode, List<SeriousBO> seriousBOS) {
        return seriousBOS.stream().filter(s -> {
            return s.getAirportCode().equals(airportCode) && s.getStatus().equals(0);
        }).collect(Collectors.toList());
    }


}