package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.dao.LocationInfoPointMapper;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoPointDO;
import com.emdata.messagewarningscore.common.service.ILocationInfoPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
@Service
public class LocationInfoPointServiceImpl extends ServiceImpl<LocationInfoPointMapper, LocationInfoPointDO> implements ILocationInfoPointService {

    @Autowired
    private LocationInfoPointMapper locationInfoPointMapper;

    @Override
    public List<LocationInfoPointDO> findByLocationId(Integer locationId) {
        LambdaQueryWrapper<LocationInfoPointDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(LocationInfoPointDO::getLocationInfoId, locationId);
        List<LocationInfoPointDO> locationInfoPointDOS = locationInfoPointMapper.selectList(lqw);
        return locationInfoPointDOS;
    }
}
