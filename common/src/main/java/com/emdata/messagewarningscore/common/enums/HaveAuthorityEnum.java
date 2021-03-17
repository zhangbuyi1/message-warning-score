package com.emdata.messagewarningscore.common.enums;

/**
 * @author zhoukai
 * @date 2020/3/21 13:36
 */
public enum HaveAuthorityEnum {
    /**
     * 是否拥有权限
     */
    YES(1, "是"),
    NO(0, "否");

    private Integer code;
    private String message;

    HaveAuthorityEnum(Integer code, String message) {
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
        for (HaveAuthorityEnum statusEnum : HaveAuthorityEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum.getMessage();
            }
        }
        return null;
    }
}
