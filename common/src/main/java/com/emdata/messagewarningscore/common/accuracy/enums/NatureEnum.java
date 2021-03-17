package com.emdata.messagewarningscore.common.accuracy.enums;

/**
 * Created by zhangshaohu on 2020/12/30.
 * * 预警性质  命中 空报 漏报
 */
public enum NatureEnum {
    /**
     * 命中
     */
    HIT("命中"),
    /**
     * 漏报
     */
    LEAK("漏报"),
    /**
     * 空报
     */
    EMPTY("空报"),
    /**
     * 不参与评分
     */
    NOT_SCORE("不评"),

    /**
     * 部分准确
     */
    PART_TRUE("部分准确");

    private String message;

    public String getMessage() {
        return message;
    }

    NatureEnum(String message) {
        this.message = message;
    }
}
