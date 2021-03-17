package com.emdata.messagewarningscore.common.enums;


/**
 * @author zhoukai
 * @date 2020/3/21 13:36
 */
public enum StateEnum {
    /**
     * 记录状态枚举类
     */
    ON(1, "正常"),

    OFF(9, "已删除");

    private Integer code;
    private String message;

    StateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据code获取msg
     *
     * @param code
     * @return
     */
    public static String getMsgByCode(Integer code) {
        for (StateEnum statusEnum : StateEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum.getMessage();
            }
        }
        return null;
    }
}
