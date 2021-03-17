package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @date: 2020/12/14
 * @author: sunming
 */
@Data
@TableName(value = "weather_auto_record")
public class WeatherAutoRecordDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 机场编码
     */
    private String airportCode;

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

    private Integer vis;

    private Integer rvr;

    private Integer cBase;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
