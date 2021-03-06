package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.handler.ArrayHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author lihongjiang
 * @Desc 机场警报  AirportAlert
 */
@Data
@TableName(value = "alert_content_resolve",autoResultMap = true)
public class AlertContentResolveDO {

//    @ApiModelProperty(value = "该字段用于测试，部署之前需要删除。")
//    private String textContent;
//    @ApiModelProperty(value = "该字段用于测试，部署之前需要删除。")
//    private String readableTextContent;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "警报的原始文本id")
    private Integer alertOriginalTextId;

    @ApiModelProperty(value = "位置Id 虹桥 浦东 终端 各航点")
    private Integer locationInfoId;

    @ApiModelProperty(value = "机场编码")
    private String airportCode;

    @ApiModelProperty(value = "机场名称")
    private String airportName;

    @ApiModelProperty(value = "警报文件名")
    private String alertFileName;

    @ApiModelProperty(value = "文件标题")
    private String releaseTitle;

    @ApiModelProperty(value = "气象台名称")
    private String weatherStationName;

    @ApiModelProperty(value = "警报发布序号")
    private String releaseSequenceNum;

    @ApiModelProperty(value = "预警发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "预警发布时间时区")
    private String releaseTimeZone;

    @ApiModelProperty(value = "预警类型:1-机场，2-终端区，3-区域")
    private Integer warningType;

    @ApiModelProperty(value = "影响区域:终端区，机场，主干航路")
    private String affectedArea;

    @ApiModelProperty(value = "预警性质")
    private String warningNature;

    @ApiModelProperty(value = "预报开始时间")
    private Date predictStartTime;

    @ApiModelProperty(value = "预报结束时间")
    private Date predictEndTime;

    @ApiModelProperty(value = "范围")
    private String predictRange;

    @ApiModelProperty(value = "强度")
    private String strength;

    @ApiModelProperty(value = "天气类型 1雷暴 2能见度 3低云 4降雪 5风")
    private Integer weatherType;

    @ApiModelProperty(value = "子天气类型 对流云团")
    private String subWeatherType;

    @ApiModelProperty(value = "天气单位")
    private String weatherUnit;

    @ApiModelProperty(value = "天气名称")
    private String weatherName;

//    @ApiModelProperty(value = "天气最小值")
//    private String weatherMinNum;
//    @ApiModelProperty(value = "天气最大值")
//    private String weatherMaxNum;
//    @ApiModelProperty(value = "附加天气单位")
//    private String additionalUnit;
//    @ApiModelProperty(value = "附加天气名称")
//    private String additionalName;
//    @ApiModelProperty(value = "附加天气最小值")
//    private String additionalMinNum;
//    @ApiModelProperty(value = "附加天气最大值")
//    private String additionalMaxNum;
//    @ApiModelProperty(value = "伴随天气")
//    private String accompanyWeather;

    @ApiModelProperty(value = "主要天气JSON lihongjiang")
    @TableField(typeHandler = ArrayHandler.class)
    private List<ForcastWeather> mainWeatherJson;

    @ApiModelProperty(value = "附加天气JSON")
    @TableField(typeHandler = ArrayHandler.class)
    private List<ForcastWeather> addiWeatherJson;

    @ApiModelProperty(value = "状态 0待评分 1评分成功 2评分失败")
    private Integer status;

    @ApiModelProperty(value = "预报概率,mdrs用到的")
    private String predictProbility;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "更新时间")
    private Date updatedTime;

}
