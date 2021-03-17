package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @author changfeng
 * @version 1.0
 * @description 新增用户入参
 * @date 2019/12/14
 */
@Data
public class UserAddParam {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Length(max = 20)
    private String name;

    @ApiModelProperty(value = "用户密码", required = true)
    @NotBlank(message = "用户密码不能为空")
    private String password;

    @ApiModelProperty(value = "真实姓名")
    @Length(max = 10)
    private String realName;

    @ApiModelProperty(value = "性别，1男2女", example = "1")
    private Integer sex;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "电话号码")
    private String telephone;

    @ApiModelProperty(value = "邮箱号")
    private String email;

    @ApiModelProperty(value = "机场代码")
    private String airportCode;

    @ApiModelProperty(value = "绑定角色集合")
    private Set<Integer> roleIds;
}
