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
 * @date: 2020/1/2
 * @description: ftp信息实体类
 */
@Data
@TableName("ftp_info")
public class FtpInfoDO {

    /**
     * id自增
     */
    @ApiModelProperty(example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String ftpName;

    private String ftpHost;

    private String ftpPort;

    private String ftpUser;

    private String ftpPassword;

    private String description;

    /**
     * 记录状态：1-正常；9-已删除
     */
    @ApiModelProperty(example = "1")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
