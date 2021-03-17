package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author changfeng
 * @version 1.0
 * @description 新增角色权限入参
 * @date 2019/12/14
 */
@Data
public class RoleAuthorityAddParam {

    @ApiModelProperty(value = "角色id", example = "1")
    @NotNull(message = "角色id不能为空")
    private Integer roleId;

    @ApiModelProperty(value = "权限id集合")
//    @Size(min = 1, message = "权限id不能为空")
    private List<Integer> authorityId;

    @ApiModelProperty(value = "权限id无效集合")
//    @Size(min = 1, message = "权限id不能为空")
    private List<Integer> unAuthorityId;

//    @ApiModelProperty(value = "是否有该权限1是0否", example = "1")
//    @NotNull(message = "是否有该权限不能为空")
//    private Integer have;

}
