package com.emdata.messagewarningscore.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description:
 * @date: 2020/12/15
 * @author: sunming
 */
@Data
@TableName(value = "metar_source")
public class MetarSourceDO {

    @TableId
    private String uuid;
    /**
     * 机场code
     */
    private String airportCode;
    /**
     * metar数据
     */
    private String metarSource;
    /**
     * metar报文时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date metarTime;


}
