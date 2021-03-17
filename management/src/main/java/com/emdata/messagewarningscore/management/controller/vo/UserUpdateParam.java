package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author changfeng
 * @version 1.0
 * @description 新增用户入参
 * @date 2019/12/14
 */
@Data
public class UserUpdateParam {

    @ApiModelProperty(value = "主键id", example = "1", required = true)
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "真实姓名")
    @Length(max=10)
    private String realName;

    @ApiModelProperty(value = "性别，1男2女", example = "1")
    private Integer sex;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "电话号码")
    private String telephone;

    @ApiModelProperty(value = "邮箱号")
    private String email;

    @ApiModelProperty(value = "绑定角色集合")
    private Set<Integer> roleIds;
}
