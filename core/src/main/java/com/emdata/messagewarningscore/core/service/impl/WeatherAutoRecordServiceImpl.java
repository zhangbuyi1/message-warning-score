package com.emdata.messagewarningscore.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.SeriousMapper;
import com.emdata.messagewarningscore.common.dao.WeatherAutoRecordMapper;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.dao.entity.WeatherAutoRecordDO;
import com.emdata.messagewarningscore.common.enums.WeatherStrengthEnum;
import com.emdata.messagewarningscore.common.service.ILocationService;
import com.emdata.messagewarningscore.core.controller.vo.WeatherAutoRecordQuery;
import com.emdata.messagewarningscore.core.service.IWeatherAutoRecordService;
import com.emdata.messagewarningscore.core.service.bo.WeatherAutoRecordBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description: 重要天气自动记录服务实现类
 * @date: 2020/12/14
 * @author: sunming
 */
@Service
public class WeatherAutoRecordServiceImpl extends ServiceImpl<WeatherAutoRecordMapper, WeatherAutoRecordDO> implements IWeatherAutoRecordService {

    @Autowired
    private SeriousMapper seriousMapper;

    @Autowired
    private ILocationService iLocationService;

    @Override
    public List<WeatherAutoRecordBO> weatherAutoRecordPage(String airportCode, WeatherAutoRecordQuery param) {
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        List<SeriousDO> doList;
        // 如果没有选择查询日期，默认展示最近3天
        if (StringUtils.isEmpty(startDate)) {
            Date date = new Date();
            Date pass3dayDate = Java8DateUtils.addHour(date, -(long) (3 * 24));
            String pass3Ymd = Java8DateUtils.format(pass3dayDate, Java8DateUtils.DATE);
            doList = findBetweenDate(airportCode, pass3Ymd, Java8DateUtils.format(date, Java8DateUtils.DATE));
        } else {
            doList = findBetweenDate(airportCode, startDate, endDate);
        }
        List<WeatherAutoRecordBO> bos = doList.stream().map(s -> {
            WeatherAutoRecordBO weatherAutoRecordBO = new WeatherAutoRecordBO();
            BeanUtils.copyProperties(s, weatherAutoRecordBO);
            weatherAutoRecordBO.setImportantWeather(s.getSeriousWeather().getMessage());
            weatherAutoRecordBO.setWeather(s.getWeather());
            weatherAutoRecordBO.setWithWeather(s.getWeather());
            LocationDO locationDO = iLocationService.getById(s.getLocationId());
            String locationName = Optional.ofNullable(locationDO).map(LocationDO::getLocationName).orElse("");
            weatherAutoRecordBO.setAffectedRange(locationName);
            weatherAutoRecordBO.setStrength(WeatherStrengthEnum.getCode("强"));
            return weatherAutoRecordBO;
        }).collect(Collectors.toList());
        return bos;
    }

    /**
     * 获取指定日期内的重要天气自动记录数据
     *
     * @param airportCode 机场代码
     * @param startDay    开始日期 yyyy-MM-dd
     * @param endDay      结束日期 yyyy-MM-dd
     * @return
     */
    public List<SeriousDO> findBetweenDate(String airportCode, String startDay, String endDay) {
        List<SeriousDO> seriousDOS = seriousMapper.findBetweenDate(airportCode, startDay, endDay);
        if (CollectionUtils.isEmpty(seriousDOS)) {
            return Collections.emptyList();
        }
        return seriousDOS;
    }

}
