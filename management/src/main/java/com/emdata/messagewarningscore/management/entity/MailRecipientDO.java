package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description: 邮箱收件人实体类
 */
@Data
@TableName("mail_recipient")
public class MailRecipientDO {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(example = "1")
    private Integer id;

    /**
     * 收件人姓名
     */
    @ApiModelProperty(value = "收件人姓名")
    private String name;

    /**
     * 收件人邮箱地址
     */
    @ApiModelProperty(value = "收件人邮箱地址")
    private String address;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
