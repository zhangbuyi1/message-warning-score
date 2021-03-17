package com.emdata.messagewarningscore.data.radar.handler.impl;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.emdata.messagewarningscore.data.radar.bo.TimeRangeRadar;
import com.emdata.messagewarningscore.data.radar.handler.RadarHandler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description: 按照时间 每N分钟生成M个文件 使用N个文件 生成
 */
@Service("TimeRadarHandler")
public class TimeRadarHandler implements RadarHandler {
    private TimeRangeRadar timeRangeRadar = null;
    /**
     * 检测到超过三天的数据未入库 则为废弃数据
     */
    private long scrapDataTime = 1000 * 60 * 60 * 24 * 3;
    /**
     * 最后一次检测时间
     */
    private Date scrapDataLastTime = new Date();

    @Override
    public List<List<TimeRangeRadar.TimeAnRangeRadar>> getRadarSourceData(String airport, String dataType, String url, int filed, int num) {
        if (this.timeRangeRadar == null) {
            this.timeRangeRadar = new TimeRangeRadar(filed, num);
        }
        Date thisDate = new Date();
        // 每三天清洗一次数据 每次清洗三天的数据
        if (thisDate.getTime() - scrapDataLastTime.getTime() > scrapDataTime) {
            timeRangeRadar.filterScrapData();
            scrapDataLastTime = thisDate;
        }
        timeRangeRadar.add(url);
        return timeRangeRadar.consumer();
    }
}