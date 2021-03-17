package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author changfeng
 * @description 日志表
 * @date 2020/2/26
 */
@Data
@TableName("log")
public class LogDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "操作用户")
    private String userName;

    /**
     * 操作类型
     * LOGIN(0, "登录"),
    *  LOGOUT(1, "登出"),
    *  SELECT(10, "查询"),
    *  INSERT(11, "新增"),
    *  UPDATE(12, "更新"),
    *  DELETE(13, "删除"),
    *  DOWLOAD(20, "下载");
     */
    @ApiModelProperty(value = "操作类型,0:登录, 1:登出, 10:查询, 11:新增, 12:更新, 13:删除, 20:下载", example = "1")
    private Integer type;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型文字")
    private String typeStr;

    /**
     * 地区名
     */
    @ApiModelProperty(value = "操作内容")
    private String contents;

    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateTime;

    /**
     * 一级菜单id
     */
    @ApiModelProperty(value = "一级菜单id", example = "1")
    private Integer menuIdLevelOne;

    /**
     * 二级菜单id
     */
    @ApiModelProperty(value = "二级菜单id", example = "1")
    private Integer menuIdLevelTwo;

    /**
     * 三级菜单id
     */
    @ApiModelProperty(value = "三级菜单id", example = "1")
    private Integer menuIdLevelThree;

    /**
     * 一级菜单name
     */
    @ApiModelProperty(value = "一级菜单name")
    private String menuNameLevelOne;

    /**
     * 二级菜单name
     */
    @ApiModelProperty(value = "二级菜单name")
    private String menuNameLevelTwo;

    /**
     * 三级菜单name
     */
    @ApiModelProperty(value = "三级菜单name")
    private String menuNameLevelThree;

    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    private String ip;


    /**
     * 是否成功 1是 0否
     */
    @ApiModelProperty(value = "是否成功 1是 0否", example = "1")
    private Integer success;

    /**
     * 错误原因
     */
    @ApiModelProperty(value = "错误原因")
    private String errorReason;

    @ApiModelProperty(value = "记录状态", example = "1")
    private Integer state;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
