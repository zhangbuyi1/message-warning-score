package com.emdata.messagewarningscore.common.service.impl;/**
 * Created by zhangshaohu on 2021/1/6.
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.accuracy.enums.WarningNature;
import com.emdata.messagewarningscore.common.common.utils.JudgeUtil;
import com.emdata.messagewarningscore.common.dao.AlertContentResolveMapper;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.service.IAlertContentService;
import com.emdata.messagewarningscore.common.service.ILocationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/6
 * @description:
 */
@Service
public class AlertContentServiceImpl extends ServiceImpl<AlertContentResolveMapper, AlertContentResolveDO> implements IAlertContentService {
    @Autowired
    private ILocationInfoService locationInfoService;

    @Override
    public List<AlertContentResolveDO> selectAlertByTime(Date startTime, Date endTime, WarningTypeEnum warningTypeEnum, Integer locationId, Integer field, Integer num) {
        List<LocationInfoDO> list1 = locationInfoService.list(new LambdaQueryWrapper<LocationInfoDO>().eq(LocationInfoDO::getLocationId, locationId));
        Date finalStartTime = JudgeUtil.add(startTime, field, num * -1);
        Date finalEndTime = JudgeUtil.add(startTime, field, num * 1);
        List<AlertContentResolveDO> collect = list1.stream().flatMap(s -> {
            LambdaQueryWrapper<AlertContentResolveDO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(AlertContentResolveDO::getLocationInfoId, s.getId());
            lqw.eq(AlertContentResolveDO::getWarningType, warningTypeEnum.getWarningType());
            lqw.ge(AlertContentResolveDO::getPredictStartTime, finalStartTime);
            lqw.le(AlertContentResolveDO::getPredictEndTime, finalEndTime);
            List<AlertContentResolveDO> list = this.list(lqw);
            if (CollectionUtils.isNotEmpty(list)) {
                return null;
            }
            return list.stream();
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<AlertContentResolveDO> selectIsScoreAlertContent(WarningTypeEnum warningTypeEnum, Integer field, Integer num) {
        Date endTime = JudgeUtil.add(new Date(), field, num);
        LambdaQueryWrapper<AlertContentResolveDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AlertContentResolveDO::getWarningType, warningTypeEnum.getCode());
        lqw.eq(AlertContentResolveDO::getWarningNature, WarningNature.FIRST.getValue());
        lqw.le(AlertContentResolveDO::getPredictEndTime, endTime);
        lqw.eq(AlertContentResolveDO::getStatus, 0);
        List<AlertContentResolveDO> list = this.list(lqw);
        return list.stream().filter(s -> {
            return !s.getWeatherType().equals(5);
        }).collect(Collectors.toList());
    }


}