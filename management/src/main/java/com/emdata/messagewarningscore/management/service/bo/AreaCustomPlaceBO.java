package com.emdata.messagewarningscore.management.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: sunming
 * @date: 2020/1/10
 * @description:
 */
@Data
public class AreaCustomPlaceBO {

    @ApiModelProperty(example = "1")
    private Integer id;

    @ApiModelProperty(value = "地点编码")
    private String placeCode;

    @ApiModelProperty(value = "地点名称")
    private String placeName;

    @ApiModelProperty(value = "上级地点code")
    private String parentCode;

    @ApiModelProperty(value = "地点级别 1：一级，2：二级，一级直属于area区县", example = "1")
    private Integer level;

    @ApiModelProperty(value = "省编码")
    private String provinceCode;

    @ApiModelProperty(value = "省名称")
    private String provinceName;

    @ApiModelProperty(value = "市编码")
    private String cityCode;

    @ApiModelProperty(value = "市名称")
    private String cityName;

    @ApiModelProperty(value = "区县编码")
    private String districtCode;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "摄像头位置地点编码数组")
    private String[] codes;
}
