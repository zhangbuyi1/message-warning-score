package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: mdrs预警评估表实体类
 * @date: 2020/12/4
 * @author: sunming
 */
@Data
@TableName("mdrs_auto_eval")
public class MdrsAutoEvalDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "机场名称")
    private String airportCode;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "重要天气")
    private String importantWeather;

    @ApiModelProperty(value = "伴随天气")
    private String withWeather;

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
}
