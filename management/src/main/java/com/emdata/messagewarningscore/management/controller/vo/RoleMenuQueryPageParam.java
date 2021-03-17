package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * 
 * @description 角色菜单关系查询参数
 * @date 2019/12/14
 */
@Data
public class RoleMenuQueryPageParam extends BaseQuery{

//    @ApiModelProperty(value = "主键id", example = "1")
//    private Integer id;

    @ApiModelProperty(value = "角色id", example = "1")
    private Integer roleId;

//    @ApiModelProperty(value = "菜单id", example = "1")
//    private Integer menuId;

//    @ApiModelProperty(value = "是否有该权限1是0否", example = "1")
//    private Integer have;

}
