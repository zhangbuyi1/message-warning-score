package com.emdata.messagewarningscore.management.controller.vo;


import com.emdata.messagewarningscore.management.entity.RoleMenuDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * @description 显示菜单是否叶子
 * @date 2020/3/4
 */
@Data
public class RoleMenuVo extends RoleMenuDO {

    @ApiModelProperty(value = "该菜单是否是父级，1是 0否", example = "1")
    private Integer father;
}
