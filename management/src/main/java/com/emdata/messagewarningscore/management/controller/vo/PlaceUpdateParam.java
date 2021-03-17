package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author changfeng
 * @description 新增地点信息入参
 * @date 2020/2/24
 */
@Data
public class PlaceUpdateParam {

    @ApiModelProperty(value = "修改记录id", example = "1")
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "上级地点编码")
    private String parentCode;

    @ApiModelProperty(value = "地点编码")
    private String placeCode;

    @ApiModelProperty(value = "地点名称")
    private String placeName;

}
