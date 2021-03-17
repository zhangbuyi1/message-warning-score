package com.emdata.messagewarningscore.data.radar.handler.impl;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.emdata.messagewarningscore.data.radar.bo.TimeRangeRadar;
import com.emdata.messagewarningscore.data.radar.handler.RadarHandler;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description: 一个雷达文件 生成一张图片
 */
@Service("AnRadarHandler")
public class AnRadarHandler implements RadarHandler {
    TimeRangeRadar timeRangeRadar = null;

    @Override
    public List<List<TimeRangeRadar.TimeAnRangeRadar>> getRadarSourceData(String station, String dataType, String url, int filed, int num) {
        if (timeRangeRadar == null) {
            timeRangeRadar = new TimeRangeRadar(url);
        } else {
            /**
             * 暂时先不清理 因为没有实时数据
             */
//            timeRangeRadar.filterScrapData();
            timeRangeRadar.add(url);
        }
        return timeRangeRadar.consumerAn();
    }
}