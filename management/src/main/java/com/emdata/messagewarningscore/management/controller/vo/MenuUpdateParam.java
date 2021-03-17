package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author changfeng
 * @version 1.0
 * @description 菜单更新入参
 * @date 2019/12/14
 */
@Data
public class MenuUpdateParam {

    @ApiModelProperty(value = "主键id", example = "1")
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "菜单地址")
    private String route;

//    @ApiModelProperty(value = "菜单级别", example = "1")
//    private Integer level;

    @ApiModelProperty(value = "父级菜单id.为空表示为根节点", example = "1")
    private Integer parentId;

    @ApiModelProperty(value = "相同上级菜单下菜单顺序", example = "1")
    private Integer menuIndex;

    @ApiModelProperty(value = "图标")
    private String icon;

//    @ApiModelProperty(value = "权限列表")
//    private List<Integer> authorityIds;
//
//    @ApiModelProperty(value = "排斥权限列表")
//    private List<Integer> unAuthorityIds;
}
