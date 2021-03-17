package com.emdata.messagewarningscore.data.service2;

import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;

import java.util.List;

/**
 * @Desc AirportWeatherService
 * @Author lihongjiang
 * @Date 2020/12/24 16:35
 **/
public interface AirportWeatherService {

    List<AlertContentResolveDO> parseTextContent(AlertOriginalTextDO airportAlert);

}
