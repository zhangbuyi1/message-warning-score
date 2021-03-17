package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.dao.RadarInfoMapper;
import com.emdata.messagewarningscore.common.dao.entity.RadarInfoDO;
import com.emdata.messagewarningscore.common.service.IRadarInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
@Service
public class RadarInfoServiceImpl extends ServiceImpl<RadarInfoMapper, RadarInfoDO> implements IRadarInfoService {

    @Autowired
    private RadarInfoMapper radarInfoMapper;

    @Override
    public RadarInfoDO findByAirportCode(String airportCode) {
        LambdaQueryWrapper<RadarInfoDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RadarInfoDO::getAirportCode, airportCode);
        List<RadarInfoDO> radarInfoDOS = radarInfoMapper.selectList(lqw);
        if (CollectionUtils.isNotEmpty(radarInfoDOS)) {
            return radarInfoDOS.get(0);
        }
        return null;
    }
}
