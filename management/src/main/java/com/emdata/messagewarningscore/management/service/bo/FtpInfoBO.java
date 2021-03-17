package com.emdata.messagewarningscore.management.service.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: sunming
 * @date: 2020/1/2
 * @description: ftp信息实体类
 */
@Data
public class FtpInfoBO {


    @ApiModelProperty(value = "ftpID", example = "1")
    private Integer id;

    @ApiModelProperty(value = "ftp名称")
    private String ftpName;

    @ApiModelProperty("ftp地址")
    private String ftpHost;

    @ApiModelProperty("ftp端口号")
    private String ftpPort;

    @ApiModelProperty("ftp用户名")
    private String ftpUser;

    @ApiModelProperty("ftp密码")
    private String ftpPassword;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
