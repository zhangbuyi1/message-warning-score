package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author changfeng
 * @version 1.0
 * @description 新增角色用户入参
 * @date 2019/12/14
 */
@Data
public class RoleUserAddParam {

    @ApiModelProperty(value = "角色id，为空即删除", example = "1")
    private Set<Integer> roleIds;

    @ApiModelProperty(value = "用户id", example = "1")
    @NotNull(message = "用户id不能为空")
    private Integer userId;


}
