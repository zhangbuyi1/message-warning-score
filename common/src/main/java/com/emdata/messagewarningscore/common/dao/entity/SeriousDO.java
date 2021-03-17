package com.emdata.messagewarningscore.common.dao.entity;/**
 * Created by zhangshaohu on 2020/12/31.
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.handler.EvaluationWeatherHandler;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: zhangshaohu
 * @date: 2020/12/31
 * @description: 重要天气记录
 */
@Data
@TableName(value = "serious", autoResultMap = true)
public class SeriousDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 机场code
     */
    private String airportCode;
    /**
     * 地点id
     */
    private Integer locationId;
    /**
     * 重要天气是进行中
     */
    private Integer status = 0;
    /**
     * 最后一次重要天气发生时间
     */
    private Date lastTime;
    @TableField(typeHandler = EvaluationWeatherHandler.class)
    private EvaluationWeather seriousWeather;

    private String weather;

    private Integer radarEcho;

    private Integer isScore;

    public SeriousDO() {

    }

    public SeriousDO(Date startTime, Date endTime, String airportCode, EvaluationWeather seriousWeather, String weather, Integer locationId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.airportCode = airportCode;
        this.seriousWeather = seriousWeather;
        this.weather = weather;
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "SeriousDO{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", airportCode='" + airportCode + '\'' +
                ", locationId=" + locationId +
                ", status=" + status +
                ", lastTime=" + lastTime +
                ", seriousWeather=" + seriousWeather +
                ", weather='" + weather + '\'' +
                ", radarEcho=" + radarEcho +
                ", isScore=" + isScore +
                '}';
    }
}