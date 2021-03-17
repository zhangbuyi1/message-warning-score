package com.emdata.messagewarningscore.common.enums;

/**
 * @author zhoukai
 * @date 2020/3/21 13:36
 */
public enum ResultCodeEnum {
    /**
     * 返回结果状态
     */
    SUCCESS(0, "请求成功"),

    ERROR(-1, "系统繁忙，请稍后重试"),

    // 权限 4000-4999

    TOKEN_EMPTY(4000, "token为空，请检查"),
    TOKEN_ERROR(4003, "token无效"),
    TOKEN_EXPIRED(4004, "token已过期"),
    TOKEN_REMOTE_LOGIN(4007, "账号已在其他设备登录"),
    TOKEN_LIMIT(4008, "权限不足,请联系管理员！"),

    USER_EMPTY(4100, "未查到该用户信息，请确认"),
    USER_EXIST(4101, "用户名已存在"),
    USER_ADD_ERROR(4102, "用户创建失败"),
    NEW_MEMBER(4103, "新用户，需要弹窗同意"),
    USER_UPDATE_ERROR(4104, "用户信息更新失败"),
    USER_DELETE_ERROR(4105, "用户删除失败"),
    AUTHORITY_EXIST(4106, "该url权限已存在，请检查"),
    AUTHORITY_ADD_ERROR(4107, "新增权限信息失败"),
    AUTHORITY_NOT_EXIST(4108, "该权限不存在"),
    AUTHORITY_UPDATE_ERROR(4109, "权限信息更新失败"),
    AUTHORITY_DELETE_ERROR(4110, "权限删除失败"),
    ROLE_NOT_EXIST(4111, "该角色不存在"),
    ROLE_AUTHORITY_ADD_ERROR(4112, "角色权限关系新增失败"),
    ROLE_AUTHORITY_NOT_EXIST(4113, "该角色权限关系不存在"),
    ROLE_ADD_ERROR(4114, "角色信息新增失败"),
    ROLE_UPDATE_ERROR(4115, "角色信息更新失败"),
    ROLE_DELETE_ERROR_CAUSE_USER(4116, "删除角色前请先解绑用户"),
    ROLE_DELETE_ERROR(4117, "角色删除失败"),
    ROLE_USER_DELETE_ERROR(4118, "角色用户关系删除失败"),
    ROLE_USER_UPDATE_ERROR(4119, "角色用户关系更新失败"),
    ROLE_USER_ADD_ERROR(4120, "角色用户关系保存失败"),
    ROLE_USER_NOT_EXIST(4121, "该角色用户关系不存在"),
    AUTHORITY_DELETE_ERROR_CAUSE_CHILD(4122, "删除上级权限前请先删除子权限"),
    ROLE_AUTHORITY_DELETE_ERROR(4123, "角色权限关系删除失败"),
    PASSWORD_ERROR(4124, "密码错误"),
    MENU_NOT_EXIST(4125, "该菜单不存在"),
    MENU_AUTHORITY_ADD_ERROR(4126, "菜单权限关系新增失败"),
    MENU_AUTHORITY_DELETE_ERROR(4127, "菜单权限关系删除失败"),
    MENU_ADD_ERROR(4128, "新增菜单信息失败"),
    MENU_UPDATE_ERROR(4129, "菜单信息更新失败"),
    MENU_DELETE_ERROR_CAUSE_CHILD(4130, "删除上级菜单前请先删除子菜单"),
    MENU_DELETE_ERROR_CAUSE_ROLE(4131, "删除菜单前请先解绑角色"),
    MENU_DELETE_ERROR(4132, "菜单删除失败"),
    ROLE_MENU_ADD_ERROR(4133, "角色菜单关系新增失败"),
    ROLE_MENU_DELETE_ERROR(4134, "角色菜单关系删除失败"),
    MENU_HAS_EXIST(4135, "该菜单路由已存在"),
    PARENT_AUTHORITY_NOT_EXIST(4136, "父级权限不存在"),
    MENU_INDEX_CONFLICT(4137, "菜单顺序冲突"),

    AREA_HAS_EXIST(5001, "该地点已存在"),
    AREA_ADD_ERR(5002, "地点新增失败"),
    PARENT_AREA_NOT_EXIST(5003, "上级地点不存在"),
    AREA_NOT_EXIST(5004, "该地点不存在"),
    AREA_DELETE_ERRO(5005, "地点删除失败"),
    AREA_UPDATE_ERRO(5006, "地点信息更新失败"),


    AIRPORT_CODE_EMPTY(6001, "机场代码为空"),
    USER_AIRPORT_EMPTY(6002, "该用户所属机场为空");


    private Integer code;
    private String message;

    ResultCodeEnum(Integer code, String message) {
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
        for (ResultCodeEnum statusEnum : ResultCodeEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum.getMessage();
            }
        }
        return null;
    }
}
