package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emdata.messagewarningscore.common.handler.RadarHandler;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @date: 2021/1/15
 * @author: sunming
 */
@Data
@TableName("radar_data")
@Builder
public class RadarDataDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer locationId;

    private String picId;

    private Date picTime;
    @TableField(typeHandler = RadarHandler.class)
    private List<RadarEchoDO> data;


}
