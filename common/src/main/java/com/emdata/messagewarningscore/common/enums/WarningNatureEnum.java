package com.emdata.messagewarningscore.common.enums;

/**
 * @description: 预警性质枚举类
 * @date: 2020/12/11
 * @author: sunming
 */
public enum WarningNatureEnum {

    /**
     * 首份
     */
    FIRST("first"),
    /**
     * 更新
     */
    UPDATE("update");

    private String message;

     WarningNatureEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
