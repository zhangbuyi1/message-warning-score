package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * 
 * @description 权限查询参数
 * @date 2019/12/14
 */
@Data
public class AuthorityQueryPageParam extends BaseQuery{

    @ApiModelProperty(value = "主键id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "权限地址")
    private String url;

    @ApiModelProperty(value = "排序的字段，默认按照创建时间created_time")
    protected String orderBy;

}
