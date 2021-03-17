package com.emdata.messagewarningscore.common.accuracy.enums;


import java.util.Arrays;
import java.util.Optional;

/**
 * Created by zhangshaohu on 2020/12/30.
 * 评定天气
 */

public enum EvaluationWeather {
    /**
     * 雷暴（对流）
     */
    TS(1, "雷暴", "TS"),
    /**
     * 低云低能见度
     */
    LOW_VIS_LOWCLOUD(2, "低云低能见度", "FOG"),
    /**
     * 降雪冻降水
     */
    RN_FZRA(3, "降雪冻降水", "SN"),
    /**
     * 中度及以上降水
     */
    RA(4, "中度以上降水", "RA");
    private Integer code;
    private String shortCode;
    private String message;

    EvaluationWeather(Integer code, String message, String shortCode) {
        this.code = code;
        this.message = message;
        this.shortCode = shortCode;

    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public String getShortCode() {
        return shortCode;
    }

    public static EvaluationWeather build(Integer code) {
        Optional<EvaluationWeather> first = Arrays.stream(EvaluationWeather.values()).filter(s -> {
            return s.getCode().equals(code);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public static EvaluationWeather build(String message) {
        Optional<EvaluationWeather> first = Arrays.stream(EvaluationWeather.values()).filter(s -> {
            return s.getCode().equals(message);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

}
