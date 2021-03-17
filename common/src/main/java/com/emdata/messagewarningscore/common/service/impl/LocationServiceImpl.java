package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.dao.LocationMapper;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.service.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @date: 2021/1/11
 * @author: sunming
 */
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, LocationDO> implements ILocationService {

    @Autowired
    private LocationMapper locationMapper;

    @Override
    public List<LocationDO> findByAirportCode(String airportCode) {
        LambdaQueryWrapper<LocationDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(LocationDO::getAirportCode, airportCode);
        return locationMapper.selectList(lqw);
    }
}
