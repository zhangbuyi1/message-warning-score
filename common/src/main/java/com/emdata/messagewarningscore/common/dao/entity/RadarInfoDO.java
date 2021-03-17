package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description:
 * @date: 2021/1/4
 * @author: sunming
 */
@Data
@TableName("radar_info")
public class RadarInfoDO {

    @TableId(value = "uuid")
    private String uuid;

    private String airportCode;

    /**
     * 雷达名称
     */
    private String radarName;

    /**
     * 雷达编号
     */
    private String radarNumber;

    private Double centerLongitude;

    private Double centerLatitude;

    /**
     * 半径(m)
     */
    private Double radius;

    private Double startLongitude;

    private Double endLongitude;
    /**
     * 雷达数据类型
     */
    private String dataType;

    private Double startLatitude;

    private Double endLatitude;

}
