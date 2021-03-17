package com.emdata.messagewarningscore.common.accuracy.enums;/**
 * Created by zhangshaohu on 2021/1/11.
 */

import java.util.Arrays;
import java.util.Optional;

/**
 * @author: zhangshaohu
 * @date: 2021/1/11
 * @description:
 */

public enum WarningNature {
    /***
     * 首份
     */
    FIRST("first", "首份", 1),
    /**
     * 修改
     */
    UPDATE("update", "修改", 2),
    /**
     * 解除报文
     */
    RELIEVE("relieve", "解除", 3);
    private String value;
    private Integer code;
    private String message;

    WarningNature(String value, String message, Integer code) {
        this.value = value;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static WarningNature build(String value) {
        Optional<WarningNature> first = Arrays.stream(WarningNature.values()).filter(s -> {
            return s.value.equals(value);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }
}