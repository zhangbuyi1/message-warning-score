package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.AirportDO;

/**
 * @description:
 * @date: 2020/12/17
 * @author: sunming
 */
public interface IAirportService extends IService<AirportDO> {

    AirportDO findByAirportCode(String airportCode);
}
