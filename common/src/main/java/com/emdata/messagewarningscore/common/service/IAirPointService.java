package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.AirPointDO;
import com.emdata.messagewarningscore.common.service.bo.PointRangeBO;

import java.util.List;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
public interface IAirPointService extends IService<AirPointDO> {
    List<PointRangeBO> getPoint(Integer locationId);
}
