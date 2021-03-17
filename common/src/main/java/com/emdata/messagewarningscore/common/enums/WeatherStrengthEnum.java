package com.emdata.messagewarningscore.common.enums;

public enum WeatherStrengthEnum {
    /**
     * 强
     */
    STRONG("S", "强"),
    /**
     * 中
     */
    MIDDLE("M", "中"),
    ;

    private String code;

    private String name;

    WeatherStrengthEnum(String code, String name) {
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
        WeatherStrengthEnum[] values = values();
        for (WeatherStrengthEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getName();
            }
        }
        return null;
    }

    public static String getCode(String name) {
        WeatherStrengthEnum[] values = values();
        for (WeatherStrengthEnum value : values) {
            if (value.getName().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }
}
