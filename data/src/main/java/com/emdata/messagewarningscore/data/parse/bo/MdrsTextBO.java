package com.emdata.messagewarningscore.data.parse.bo;

import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: mdrs解析后的实体类
 * @date: 2020/12/10
 * @author: sunming
 */
@Data
public class MdrsTextBO {

    private Integer id;

    private String title;

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
