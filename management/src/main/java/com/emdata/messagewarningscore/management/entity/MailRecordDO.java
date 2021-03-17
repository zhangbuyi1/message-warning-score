package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: sunming
 * @date: 2020/1/8
 * @description:
 */
@Data
@TableName("mail_record")
public class MailRecordDO {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(example = "1")
    private Integer id;

    private String appId;

    private String sender;

    private String receive;

    private String subject;

    private String content;

    /**
     * 邮件发送是否成功，0--成功，1--失败
     */
    @ApiModelProperty(example = "1")
    private Integer status;
}
