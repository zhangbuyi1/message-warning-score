package com.emdata.messagewarningscore.data.radar.handler;

import com.emdata.messagewarningscore.data.radar.bo.TimeRangeRadar;

import java.util.List;

/**
 * Created by zhangshaohu on 2021/1/19.
 * 根据雷达类型 返回一张图片所需要的数据
 */
public interface RadarHandler {
    List<List<TimeRangeRadar.TimeAnRangeRadar>> getRadarSourceData(String station, String dataType, String url, int filed, int num);
}
