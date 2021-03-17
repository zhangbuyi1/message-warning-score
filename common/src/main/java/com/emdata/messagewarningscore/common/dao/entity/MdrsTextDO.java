package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.handler.EvaluationWeatherHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: mdrs解析后的实体类
 * @date: 2020/12/10
 * @author: sunming
 */
@Data
@TableName("mdrs_text")
public class MdrsTextDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机场编码
     */
    private String airportCode;

    /**
     * 地区
     */
    private String area;
    /**
     * 发布时间
     */
    private Date releaseTime;

    @ApiModelProperty(value = "预报概率")
    private String predictProbility;

    @ApiModelProperty(value = "预报开始时间")
    private Date predictStartTime;

    @ApiModelProperty(value = "预报结束时间")
    private Date predictEndTime;

    /**
     * 重要天气
     */
    @TableField(typeHandler = EvaluationWeatherHandler.class)
    private EvaluationWeather seriousWeather;

    private String weather;

    /**
     * 范围
     */
    private String affectedRange;

    /**
     * 备注
     */
    private String remark;
}
