package com.emdata.messagewarningscore.data.service2.impl.airport;

import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.data.service2.AirportWeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Desc AirportSnowfallImpl
 * @Author lihongjiang
 * @Date 2020/12/24 16:40
 **/
@Slf4j
@Service
public class AirportSnowfallImpl implements AirportWeatherService {

    @Override
    public List<AlertContentResolveDO> parseTextContent(AlertOriginalTextDO airportAlert) {
        return null;
    }

}
