package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author changfeng
 * @version 1.0
 * @description 新增用户入参
 * @date 2019/12/14
 */
@Data
public class AuthorityAddParam {

    @ApiModelProperty(value = "权限名称", example = "1")
    @NotBlank(message = "权限名称不能为空")
    private String name;

    @ApiModelProperty(value = "权限地址", example = "1")
    @NotBlank(message = "权限地址不能为空")
    private String url;

//    @ApiModelProperty(value = "权限级别", example = "1")
//    @NotNull(message = "权限级别不能为空")
//    private Integer level;
//
    @ApiModelProperty(value = "父级权限id.为0表示为根节点", example = "1")
    @NotNull(message = "父级权限id不能为空，顶级权限父id为0")
    private Integer parentId;

}
