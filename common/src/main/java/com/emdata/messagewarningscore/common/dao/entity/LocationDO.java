package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description: 地点实体类
 * @date: 2021/1/11
 * @author: sunming
 */
@Data
@TableName("location")
public class LocationDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 地点code
     */
    private String locationCode;

    /**
     * 地点名称
     */
    private String locationName;

    /**
     * 机场code
     */
    private String airportCode;
    /**
     * 雷达id
     */
    private String radarInfoId;

    /**
     * 机场，终端区，进近区
     */
    private String type;
}
