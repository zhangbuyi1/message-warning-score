package com.emdata.messagewarningscore.common.enums;

/**
 * @description:
 * @date: 2021/1/6
 * @author: sunming
 */
public enum LocationTypeEnum {
    /**
     * 终端区
     */
    TERMINAL("terminal", "终端区"),
    /**
     * 机场
     */
    AIRPORT("airport", "机场"),

    /**
     * 航路
     */
    ROUTE("route", "航路"),
    /**
     * 关键点
     */
    KEY_POINT("keyPoint", "关键点"),

    /**
     * 进近区
     */
    APPROACH_AREA("approachArea", "进近区");

    private String code;

    private String name;

    LocationTypeEnum(String code, String name) {
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
        LocationTypeEnum[] values = values();
        for (LocationTypeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getName();
            }
        }
        return null;
    }

    public static String getCode(String name) {
        LocationTypeEnum[] values = values();
        for (LocationTypeEnum value : values) {
            if (value.getName().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }
}
