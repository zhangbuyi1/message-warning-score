package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@TableName("mail_config")
public class MailConfig {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(example = "1")
    private Integer id;

    private String appId;

    private String host;

    private String username;

    private String password;

    @ApiModelProperty(example = "1")
    private Integer port;


}
