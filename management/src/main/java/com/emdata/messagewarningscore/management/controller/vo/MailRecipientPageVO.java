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
public class MailRecipientPageVO extends BaseQuery {


    @ApiModelProperty(value = "收件人姓名")
    private String name;

    @ApiModelProperty(value = "邮箱地址")
    private String address;
}
