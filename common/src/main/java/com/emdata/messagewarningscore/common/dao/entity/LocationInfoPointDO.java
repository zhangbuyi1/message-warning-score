package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description: 地点和点的中间表
 * @date: 2021/1/6
 * @author: sunming
 */
@Data
@TableName("location_info_point")
public class LocationInfoPointDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer locationInfoId;

    private String pointUuid;
    /**
     * 排序字段
     */
    private Integer number;
}
