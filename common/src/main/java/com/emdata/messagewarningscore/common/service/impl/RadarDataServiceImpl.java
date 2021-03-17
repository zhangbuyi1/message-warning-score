package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.accuracy.config.OtherConfig;
import com.emdata.messagewarningscore.common.accuracy.config.ThresholdConfig;
import com.emdata.messagewarningscore.common.common.utils.SeriousUtils;
import com.emdata.messagewarningscore.common.dao.RadarDataMapper;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.RadarDataDO;
import com.emdata.messagewarningscore.common.service.ILocationService;
import com.emdata.messagewarningscore.common.service.IRadarDataService;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description:
 * @date: 2021/1/15
 * @author: sunming
 */
@Service
public class RadarDataServiceImpl extends ServiceImpl<RadarDataMapper, RadarDataDO> implements IRadarDataService {
    @Autowired
    private ILocationService locationService;

    @Override
    public List<SeriousBO> getTsToSerious(Integer locationId, Date startTime, Date endTime, boolean ifFloating) {
        List<SeriousBO> all = new ArrayList<>();
        // 获得当前地区
        List<RadarDataDO> list = this.list(new LambdaQueryWrapper<RadarDataDO>().eq(RadarDataDO::getLocationId, locationId).ge(RadarDataDO::getPicTime, startTime).le(RadarDataDO::getPicTime, endTime));
        LocationDO one = locationService.getOne(new LambdaQueryWrapper<LocationDO>().eq(LocationDO::getId, locationId));
        OtherConfig otherConfig = ThresholdConfig.getOtherConfig(one.getAirportCode());
        list.stream().map(s -> {
            SeriousBO scoreThreshold = SeriousUtils.isScoreThreshold(s, one.getAirportCode(), ifFloating);
            SeriousBO history = getSeriousBO(one.getAirportCode(), all);
            if (history != null) {
                if (scoreThreshold == null) {
                    if (s.getPicTime().getTime() - history.getLastTime().getTime() > otherConfig.getSeriousInterval()) {
                        history.setEndTime(s.getPicTime());
                        history.setStatus(1);
                    }
                } else {
                    history.setEndTime(s.getPicTime());
                    history.setLastTime(s.getPicTime());
                }
            } else {
                if (scoreThreshold != null) {
                    all.add(scoreThreshold);
                }
            }
            return s;
        }).collect(Collectors.toList());
        return all;
    }

    /**
     * 返回重要天气
     *
     * @param airportCode
     * @param seriousBOS
     * @return
     */
    public static SeriousBO getSeriousBO(String airportCode, List<SeriousBO> seriousBOS) {
        Optional<SeriousBO> first = seriousBOS.stream().filter(s -> {
            return s.getAirportCode().equals(airportCode) && s.getStatus().equals(0);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }
}
