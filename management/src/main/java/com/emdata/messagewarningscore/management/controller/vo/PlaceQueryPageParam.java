package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * 
 * @description 地点分页查询参数
 * @date 2019/12/14
 */
@Data
public class PlaceQueryPageParam extends BaseQuery{

    @ApiModelProperty(value = "地点编码")
    private String placeCode;

    @ApiModelProperty(value = "地点名称")
    private String placeName;

}
