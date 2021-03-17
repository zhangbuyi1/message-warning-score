package com.emdata.messagewarningscore.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 影响地域枚举类
 */
public enum AffectedAreaEnum {
    /**
     * 终端区
     */
    TERMINAL("terminal", "终端区"),
    /**
     * 机场
     */
    AIRPORT("airport", "机场"),
    /**
     * 主干道路
     */
    MAINROAD("mainRoad", "主干道路");

    private String code;
    private String message;

    AffectedAreaEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public static String getValue(String code) {
        Optional<AffectedAreaEnum> first = Arrays.stream(values()).filter(a -> a.getCode().equals(code)).findFirst();
        return first.map(AffectedAreaEnum::getMessage).orElse(null);
    }
}
