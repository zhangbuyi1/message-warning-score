package com.emdata.messagewarningscore.common.enums;

/**
 * @author zhoukai
 * @date 2020/3/21 13:36
 */
public enum LogTypeEnum {

    /**
     * 日志类型枚举
     */
    LOGIN(0, "登录"),
    LOGOUT(1, "登出"),
    SELECT(10, "查询"),
    INSERT(11, "新增"),
    UPDATE(12, "更新"),
    DELETE(13, "删除"),
    DOWLOAD(20, "下载");


    private Integer type;
    private String message;

    LogTypeEnum(Integer type, String message) {

        this.type = type;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
