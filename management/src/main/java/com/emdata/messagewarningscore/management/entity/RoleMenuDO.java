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
@TableName("role_menu")
public class RoleMenuDO extends PublicDO {

    @TableId(value = "id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "角色id", example = "1")
    private Integer roleId;

    @ApiModelProperty(value = "菜单id", example = "1")
    private Integer menuId;

//    @ApiModelProperty(value = "是否有该菜单1是0否", example = "1")
//    private Integer have;

    @ApiModelProperty(value = "记录状态：1正常，9已删除", example = "1")
    private Integer state;
}
