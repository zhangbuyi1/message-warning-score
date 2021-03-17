package com.emdata.messagewarningscore.common.accuracy.config;/**
 * Created by zhangshaohu on 2020/12/21.
 */

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description: 雷暴对流配置文件
 */
@Data
public class TSConfig extends AirportCode {
    /**
     * 雷达回波范围 宽
     */
    private Integer wide = 20;
    /**
     * 雷达回波范围 高
     */
    private Integer height = 30;
    /**
     * 雷达回波强度
     */
    private Integer radarEcho = 35;
    /**
     * 雷达覆盖范围
     */
    private Integer radarCoverage = 20;

    /**
     * 部分准确百分比
     */
    private Integer floatingRange = 20;
    /**
     * 雷暴伴随天气：大风（≥15m/s（包括阵风））
     */
    private Integer accompanyWindSpeed = 15;
    /**
     * 强降
     * 水（+TSRA、+RA、+SHRA）、冰雹（GR 或 GS）、龙卷（FC）、
     */
    private List<String> accompanyWeather = new ArrayList<String>() {
        {
            add("+TSRA");
            add("+RA");
            add("+SHRA");
            add("GR");
            add("GS");
            add("FC");
        }
    };
    /**
     * 低云低能见度等天气。
     */
    private Integer accompanyVis = 800;
    /**
     * 低云60
     */
    private Integer accompanyCloudHeight = 60;

    /**
     * 若预报的伴随天气在预报时段外前后 2h 内均未出现，
     * 则该份预报成功质量得分扣 2 分
     * 雷暴天气减分阈值
     */
    private Integer accompanyReductionScoreTime = 2;
}