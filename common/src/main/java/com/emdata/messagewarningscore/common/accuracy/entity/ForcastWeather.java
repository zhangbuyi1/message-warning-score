package com.emdata.messagewarningscore.common.accuracy.entity;
/**
 * Created by zhangshaohu on 2021/1/4.
 */

import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description: 预测天气现象 短时
 */
@Data
public class ForcastWeather {
    /**
     * 描述
     */
    private String describe;
    /**
     * code码
     */
    private String code;
    /**
     * 最小值
     */
    private Integer min;
    /**
     * 最大值
     */
    private Integer max;

    /**
     * 优先级
     */
    private Integer priority;

    private Integer shortMin;

    private Integer shortMax;
}