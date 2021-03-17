package com.emdata.messagewarningscore.common.accuracy.config;/**
 * Created by zhangshaohu on 2021/1/11.
 */

import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2021/1/11
 * @description:
 */
@Data
public class OtherConfig extends AirportCode {
    /**
     * ）没有预报某种天气，实际在机场相对繁忙时段
     * （06:00-22:00）出现持续 1 小时以上，其他时段出现持续 2
     * 小时以上该天气则为漏报，否则不参评；
     * （
     */
    @Data
    public class TimeInterval {
        private int startHour = 6;
        private int startMin = 0;
        private int startSecond = 0;
        private int endHour = 22;
        private int endMin = 0;
        private int endSecond = 0;
    }

    /**
     * 繁忙时段
     */
    private TimeInterval peakTime = new TimeInterval();
    /**
     * 繁忙时段时间重要天气持续出现peakTimeNum 为漏报
     */
    private Integer peakTimeNum = 1;
    /**
     * 非繁忙时段
     */
    private Integer othertimeNum = 2;
    /**
     * 重要天气间隔时间
     */
    private Long seriousInterval = 1000 * 60 * 60 * 2L;

}