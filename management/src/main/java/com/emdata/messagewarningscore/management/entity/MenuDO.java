package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * @description
 * @date 2020/2/21
 */
@Data
@TableName("menu")
public class MenuDO extends PublicDO {

    @TableId(value = "id", type= IdType.AUTO)
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

    @ApiModelProperty(value = "相同上级菜单下菜单顺序", example = "1")
    private Integer menuIndex;

    @ApiModelProperty(value = "记录状态：1正常，9已删除", example = "1")
    private Integer state;

    @ApiModelProperty(value = "图标")
    private String icon;
}
