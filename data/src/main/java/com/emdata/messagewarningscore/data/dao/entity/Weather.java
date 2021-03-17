package com.emdata.messagewarningscore.data.dao.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Desc 机场警报  AirportAlert
 * @author lihongjiang
 */
@Data
public class Weather {

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "编号")
    private String code;

    @ApiModelProperty(value = "天气名称")
    private String names;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    public Weather(){

    }

    public Weather(String type, String code, String names, Integer priority, Integer sort){
        this.type = type;
        this.code = code;
        this.names = names;
        this.priority = priority;
        this.sort = sort;
    }

}
