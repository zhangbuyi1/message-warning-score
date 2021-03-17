package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
@Data
@TableName("area_custom_place")
public class AreaCustomPlaceDO {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(example = "1")
    private Integer id;

    private String placeCode;

    private String placeName;

    private String parentCode;

    @ApiModelProperty(example = "1")
    private Integer level;

    private String provinceCode;

    private String provinceName;

    private String cityCode;

    private String cityName;

    private String districtCode;

    private String districtName;

    @ApiModelProperty(example = "1")
    private Integer state;
}
