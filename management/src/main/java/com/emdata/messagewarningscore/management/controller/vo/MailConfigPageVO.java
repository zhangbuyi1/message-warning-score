package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: sunming
 * @date: 2020/2/25
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MailConfigPageVO extends BaseQuery {


    @ApiModelProperty(value = "服务ID")
    private String appId;

    @ApiModelProperty(value = "邮箱用户名")
    private String username;
}
