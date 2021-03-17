package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;

import java.util.List;

/**
 * @description:
 * @date: 2021/1/11
 * @author: sunming
 */
public interface ILocationService extends IService<LocationDO> {

    List<LocationDO> findByAirportCode(String airportCode);
}
