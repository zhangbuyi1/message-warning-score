package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: sunming
 * @date: 2020/1/2
 * @description:
 */
@Data
public class FtpInfoParam {

    @ApiModelProperty(value = "ftp详细信息id，修改时为必传项", example = "1")
    private Integer id;

    @ApiModelProperty(value = "ftp名称")
    private String ftpName;

    @ApiModelProperty(value = "ftp的ip地址")
    private String ftpHost;

    @ApiModelProperty(value = "ftp的端口号")
    private String ftpPort;

    @ApiModelProperty(value = "ftp的用户名")
    private String ftpUser;

    @ApiModelProperty(value = "ftp的密码")
    private String ftpPassword;

    @ApiModelProperty(value = "描述")
    private String description;

}
