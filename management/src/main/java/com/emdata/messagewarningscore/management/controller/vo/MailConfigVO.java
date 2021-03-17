package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: sunming
 * @date: 2020/2/25
 * @description: 邮箱配置实体类
 */
@Data
public class MailConfigVO {

    @ApiModelProperty(value = "修改记录id", example = "1")
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "服务名")
    private String appId;

    @ApiModelProperty(value = "邮箱ip")
    private String host;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "端口号", example = "1")
    private Integer port;

}
