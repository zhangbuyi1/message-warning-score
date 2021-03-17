package com.emdata.messagewarningscore.common.dao.entity;/**
 * Created by zhangshaohu on 2021/1/15.
 */

import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2021/1/15
 * @description:
 */
@Data
public class RadarEchoDO {

    /**
     * 雷达回波百分比
     */
    private Double radarPercentage;


    private Integer locationInfoId;
}