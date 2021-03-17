package com.emdata.messagewarningscore.common.accuracy.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by zhangshaohu on 2020/12/30.
 * * 评定对象
 */
public enum EvaluationObject {

    /**
     * 机场预警
     */
    AIRPORT_ALERT("机场预警", "airport"),
    /**
     * 终端区警报
     */
    TERMINAL_ALERT("终端区预警", "ts"),
    /**
     * 重要天气发生概率预报。
     */
    MDRS_ALERT("重要天气", "mdrs");
    private String message;
    private String value;

    EvaluationObject(String message, String value) {
        this.value = value;
        this.message = message;
    }

    public static EvaluationObject build(String message) {
        Optional<EvaluationObject> first = Arrays.stream(EvaluationObject.values()).filter(s -> {
            return s.value.equals(message);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    public String getValue() {
        return value;
    }
}
