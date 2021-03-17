package com.emdata.messagewarningscore.common.accuracy.config;/**
 * Created by zhangshaohu on 2020/12/21.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description:
 */
public class ThresholdConfig {
    private final static String defaultKey = "default";
    /**
     * 低云低能见度配置文件
     */
    private static List<LowVisibilityConfig> lowVisibilityConfigs = new ArrayList<LowVisibilityConfig>() {
        {
            add(new LowVisibilityConfig());
        }
    };
    /**
     *
     */
    private static List<GiveAlarmConfig> giveAlarmConfigs = new ArrayList<GiveAlarmConfig>() {{
        {
            add(GiveAlarmConfig.builder().airportCode("ZSPD").build());
        }
    }};
    /**
     * 降雪 冻降水配置文件
     */
    private static List<RaSnConfig> raSnConfigs = new ArrayList<RaSnConfig>() {{
        add(new RaSnConfig());
    }};
    /**
     * 雷暴 对流配置文件
     */
    private static List<TSConfig> tsConfigs = new ArrayList<TSConfig>() {{
        add(new TSConfig());
    }};
    /**
     * 其他配置
     */
    private static List<OtherConfig> otherConfigs = new ArrayList<OtherConfig>() {{
        add(new OtherConfig());
    }};

    /**
     * 返回
     *
     * @param airportCode
     * @return
     */
    public static GiveAlarmConfig getGiveAlarmConfig(String airportCode) {
        return get(giveAlarmConfigs, airportCode);
    }

    /**
     * 根据机场代码 返回低云低能见度配置文件
     *
     * @param airportCode
     * @return
     */
    public static LowVisibilityConfig getLowVisibilityConfig(String airportCode) {
        return get(lowVisibilityConfigs, airportCode);
    }

    public static OtherConfig getOtherConfig(String airportCode) {
        return get(otherConfigs, airportCode);
    }


    /**
     * 根据机场代码 返回降雪 冻降水配置文件
     *
     * @param airportCode
     * @return
     */
    public static RaSnConfig getRaSnConfig(String airportCode) {
        return get(raSnConfigs, airportCode);
    }

    /**
     * 根据机场代码返回 雷暴
     *
     * @param airportCode
     * @return
     */
    public static TSConfig getTsConfig(String airportCode) {
        return get(tsConfigs, airportCode);
    }

    private static <T extends AirportCode> T get(List<T> all, String airportCode) {
        Optional<T> first = all.stream().filter(s -> {
            return s.getAirportCode().equals(airportCode);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            Optional<T> first1 = all.stream().filter(s -> {
                return s.getAirportCode().equals(defaultKey);
            }).findFirst();
            if (first1.isPresent()) {
                return first1.get();
            }
        }
        return null;
    }
}