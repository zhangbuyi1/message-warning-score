package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author changfeng
 * @version 1.0
 * @description 角色更新入参
 * @date 2019/12/14
 */
@Data
public class RoleUpdateParam {

    @ApiModelProperty(value = "主键id", example = "1")
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @ApiModelProperty(value = "菜单列表")
    private List<Integer> menuIds;

//    @ApiModelProperty(value = "排斥权限列表")
//    private List<Integer> unMenuIds;

    @ApiModelProperty(value = "权限列表")
    private List<Integer> authorityIds;

    @ApiModelProperty(value = "排斥权限列表")
    private List<Integer> unAuthorityIds;
}
