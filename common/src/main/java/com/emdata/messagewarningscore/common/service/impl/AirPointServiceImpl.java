package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.dao.AirPointMapper;
import com.emdata.messagewarningscore.common.dao.entity.AirPointDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoPointDO;
import com.emdata.messagewarningscore.common.enums.RangeTypeEnum;
import com.emdata.messagewarningscore.common.service.IAirPointService;
import com.emdata.messagewarningscore.common.service.ILocationInfoPointService;
import com.emdata.messagewarningscore.common.service.ILocationInfoService;
import com.emdata.messagewarningscore.common.service.bo.PointRangeBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
@Service
public class AirPointServiceImpl extends ServiceImpl<AirPointMapper, AirPointDO> implements IAirPointService {
    @Autowired
    private ILocationInfoService locationInfoService;
    @Autowired
    private ILocationInfoPointService locationInfoPointService;

    @Override
    public List<PointRangeBO> getPoint(Integer locationId) {
        // 查询到当前地点的所有雷暴区域
        List<LocationInfoDO> list = locationInfoService.list(new LambdaQueryWrapper<LocationInfoDO>().eq(LocationInfoDO::getLocationId, locationId));
        // 终端区面积
        List<PointRangeBO> getsurface = getsurface(list);
        return getsurface;
    }

    /**
     * 根据返回  获取点和线
     *
     * @param surface
     * @return
     */
    private List<PointRangeBO> getsurface(List<LocationInfoDO> surface) {
        List<PointRangeBO> collect = surface.stream().map(s -> {
            Integer id = s.getId();
            // 一个范围所有的点
            List<AirPointDO> pointDOS = locationInfoPointService.list(new LambdaQueryWrapper<LocationInfoPointDO>().
                    eq(LocationInfoPointDO::getLocationInfoId, id)).
                    stream().
                    sorted(Comparator.comparing(LocationInfoPointDO::getNumber)).
                    map(c -> {
                        String pointUuid = c.getPointUuid();
                        AirPointDO one = this.getOne(new LambdaQueryWrapper<AirPointDO>().eq(AirPointDO::getUuid, pointUuid));
                        return one;
                    }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(pointDOS)) {
                return null;
            }
            PointRangeBO pointRangeBO = new PointRangeBO();
            pointRangeBO.setAll(pointDOS);
            pointRangeBO.setName(s.getName());
            pointRangeBO.setRangeTypeEnum(RangeTypeEnum.build(s.getRangeType()));
            pointRangeBO.setWide(s.getWidth());
            pointRangeBO.setLocationInfoId(s.getId());
            return pointRangeBO;
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());
        return collect;
    }
}
