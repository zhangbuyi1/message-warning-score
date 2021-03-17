package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @date: 2021/1/4
 * @author: sunming
 */
@Data
@TableName("cr_picture")
@Builder
public class CrPictureDO {

    @TableId(value = "uuid")
    private String uuid;

    private String airportCode;

    private String radarUuid;

    /**
     * cr原图
     */
    private String picPath;

    /**
     * cr彩图
     */
    private String colorPicPath;

    /**
     * 预测时间，即原始文件最后一张的时间
     */
    private Date picTime;

    /**
     * 是否有天气过程 0是 -1否 -2天气现象未知
     */
    private Integer cavok;

    /**
     * 移动方向
     */
    private Integer route;

    /**
     * 回波类型
     */
    private Integer shape;


}
