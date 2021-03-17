package com.emdata.messagewarningscore.common.accuracy.entity;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.WarningNature;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description: 从数据库中查询到天气现象
 */
@Data
public class ForcastData {

    private Integer alertContentResolveId;
    /**
     * 机场编码
     */
    private String airportCode;
    /**
     * 影响地域
     */
    private String region;
    /**
     * 范围
     */
    private String range;
    /**
     * 发布时间
     */
    private Date sendTime;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 地点id
     */
    private Integer locationInfoId;
    /**
     * 地点id
     */
    private Integer locationId;
    /**
     * 性质
     */
    private WarningNature nature;
    /**
     * 当前预警类型(机场警报,终端区,MDRS)
     */
    private WarningTypeEnum warningTypeEnum;
    /**
     * 机场预报天气类型
     */
    private EvaluationWeather evaluationWeather;
    /**
     * 主要天气
     */
    private List<ForcastWeather> mainWeather;
    /**
     * 伴随天气
     */
    private List<ForcastWeather> accompanyWeathers;

    public ForcastData(String airportCode, String region, Date sendTime, Date startTime, Date endTime, Integer locationInfoId, WarningNature nature, WarningTypeEnum warningTypeEnum, EvaluationWeather evaluationWeather,
                       List<ForcastWeather> mainWeather, List<ForcastWeather> accompanyWeathers) {
        this.airportCode = airportCode;
        this.region = region;
        this.sendTime = sendTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationInfoId = locationInfoId;
        this.nature = nature;
        this.warningTypeEnum = warningTypeEnum;
        this.evaluationWeather = evaluationWeather;
        this.mainWeather = mainWeather;
        this.accompanyWeathers = accompanyWeathers;
    }

    public ForcastData(AlertContentResolveDO alertContentResolveDO) {
        this.accompanyWeathers = alertContentResolveDO.getAddiWeatherJson();
        this.mainWeather = alertContentResolveDO.getMainWeatherJson();
        String warningNature = alertContentResolveDO.getWarningNature();
        this.nature = WarningNature.valueOf(warningNature);
        this.startTime = alertContentResolveDO.getPredictStartTime();
        this.endTime = alertContentResolveDO.getPredictEndTime();
        this.evaluationWeather = EvaluationWeather.build(alertContentResolveDO.getWeatherType());
        this.warningTypeEnum = WarningTypeEnum.build(alertContentResolveDO.getWarningType().toString());
        this.locationInfoId = alertContentResolveDO.getLocationInfoId();
        this.sendTime = alertContentResolveDO.getReleaseTime();
        this.region = alertContentResolveDO.getAffectedArea();
        this.range = alertContentResolveDO.getPredictRange();
    }

    public ForcastData(AlertContentResolveDO alertContentResolveDO, LocationInfoDO locationInfoDO, LocationDO locationDO) {
        this.accompanyWeathers = alertContentResolveDO.getAddiWeatherJson();
        this.mainWeather = alertContentResolveDO.getMainWeatherJson();
        String warningNature = alertContentResolveDO.getWarningNature();
        this.nature = WarningNature.build(warningNature);
        this.startTime = alertContentResolveDO.getPredictStartTime();
        this.endTime = alertContentResolveDO.getPredictEndTime();
        this.evaluationWeather = EvaluationWeather.build(alertContentResolveDO.getWeatherType());
        this.warningTypeEnum = WarningTypeEnum.build(alertContentResolveDO.getWarningType());
        this.locationInfoId = alertContentResolveDO.getLocationInfoId();
        this.sendTime = alertContentResolveDO.getReleaseTime();
        this.region = alertContentResolveDO.getAffectedArea();
        this.range = alertContentResolveDO.getPredictRange();
        this.locationId = locationInfoDO.getLocationId();
        this.airportCode = locationDO.getAirportCode();

    }
}