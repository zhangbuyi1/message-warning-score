package com.emdata.messagewarningscore.common.accuracy.entity;/**
 * Created by zhangshaohu on 2020/12/29.
 */

/**
 * @author: zhangshaohu
 * @date: 2020/12/29
 * @description: 预测预警数据 集成时间以及报文数据类
 */
public class ForecastEntity extends AccuracyEntity {
    /**
     * 地点信息
     */
    private Place place;

    /**
     * 数据信息
     */
    protected String data;
    /**
     * 当前类型
     */
    protected String evaluationType;


}