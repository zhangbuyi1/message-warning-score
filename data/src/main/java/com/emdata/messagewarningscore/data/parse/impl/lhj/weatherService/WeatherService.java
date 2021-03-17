package com.emdata.messagewarningscore.data.parse.impl.lhj.weatherService;

import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;

/**
 * @Desc WeatherService 天气服务接口
 * @Author lihongjiang
 * @Date 2020/12/25 11:22
 **/
public interface WeatherService {

    /**
     * 解析文本天气
     *
     * @param airportAlert 原始文本
     * @return 文本天气参数对象
     **/
    AlertContentResolveDO parseTextWeather(AlertOriginalTextDO airportAlert);

}
