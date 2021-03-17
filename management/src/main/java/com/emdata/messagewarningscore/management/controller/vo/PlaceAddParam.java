package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author changfeng
 * @description 新增地点信息入参
 * @date 2020/2/24
 */
@Data
public class PlaceAddParam {

    @ApiModelProperty(value = "上级地点编码")
    @NotBlank(message = "上级地点编码不能为空")
    private String parentCode;

    @ApiModelProperty(value = "地点编码")
    @NotBlank(message = "地点编码不能为空")
    private String placeCode;

    @ApiModelProperty(value = "地点名称")
    @NotBlank(message = "地点名称不能为空")
    private String placeName;

}
