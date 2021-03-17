package com.emdata.messagewarningscore.core.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 重要天气自动记录表格返回实体类
 * @date: 2020/12/15
 * @author: sunming
 */
@Data
public class WeatherAutoRecordTableBO {

    @ApiModelProperty(value = "日期（yyyy-MM-dd）")
    private String time;

    @ApiModelProperty(value = "天气集合")
    private List<TableWeatherBO> weathers;
}
