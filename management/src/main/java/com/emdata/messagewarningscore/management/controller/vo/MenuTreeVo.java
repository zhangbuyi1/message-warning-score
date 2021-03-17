package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 菜单查询返回数据实体类
 * 
 * @author changfeng
 * @date 2019/06/22
 */
@Data
public class MenuTreeVo {

    @ApiModelProperty(value = "id", example = "1")
    private Integer id;

    private String name;

    @ApiModelProperty(value = "level", example = "1")
    private Integer level;

    private String route;

    @ApiModelProperty(value = "parentId", example = "1")
    private Integer parentId;

    @ApiModelProperty(value = "相同上级菜单下菜单顺序", example = "1")
    private Integer menuIndex;
//    @ApiModelProperty(value = "权限列表")
//    private List<Integer> authorityIds;
//
//    @ApiModelProperty(value = "排斥权限列表")
//    private List<Integer> unAuthorityIds;

    private List<MenuTreeVo> children;

    private String icon;

}
