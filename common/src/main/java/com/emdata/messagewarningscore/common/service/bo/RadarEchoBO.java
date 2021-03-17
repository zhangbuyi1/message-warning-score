package com.emdata.messagewarningscore.common.service.bo;/**
 * Created by zhangshaohu on 2021/1/14.
 */

import com.emdata.messagewarningscore.common.enums.RangeTypeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/14
 * @description:
 */
@Data
public class RadarEchoBO {
    /**
     * 范围类型
     */
    private RangeTypeEnum rangeTypeEnum;
    /**
     * 雷达回波
     */
    private List<Double> radarRange;
    /**
     * 图片时间
     */
    private Date time;
    /**
     * 名称
     */
    private String name;

    private Integer locationInfoId;
}