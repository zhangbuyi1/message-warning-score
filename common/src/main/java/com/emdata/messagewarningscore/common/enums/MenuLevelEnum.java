package com.emdata.messagewarningscore.common.enums;

/**
 * @author zhoukai
 * @date 2020/3/21 13:36
 */
public enum MenuLevelEnum {
    /**
     * 菜单等级枚举类
     */
    ONE(1, "一级"),
    TWO(2, "二级"),
    THREE(3, "三级");

    private Integer code;
    private String message;

    MenuLevelEnum(Integer code, String message) {
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
        for (MenuLevelEnum statusEnum : MenuLevelEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum.getMessage();
            }
        }
        return null;
    }
}
