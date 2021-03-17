package com.emdata.messagewarningscore.common.accuracy.enums;

/**
 * Created by zhangshaohu on 2020/12/30.
 */
public enum WeatherType {
    /**
     * 雷暴对流 如果预报只有雷暴 没有降水的情况
     */
    TS("TS", "雷暴对流"),
    /**
     * 包括雷暴降水
     * 降水
     */
    RA("RA", "中度及以上降水"),
    /**
     * 降雪冻降水
     */
    RASN("RASN", "降雪冻降水"),
    /**
     * 冰雹
     */
    GRGS("GRGS", "冰雹"),
    /**
     * 龙卷
     */
    FC("FC", "龙卷"),
    /**
     * 风
     */
    WIND("WIND", "大风"),

    /**
     * 云
     */
    CLOUD("CLOUD", "云"),

    /**
     * 能见度
     */
    VIS("VIS", "能见度"),

    /**
     * 垂直能见度
     */
    VVVIS("VVVIS", "垂直能见度"),
    /**
     * 跑道视尘
     */
    RVR("RVR", "跑道视尘");

    private String code;
    private String message;

    WeatherType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
