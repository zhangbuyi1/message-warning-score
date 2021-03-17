package com.emdata.messagewarningscore.common.accuracy.entity;/**
 * Created by zhangshaohu on 2020/12/21.
 */

import lombok.Data;

import java.util.Date;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description: 计算 准确率的数据类
 */
@Data
public class AccuracyEntity extends ForecastDate {

    /**
     * 预警类型：首份first、更新update
     */
    protected String warningNature;

    protected String predictProbility;

    public void set(Date startForcastTime, Date endForcastTime, Date startLiveTime, Date endLiveTime, Date sendTime, String warningNature) {
        this.startForcastTime = startForcastTime;
        this.endForcastTime = endForcastTime;
        this.startLiveTime = startLiveTime;
        this.endLiveTime = endLiveTime;
        this.sendTime = sendTime;
        this.warningNature = warningNature;
    }

    public AccuracyEntity(Date startForcastTime, Date endForcastTime, Date startLiveTime, Date endLiveTime, Date sendTime, String warningNature) {
        this.startForcastTime = startForcastTime;
        this.endForcastTime = endForcastTime;
        this.startLiveTime = startLiveTime;
        this.endLiveTime = endLiveTime;
        this.sendTime = sendTime;
        this.warningNature = warningNature;
    }

    public AccuracyEntity() {

    }

}