package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.RadarDataDO;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @date: 2021/1/15
 * @author: sunming
 */
public interface IRadarDataService extends IService<RadarDataDO> {
    /**
     * 获得一个地点的雷达反射率超过35dbz以上的超过覆盖率百分之20
     *
     * @param locationId 地点id
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param ifFloating 是否需要
     * @return
     */
    List<SeriousBO> getTsToSerious(Integer locationId, Date startTime, Date endTime, boolean ifFloating);
}
