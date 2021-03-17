package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Desc 警报的原始文本
 * @author lihongjiang
 */
@Data
@TableName(value = "alert_original_text")
public class AlertOriginalTextDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "警报类型 1-机场 2-终端 3-区域 4-MDSI")
    private Integer alertType;

    @ApiModelProperty(value = "文件名")
    private String alertFileName;

    @ApiModelProperty(value = "机场编码")
    private String airportCode;

    @ApiModelProperty(value = "机场名称")
    private String airportName;

    @ApiModelProperty(value = "发布标题")
    private String releaseTitle;

    @ApiModelProperty(value = "气象台名称")
    private String weatherStationName;

    @ApiModelProperty(value = "发布序号")
    private String releaseSequenceNum;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "发布时间时区")
    private String releaseTimeZone;

    @ApiModelProperty(value = "正文内容")
    private String textContent;

    @ApiModelProperty(value = "正文解析状态 0待解析 1解析成功 2解析失败")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "更新时间")
    private Date updatedTime;

}
