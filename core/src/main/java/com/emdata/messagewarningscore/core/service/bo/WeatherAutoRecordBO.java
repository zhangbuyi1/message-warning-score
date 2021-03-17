package com.emdata.messagewarningscore.core.service.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: 重要天气自动记录返回bo
 * @date: 2020/12/14
 * @author: sunming
 */
@Data
public class WeatherAutoRecordBO {
    /**
     * 机场编码
     */
    @ApiModelProperty(value = "机场代码")
    private String airportCode;

    @ApiModelProperty(value = "机场名称")
    private String airportName;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "重要天气代码")
    private String importantWeather;

    @ApiModelProperty(value = "天气现象")
    private String weather;

    @ApiModelProperty(value = "伴随天气")
    private String withWeather;

    @ApiModelProperty(value = "强度")
    private String strength;

    @ApiModelProperty(value = "范围")
    private String affectedRange;

    @ApiModelProperty(value = "能见度")
    private Integer vis;

    @ApiModelProperty(value = "rvr")
    private Integer rvr;

    @ApiModelProperty(value = "云底高")
    private Integer cBase;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
