package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoPointDO;

import java.util.List;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
public interface ILocationInfoPointService extends IService<LocationInfoPointDO> {

    /**
     * 根据地点id查询
     *
     * @param locationId
     * @return
     */
    List<LocationInfoPointDO> findByLocationId(Integer locationId);
}
