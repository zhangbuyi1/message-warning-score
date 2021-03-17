package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author changfeng
 * @version 1.0
 * @description 新增角色入参
 * @date 2019/12/14
 */
@Data
public class RoleAddParam {

    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @ApiModelProperty(value = "菜单列表")
    private List<Integer> menuIds;

//    @ApiModelProperty(value = "排斥菜单列表")
//    private List<Integer> unMenuIds;

    @ApiModelProperty(value = "权限列表")
    private List<Integer> authorityIds;

    @ApiModelProperty(value = "排斥权限列表")
    private List<Integer> unAuthorityIds;

}
