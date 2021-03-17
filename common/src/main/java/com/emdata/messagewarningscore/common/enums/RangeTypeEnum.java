package com.emdata.messagewarningscore.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
public enum RangeTypeEnum {
    /**
     * 点
     */
    POINT("point", "点"),
    /**
     * 线
     */
    LINE("line", "线"),

    /**
     * 面
     */
    SURFACE("surface", "面"),;

    private String code;

    private String name;

    RangeTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getName(String code) {
        RangeTypeEnum[] values = values();
        for (RangeTypeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getName();
            }
        }
        return null;
    }

    public static String getCode(String name) {
        RangeTypeEnum[] values = values();
        for (RangeTypeEnum value : values) {
            if (value.getName().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

    public static RangeTypeEnum build(String code) {
        Optional<RangeTypeEnum> first = Arrays.stream(RangeTypeEnum.values()).filter(s -> {
            return s.code.equals(code);
        }).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }
}
