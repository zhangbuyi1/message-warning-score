package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/8
 * @description:
 */
@Data
public class MailMsgVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "应用Id", required = true)
    private String appId;

    @ApiModelProperty(value = "接收人邮箱", required = true)
    private List<String> tos;

    @ApiModelProperty(value = "主题", required = true)
    private String subject;

    @ApiModelProperty(value = "内容", required = true)
    private String content;
}
