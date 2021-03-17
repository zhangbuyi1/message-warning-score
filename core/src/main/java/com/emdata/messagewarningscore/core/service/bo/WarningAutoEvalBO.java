package com.emdata.messagewarningscore.core.service.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: 自动评估返回实体类
 * @date: 2020/12/16
 * @author: sunming
 */
@Data
public class WarningAutoEvalBO extends VisCbaseRvrBO {
    @ApiModelProperty(value = "机场名称")
    private String airportCode;

    @ApiModelProperty(value = "机场名称")
    private String airportName;

    @ApiModelProperty(value = "预警类型:终端区预警，机场预警，区域预警")
    private String warningType;

    @ApiModelProperty(value = "影响区域:终端区，机场，主干航路")
    private String affectedArea;

    @ApiModelProperty(value = "预警性质")
    private String warningNature;

    @ApiModelProperty(value = "预警发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;

    @ApiModelProperty(value = "范围")
    private String affectedRange;

    @ApiModelProperty(value = "伴随天气")
    private String withWeather;
    @ApiModelProperty(value = "评分的重要天气")
    private String scoreWeather;

    @ApiModelProperty(value = "映射到评分天气上的天气")
    private String weather;

    @ApiModelProperty(value = "MDRS启动情况:无，黄色，红色，橙色")
    private String mdrsStart;

    @ApiModelProperty(value = "评估")
    private String evaluate;

    @ApiModelProperty(value = "实况地点+日期")
    private String metarPlaceDate;

    @ApiModelProperty(value = "实况日期yyyyMMdd")
    private String metarDate;

    @ApiModelProperty(value = "实况时长")
    private Integer metarDuration;

    @ApiModelProperty(value = "提前量")
    private Double lead;

    @ApiModelProperty(value = "偏差量")
    private Double deviation;

    @ApiModelProperty(value = "重合率")
    private Double coinRate;

    @ApiModelProperty(value = "预报开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preStartTime;

    @ApiModelProperty(value = "预报结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preEndTime;

    @ApiModelProperty(value = "实况开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date metarStartTime;

    @ApiModelProperty(value = "实况结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date metarEndTime;
}
