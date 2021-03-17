package com.emdata.messagewarningscore.common.service.bo;/**
 * Created by zhangshaohu on 2021/1/14.
 */

import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.ThresholdEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author: zhangshaohu
 * @date: 2021/1/14
 * @description:
 */
@Data
public class SeriousBO extends SeriousDO {
    /**
     * 是否满足阈值
     */
    private ThresholdEnum threshold;
    /**
     * 最后一次重要天气的时间
     */
    private Date lastTime;

    public SeriousBO(Date startTime, Date endTime, String airportCode, EvaluationWeather seriousWeather, String weather, Integer locationId, ThresholdEnum threshold) {
        super(startTime, endTime, airportCode, seriousWeather, weather, locationId);
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return super.toString() + "SeriousBO{" +
                "threshold=" + threshold +
                ", lastTime=" + lastTime +
                '}';
    }
}