package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: sunming
 * @date: 2020/2/25
 * @description: 邮箱收件人实体类
 */
@Data
public class MailRecipientParam {

    @ApiModelProperty(value = "修改记录id", example = "1")
    @NotNull(message = "主键id不能为空")
    private Integer id;

    @ApiModelProperty(value = "收件人姓名")

    private String name;

    @ApiModelProperty(value = "收件人邮箱地址")
    private String address;
}
