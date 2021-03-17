package com.emdata.messagewarningscore.data.dao.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Desc 机场警报  AirportAlert
 * @author lihongjiang
 */
@Data
public class WeatherBO {

    @ApiModelProperty(value = "描述")
    private String describe;

    @ApiModelProperty(value = "编号")
    private String code;

    @ApiModelProperty(value = "最小值")
    private String min;

    @ApiModelProperty(value = "最大值")
    private String max;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

}
