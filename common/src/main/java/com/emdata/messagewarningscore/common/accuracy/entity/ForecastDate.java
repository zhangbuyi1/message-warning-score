package com.emdata.messagewarningscore.common.accuracy.entity;/**
 * Created by zhangshaohu on 2020/12/29.
 */

import lombok.Data;

import java.util.Date;

/**
 * @author: zhangshaohu
 * @date: 2020/12/29
 * @description: 预测报文时间类
 */
@Data
public class ForecastDate {
    /**
     * 預測開始時間
     */
    protected Date startForcastTime;
    /**
     * 預測結束時間
     */
    protected Date endForcastTime;
    /**
     * 实况开始时间
     */
    protected Date startLiveTime;

    /**
     * 实况结束时间
     */
    protected Date endLiveTime;
    /**
     * 发布时间
     */
    protected Date sendTime;
}