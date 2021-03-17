package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.RadarInfoDO;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
public interface IRadarInfoService extends IService<RadarInfoDO> {


    RadarInfoDO findByAirportCode(String airportCode);
}
