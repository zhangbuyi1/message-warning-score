package com.emdata.messagewarningscore.data.parse.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: mdrs预警评估表实体类
 * @date: 2020/12/4
 * @author: sunming
 */
@Data
public class MdrsWarningScoreBO {

    @ApiModelProperty(value = "机场名称")
    private String airportCode;

    @ApiModelProperty(value = "发布时间")
    private String releaseTime;

    @ApiModelProperty(value = "重要天气")
    private String importantWeather;

    @ApiModelProperty(value = "预报概率")
    private String predictProbility;

    @ApiModelProperty(value = "预报开始时间")
    private String predictStartTime;

    @ApiModelProperty(value = "预报结束时间")
    private String predictEndTime;

    @ApiModelProperty(value = "实况开始时间")
    private String metarStartTime;

    @ApiModelProperty(value = "实况结束时间")
    private String metarEndTime;

    @ApiModelProperty(value = "主观得分")
    private Integer subjectScore;

    @ApiModelProperty(value = "命中情况")
    private String hit;

    @ApiModelProperty(value = "开始偏差量")
    private String startDeviation;

    @ApiModelProperty(value = "结束偏差量")
    private String endDeviation;
}
