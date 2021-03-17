package com.emdata.messagewarningscore.data.parse.impl.lhj.common;

import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.data.parse.impl.lhj.weatherService.*;
import com.emdata.messagewarningscore.data.parse.impl.lhj.weatherService.impl.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommon.contentThreadLocal;

/**
 * @Desc AlertCommomAdapter
 * @Author lihongjiang
 * @Date 2021/1/15 9:51
 **/
@Component
public class AlertCommonAdapter {

    /**
     * 适配接口实现类
     *
     * @param alertContents 返回结果
     * @param airportAlert 原始文本对象
     **/
    public void adapterInterfaceImpl(List<AlertContentResolveDO> alertContents, AlertOriginalTextDO airportAlert, String areaName){
        // 待解析的内容
        String contentText = contentThreadLocal.get();
        // 适配接口实现类
        WeatherService airportService = null;
        if(contentText.contains("对流") || contentText.contains("雷雨") || contentText.contains("阵雨")
                || contentText.contains("降雨") || contentText.contains("降水")){
            airportService = new ThunderstormServiceImpl();
        }else if(contentText.contains("雪")){
            airportService = new SnowfallServiceImpl();
        }else if(contentText.contains("霜")){
            airportService = new FrostServiceImpl();
        }else if(contentText.contains("能见度") || contentText.contains("RVR") || contentText.contains("雾")){
            airportService = new VisibilityServiceImpl();
        }else if(contentText.contains("云底高") || contentText.contains("云高") || contentText.contains("低云")){
            airportService = new LowCloudServiceImpl();
        }else {
            if(contentText.contains("风")){
                airportService = new WindServiceImpl();
            }
        }
        if (null != airportService) {
            // 调用接口
            AlertContentResolveDO alertContentResolveDO = airportService.parseTextWeather(airportAlert);
            alertContentResolveDO.setAffectedArea(areaName);
            // 获取并添加警报内容
            alertContents.add(alertContentResolveDO);
        }
    }

}
