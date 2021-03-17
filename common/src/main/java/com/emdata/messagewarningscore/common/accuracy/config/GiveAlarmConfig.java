package com.emdata.messagewarningscore.common.accuracy.config;/**
 * Created by zhangshaohu on 2021/2/7.
 */

import lombok.Builder;
import lombok.Data;

import java.util.Calendar;

/**
 * @author: zhangshaohu
 * @date: 2021/2/7
 * @description: 告警不同阈值配置文件
 */
@Data
@Builder
public class GiveAlarmConfig extends AirportCode {

    @Builder
    @Data
    public static class DbzEcho {
        /**
         * 雷达回波
         */
        private Integer dbzEcho;
        /**
         * 百分比
         */
        private Integer dbzEchoPercentage;
        /**
         * 持续多久
         */
        private Integer continued;
        /**
         * 单位 单位使用Calendar类 如Calendar.HOUR_OF_DAY
         */
        private Integer field;

        /**
         * 返回百分比
         *
         * @return
         */
        public Double getPercentage() {
            return this.dbzEchoPercentage / 100.0;
        }
    }

    /**
     * 机场code
     */
    private String airportCode = "default";
    /**
     * 航路雷达回波配置
     */
    private DbzEcho route = DbzEcho.builder().dbzEcho(45).dbzEchoPercentage(10).field(Calendar.HOUR_OF_DAY).continued(1).build();
    /**
     * 航点雷达回波配置
     */
    private DbzEcho point = DbzEcho.builder().dbzEcho(35).dbzEchoPercentage(20).field(Calendar.HOUR_OF_DAY).continued(1).build();
    /**
     * 范围雷达回波配置
     */
    private DbzEcho range = DbzEcho.builder().dbzEcho(35).dbzEchoPercentage(20).field(Calendar.HOUR_OF_DAY).continued(1).build();

}