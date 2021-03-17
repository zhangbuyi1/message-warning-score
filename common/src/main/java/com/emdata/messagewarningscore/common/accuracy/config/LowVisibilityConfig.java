package com.emdata.messagewarningscore.common.accuracy.config;/**
 * Created by zhangshaohu on 2020/12/21.
 */

import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description: 低云低能见度配置文件
 */
@Data
public class LowVisibilityConfig extends AirportCode {
    /**
     * 低云高或垂直能见度＜60 米且云量在
     * BKN
     */
    private Integer lowBknHeight = 60;
    /**
     * （和）低能见度＜800 米。
     */
    private Integer lowVis = 800;
    /**
     * 跑道视尘
     */
    private Integer lowRvr = 500;
    /**
     * 接近阈值百分比
     */
    private Integer nearThreshold = 20;

}