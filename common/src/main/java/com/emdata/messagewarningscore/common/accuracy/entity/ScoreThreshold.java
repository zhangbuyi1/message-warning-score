package com.emdata.messagewarningscore.common.accuracy.entity;

import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.metar.Metar;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/12/30
 * @description:
 */
@Data
public class ScoreThreshold {
    /**
     * 地点
     */
    private String airport;
    /**
     * 当前对象创建时间
     */
    private Date createTime;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 起评类型
     */
    private EvaluationWeather evaluationWeather;

    /**
     * 天气
     */
    private String weather;

    /**
     * 时间段类实况信息
     */
    private List<MetarSourceDO> metars = new ArrayList<>();


    public ScoreThreshold() {

    }

    /**
     * @param airport
     * @param createTime
     * @param startTime
     * @param endTime
     * @param evaluationWeather
     * @param metarSourceDO
     */
    public ScoreThreshold(String airport, Date createTime, Date startTime, Date endTime, EvaluationWeather evaluationWeather, MetarSourceDO metarSourceDO) {
        this.airport = airport;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.evaluationWeather = evaluationWeather;
        this.metars.add(metarSourceDO);
    }

    public List<Metar> getMetar() {
        return this.metars.stream().map(s -> {
            return new Metar(s.getMetarSource(), s.getMetarTime());
        }).collect(Collectors.toList());
    }
}