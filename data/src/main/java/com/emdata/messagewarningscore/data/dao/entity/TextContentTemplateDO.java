package com.emdata.messagewarningscore.data.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Desc 机场警报
 * @author lihongjiang
 */
@Data
@TableName("text_content_template")
public class TextContentTemplateDO implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "模板类型 1机场 2区域 3终端 4...")
    private String templateType;

    @ApiModelProperty(value = "天气类型 1雷暴 2能见度 3低云 4降雪 5风")
    private String weatherType;

    @ApiModelProperty(value = "模板内容")
    private String templateContent;

    @ApiModelProperty(value = "模板状态 0无效 1有效")
    private String templateStatus;

    @ApiModelProperty(value = "备用字段")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
