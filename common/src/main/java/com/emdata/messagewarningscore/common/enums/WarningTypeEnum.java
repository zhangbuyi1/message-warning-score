package com.emdata.messagewarningscore.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 警报类型枚举类
 */
public enum WarningTypeEnum {
    /**
     * 预警类型:1-机场 2-终端区 3-区域
     */
    /**
     * 机场警报
     */
    AIRPORT_WARNING("airport", "机场警报", 1),

    /**
     * 区域警报
     */
    AREA_WARNING("area", "区域预警", 3),

    /**
     * 终端区警报
     */
    TERMINAL_WARNING("terminal", "终端区警报", 2),
    /**
     * MDRS
     */
    MDRS_WARNING("mdrs", "MDRS", 4);

    private String warningType;

    private String message;

    private Integer code;

    WarningTypeEnum(String warningType, String message, Integer code) {
        this.warningType = warningType;
        this.message = message;
        this.code = code;
    }

    public String getWarningType() {
        return warningType;
    }

    public String getMessage() {
        return message;
    }

    public static WarningTypeEnum build(String warngingType) {
        Optional<WarningTypeEnum> first = Arrays.stream(WarningTypeEnum.values()).filter(s -> {
            return s.warningType.equals(warngingType);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public static WarningTypeEnum build(Integer code) {
        Optional<WarningTypeEnum> first = Arrays.stream(WarningTypeEnum.values()).filter(s -> {
            return s.code.equals(code);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }
}
