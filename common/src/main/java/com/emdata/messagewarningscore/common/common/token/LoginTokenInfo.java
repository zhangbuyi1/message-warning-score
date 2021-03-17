package com.emdata.messagewarningscore.common.common.token;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: pupengfei
 * @date: 2019/4/28
 * @description
 */
@Data
public class LoginTokenInfo {

    public static final String LOGIN_REDIS_PREFIX = "login.user";

    public static final String LOGOUT_REDIS_PREFIX = "logout.user";

    /**
     * 该token 的uuid（登出时，用作key）
     */
    private String uuid;

    private String phone;

    private String userId;

    private String userName;

    /**
     * 登录终端，例如123车管APP：123app
     */
    private String loginTerminal = "no-terminal";

    /**
     * 过期时间，单位：min，默认：21600（15天）
     */
    private Integer expireTime = 21600;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 储存扩展的信息
     */
    private Map<String, String> extraInfo = new HashMap<>();

    /**
     * 获取用户id-登录平台唯一字符串，用以redis中缓存token
     * @return
     */
    public String cacheKey() {
        return LOGIN_REDIS_PREFIX + ":" + this.loginTerminal + ":" + this.userId ;
    }

    /**
     * 登出时，保存该token登出信息的key，value：
     * @return
     */
    public String logoutCacheKey() {
        return LOGOUT_REDIS_PREFIX + ":" + this.loginTerminal + ":" + uuid;
    }

}
