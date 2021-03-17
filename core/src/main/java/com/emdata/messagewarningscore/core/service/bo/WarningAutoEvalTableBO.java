package com.emdata.messagewarningscore.core.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 预警自动评估表格返回实体类
 * @date: 2020/12/16
 * @author: sunming
 */
@Data
public class WarningAutoEvalTableBO {

    @ApiModelProperty(value = "日期(yyyy-MM-dd)")
    private String time;

    private List<EvalTableWeatherBO> tableWeatherBOList;
}
