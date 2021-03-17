package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.dao.AirportMapper;
import com.emdata.messagewarningscore.common.dao.entity.AirportDO;
import com.emdata.messagewarningscore.common.service.IAirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @date: 2020/12/17
 * @author: sunming
 */
@Service
public class AirportServiceImpl extends ServiceImpl<AirportMapper, AirportDO> implements IAirportService {

    @Autowired
    private AirportMapper airportMapper;


    @Override
    public AirportDO findByAirportCode(String airportCode) {
        LambdaQueryWrapper<AirportDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AirportDO::getAirportCode, airportCode);
        List<AirportDO> airportDOS = airportMapper.selectList(lqw);
        if (CollectionUtils.isNotEmpty(airportDOS)) {
            return airportDOS.get(0);
        }
        return null;
    }
}
