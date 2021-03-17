package com.emdata.messagewarningscore.core.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @date: 2020/12/14
 * @author: sunming
 */
@Data
public class WarningAutoEvalQueryParam {

    @ApiModelProperty(value = "开始日期(yyyy-MM-dd)")
    private String startDate;

    @ApiModelProperty(value = "结束日期(yyyy-MM-dd)")
    private String endDate;
}
