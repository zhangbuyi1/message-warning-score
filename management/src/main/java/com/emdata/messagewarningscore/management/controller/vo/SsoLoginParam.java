package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author changfeng
 * @description 登录请求参数
 * @date 2019/12/11
 */
@Data
public class SsoLoginParam {

    @ApiModelProperty(value = "登录账户", required = true)
    @NotBlank(message = "登录账户不能为空")
    private String username;

    @ApiModelProperty(value = "登录密码", required = true)
    @NotBlank(message = "登录密码不能为空")
    private String password;
}
