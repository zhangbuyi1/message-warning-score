package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description: 地区表
 */
@Data
@TableName("area")
public class AreaDO {

    @TableId(value = "area_id", type = IdType.AUTO)
    @ApiModelProperty(example = "1")
    private Integer areaId;

    /**
     * 地区编码
     */
    private String areaCode;

    /**
     * 地区名
     */
    private String areaName;

    /**
     * 地区级别（1:省份province,2:市city,3:区县district,4:街道street）
     */
    @ApiModelProperty(example = "1")
    private Integer level;

    /**
     * 地区上级编码
     */
    private String parentCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 经纬度坐标
     */
    private String center;

    /**
     * 地区父节点
     */
    @ApiModelProperty(example = "1")
    private Integer parentId;
}
