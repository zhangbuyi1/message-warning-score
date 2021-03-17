package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
@Data
@TableName("air_point")
public class AirPointDO {

    @TableId(value = "uuid", type = IdType.AUTO)
    private String uuid;

    private String airPointEname;

    private String airPointCname;

    private String airPointNumber;

    private Double longitude;

    private Double latitude;

    private Integer type;


}
