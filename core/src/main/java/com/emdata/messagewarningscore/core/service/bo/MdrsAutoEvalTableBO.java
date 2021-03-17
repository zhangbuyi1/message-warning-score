package com.emdata.messagewarningscore.core.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @date: 2020/12/16
 * @author: sunming
 */
@Data
public class MdrsAutoEvalTableBO {
    @ApiModelProperty(value = "日期(yyyy-MM-dd)")
    private String time;
}
