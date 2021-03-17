package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("airport")
public class AirportDO {

    @TableId
    private String uuid;

    /**
     * 机场名称
     */
    private String airportName;

    /**
     * 地名代码
     */
    private String airportCode;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 记录状态 0正常 1删除
     */
    private Integer status;

}