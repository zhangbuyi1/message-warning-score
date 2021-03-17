package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * @description 登录返回数据
 * @date 2019/12/11
 */
@Data
public class SsoLoginVo {

    public SsoLoginVo(String token) {
        this.token = token;
    }

    @ApiModelProperty(value = "用户id", example = "1")
    private Integer userId;

    @ApiModelProperty(value = "登录token")
    private String token;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "真实姓名")
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
}
