package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.dao.LocationInfoMapper;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;
import com.emdata.messagewarningscore.common.service.ILocationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
@Service
public class LocationInfoServiceImpl extends ServiceImpl<LocationInfoMapper, LocationInfoDO> implements ILocationInfoService {

    @Autowired
    private LocationInfoMapper locationInfoMapper;

    /**
     * 根据雷达uuid查询出地点信息
     *
     * @param radarInfoUuid 雷达uuid
     * @return
     */
    @Override
    public List<LocationInfoDO> findByRadarUuid(String radarInfoUuid) {
        LambdaQueryWrapper<LocationInfoDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(LocationInfoDO::getRadarInfoUuid, radarInfoUuid);
        List<LocationInfoDO> locationInfoDOS = locationInfoMapper.selectList(lqw);
        return locationInfoDOS;
    }

    @Override
    public LocationInfoDO findByName(String name) {
        LambdaQueryWrapper<LocationInfoDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(LocationInfoDO::getName, name);
        List<LocationInfoDO> locationInfoDOS = locationInfoMapper.selectList(lqw);
        if (CollectionUtils.isNotEmpty(locationInfoDOS)) {
            return locationInfoDOS.get(0);
        }
        return null;
    }
}
