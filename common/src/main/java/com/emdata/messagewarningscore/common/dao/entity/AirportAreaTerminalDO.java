package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emdata.messagewarningscore.common.common.LevelValueJsonHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 机场、区域、终端区预警共用实体类   机场警报  AirportAlert
 * @date: 2020/12/10
 * @author: sunming
 */
@Data
@TableName("airport_area_terminal")
public class AirportAreaTerminalDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机场编码
     */
    private String airportCode;

    /**
     * 机场名称
     */
    private String airportName;

    /**
     * 预警类型:终端区预警，机场预警，区域预警
     */
    private String warningType;

    @ApiModelProperty(value = "影响区域:终端区，机场，主干航路")
    private String affectedArea;

    @ApiModelProperty(value = "预警性质")
    private String warningNature;

    @ApiModelProperty(value = "预警发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "预报开始时间")
    private Date predictStartTime;

    @ApiModelProperty(value = "预报结束时间")
    private Date predictEndTime;

    /**
     * 伴随天气
     */
    @TableField(typeHandler = LevelValueJsonHandler.class)
    private List<ImportantWeatherDO> importantWeatherDOS;

    @ApiModelProperty(value = "强度")
    private String strength;

    @ApiModelProperty(value = "范围")
    private String affectedRange;

}
