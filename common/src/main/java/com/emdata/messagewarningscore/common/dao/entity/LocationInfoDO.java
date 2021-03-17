package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2021/1/5
 * @description: 地点信息类
 */
@Data
@TableName(value = "location_info", autoResultMap = true)
public class LocationInfoDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer locationId;

    /**
     * 航线的宽度
     */
    private Double width;

    /**
     * 点，线，面
     */
    private String rangeType;
    /**
     * 名称
     */
    private String name;

    /**
     * 雷达uuid
     */
    private String radarInfoUuid;

}