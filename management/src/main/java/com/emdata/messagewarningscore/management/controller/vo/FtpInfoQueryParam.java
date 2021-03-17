package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: sunming
 * @date: 2020/2/26
 * @description:
 */
@Data
public class FtpInfoQueryParam extends BaseQuery{

    @ApiModelProperty(value = "ftp的ip地址")
    private String ftpHost;

    @ApiModelProperty(value = "ftp名称")
    private String ftpName;
}
