package com.emdata.messagewarningscore.core.service.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @date: 2020/12/16
 * @author: sunming
 */
@Data
public class MdrsAutoEvalAllBO extends VisCbaseRvrBO{
    @ApiModelProperty(value = "机场名称")
    private String airportCode;

    @ApiModelProperty(value = "机场名称")
    private String airportName;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;

    @ApiModelProperty(value = "重要天气")
    private String importantWeather;

    @ApiModelProperty(value = "伴随天气")
    private String withWeather;

    @ApiModelProperty(value = "映射到重要天气上的天气")
    private String weather;

    @ApiModelProperty(value = "预报概率")
    private String predictProbility;

    @ApiModelProperty(value = "命中情况")
    private String hit;

    @ApiModelProperty(value = "开始偏差量")
    private Double startDeviation;

    @ApiModelProperty(value = "结束偏差量")
    private Double endDeviation;

    @ApiModelProperty(value = "命中得分")
    private Double hitScore;

    @ApiModelProperty(value = "概率权重")
    private Double proWeight;

    @ApiModelProperty(value = "主观得分")
    private Double subjectScore;
    @ApiModelProperty(value = "客观得分")
    private Double objectiveScore;

    @ApiModelProperty(value = "综合得分")
    private Double allScore;

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
