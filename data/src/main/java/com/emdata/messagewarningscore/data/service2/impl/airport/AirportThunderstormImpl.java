package com.emdata.messagewarningscore.data.service2.impl.airport;

import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.data.service2.AirportWeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Desc AirportThunderstormImpl
 * @Author lihongjiang
 * @Date 2020/12/24 16:33
 **/
@Slf4j
@Service
public class AirportThunderstormImpl implements AirportWeatherService {

    @Override
    public List<AlertContentResolveDO> parseTextContent(AlertOriginalTextDO airportAlert) {
        return null;
    }
}
