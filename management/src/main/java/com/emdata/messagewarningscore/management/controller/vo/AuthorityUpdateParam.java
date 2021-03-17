package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author changfeng
 * @version 1.0
 * @description 权限更新入参
 * @date 2019/12/14
 */
@Data
public class AuthorityUpdateParam {

    @ApiModelProperty(value = "主键id", example = "1")
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "权限地址")
    private String url;

//    @ApiModelProperty(value = "权限级别", example = "1")
//    private Integer level;
//
    @ApiModelProperty(value = "父级权限id.为0表示为根节点", example = "1")
    private Integer parentId;
}
