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
 * @date: 2020/12/16
 * @author: sunming
 */
@Data
@TableName("mdrs_auto_eval_infos")
public class MdrsAutoEvalInfosDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer evalId;
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

    private String preStrength;

    private Integer preVis;

    private Integer preCbase;

    private Integer preRvr;

    private String metarStrength;

    private Integer metarVis;

    private Integer metarCbase;

    private Integer metarRvr;
}
