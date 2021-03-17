package com.emdata.messagewarningscore.common.enums;

public enum EvaluateEnum {
    /**
     * 准确
     */
    ACCURATE("ACCURATE", "准确"),
    /**
     * 基本准确
     */
    BASE_ACCURATE("BASE_ACCURATE", "基本准确"),
    /**
     * 不准确
     */
    INACCURATE("INACCURATE", "不准确"),
    /**
     * 空报
     */
    EMPTY_REPORT("EMPTY_REPORT", "空报"),
    /**
     * 漏报
     */
    UNDER_REPORT("UNDER_REPORT", "漏报"),
    /**
     * 不评
     */
    NO("NO", "不评"),
    ;

    private String code;

    private String name;

    EvaluateEnum(String code, String name) {
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
        EvaluateEnum[] values = values();
        for (EvaluateEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getName();
            }
        }
        return null;
    }

    public static String getCode(String name) {
        EvaluateEnum[] values = values();
        for (EvaluateEnum value : values) {
            if (value.getName().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }
}
