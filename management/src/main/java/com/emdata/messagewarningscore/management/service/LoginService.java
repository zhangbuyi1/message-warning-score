package com.emdata.messagewarningscore.management.service;


import com.emdata.messagewarningscore.management.controller.vo.SsoLoginParam;
import com.emdata.messagewarningscore.management.controller.vo.SsoLoginVo;
import com.emdata.messagewarningscore.management.controller.vo.TokenCheckParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author changfeng
 * @description login 业务类
 * @date 2019/12/11
 */
public interface LoginService {
    /**
     * login
     */
    SsoLoginVo login(SsoLoginParam param, HttpServletRequest request);

    void tokenCheck(TokenCheckParam param);

    void logout(HttpServletRequest req, HttpServletRequest request);
}
