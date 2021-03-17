package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author changfeng
 * 
 * @description 用户分页查询入参
 * @date 2019/12/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserQueryPageParam extends BaseQuery{

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "排序的字段，默认按照创建时间created_time")
    protected String orderBy;

}
