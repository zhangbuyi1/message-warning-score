package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author changfeng
 * @version 1.0
 * @description 新增菜单入参
 * @date 2019/12/14
 */
@Data
public class MenuAddParam {

    @ApiModelProperty(value = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @ApiModelProperty(value = "菜单路由")
    @NotBlank(message = "菜单路由不能为空")
    private String route;

//    @ApiModelProperty(value = "菜单级别", example = "1")
//    @NotNull(message = "菜单级别不能为空")
//    private Integer level;

    @ApiModelProperty(value = "父级菜单id.为0表示为根节点", example = "1")
    @NotNull(message = "父级菜单id不能为0")
    private Integer parentId;

    @ApiModelProperty(value = "相同上级菜单下菜单顺序", example = "1")
    @NotNull(message = "菜单顺序不能为空")
    private Integer menuIndex;

//    @ApiModelProperty(value = "权限列表")
//    private List<Integer> authorityIds;
//
//    @ApiModelProperty(value = "排斥权限列表")
//    private List<Integer> unAuthorityIds;

    @ApiModelProperty(value = "图标")
    private String icon;
}
