package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Sort;

/**
 * @author changfeng
 * @description 分页查询基础类
 * @date 2019/12/14
 */
@Data
public class BaseQuery {
    @ApiModelProperty(value = "分页参数：当前第N页", required = true, example = "1")
    protected Long current = 1L;

    @ApiModelProperty(value = "分页参数：每页N条数据", required = true, example = "10")
    private Long size = 10L;

    @ApiModelProperty(value = "排序的字段")
    protected String orderBy;

    @ApiModelProperty(value = "顺序，默认：DESC，可选值：DESC， ASC")
    protected Sort.Direction sort = Sort.Direction.DESC;

    @ApiModelProperty(value = "开始时间（yyyy-MM-dd HH:mm:ss）")
    private String startTime;

    @ApiModelProperty(value = "结束时间（yyyy-MM-dd HH:mm:ss）")
    private String endTime;

}
