package com.emdata.messagewarningscore.core.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: mdrs自动评估查询实体类
 * @date: 2020/12/16
 * @author: sunming
 */
@Data
public class MdrsAutoEvalQueryParam {

    @ApiModelProperty(value = "开始日期(yyyy-MM-dd)")
    private String startDate;

    @ApiModelProperty(value = "结束日期(yyyy-MM-dd)")
    private String endDate;
}
