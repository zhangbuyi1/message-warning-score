package com.emdata.messagewarningscore.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emdata.messagewarningscore.common.common.LevelValueJsonHandler;
import com.emdata.messagewarningscore.common.dao.entity.ImportantWeatherDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author lihongjiang
 * @Desc 机场警报  AirportAlert
 */
@Data
@TableName("airport_alert_content")
public class AirportAlertContentDO {

    @ApiModelProperty(value = "该字段用于测试，部署之前需要删除。")
    private String textContent;
    @ApiModelProperty(value = "该字段用于测试，部署之前需要删除。")
    private String text;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "机场警报id")
    private Integer airportAlertId;

    @ApiModelProperty(value = "机场编码")
    private String airportCode;

    @ApiModelProperty(value = "机场名称")
    private String airportName;

    @ApiModelProperty(value = "机场警报文件名称")
    private String airportAlertFileName;

    @ApiModelProperty(value = "气象台名称")
    private String weatherStationName;

    @ApiModelProperty(value = "警报发布序号")
    private String releaseSequenceNum;

    @ApiModelProperty(value = "预警发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "预警发布时间时区")
    private String releaseTimeZone;

    @ApiModelProperty(value = "预警类型:终端区预警，机场预警，区域预警")
    private String warningType;

    @ApiModelProperty(value = "影响区域:终端区，机场，主干航路")
    private String affectedArea;

    @ApiModelProperty(value = "预警性质")
    private String warningNature;

    @ApiModelProperty(value = "预报开始时间")
    private Date predictStartTime;

    @ApiModelProperty(value = "预报结束时间")
    private Date predictEndTime;

    @ApiModelProperty(value = "范围")
    private String range;

    @ApiModelProperty(value = "强度")
    private String strength;

    @ApiModelProperty(value = "天气类型 雷暴、低云、风、低能低见、降雪/冻降水")
    private String weatherType;

    @ApiModelProperty(value = "子天气类型 对流云团")
    private String subWeatherType;

    @ApiModelProperty(value = "天气单位")
    private String weatherUnit;

    @ApiModelProperty(value = "天气名称")
    private String weatherName;

    @ApiModelProperty(value = "天气最小值")
    private String weatherMinNum;

    @ApiModelProperty(value = "天气最大值")
    private String weatherMaxNum;

    @ApiModelProperty(value = "附加天气名称")
    private String additionalName;

    @ApiModelProperty(value = "附加天气最小值")
    private String additionalMinNum;

    @ApiModelProperty(value = "附加天气最大值")
    private String additionalMaxNum;

    @ApiModelProperty(value = "伴随天气")
    private String accompanyWeather;

    @ApiModelProperty(value = "伴随天气2")
    @TableField(typeHandler = LevelValueJsonHandler.class)
    private List<ImportantWeatherDO> importantWeatherDOS;

}
