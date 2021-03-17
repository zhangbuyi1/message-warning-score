package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * 
 * @description 菜单查询参数
 * @date 2019/12/14
 */
@Data
public class MenuQueryPageParam extends BaseQuery{

    @ApiModelProperty(value = "主键id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "菜单地址")
    private String route;

    @ApiModelProperty(value = "菜单级别", example = "1")
    private Integer level;

    @ApiModelProperty(value = "父级菜单id.为空表示为根节点", example = "1")
    private Integer parentId;

    @ApiModelProperty(value = "排序的字段，默认按照创建时间created_time")
    protected String orderBy;

}
