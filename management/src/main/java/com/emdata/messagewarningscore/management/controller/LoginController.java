package com.emdata.messagewarningscore.management.controller;

import com.emdata.messagewarningscore.management.controller.vo.SsoLoginParam;
import com.emdata.messagewarningscore.management.controller.vo.SsoLoginVo;
import com.emdata.messagewarningscore.management.controller.vo.TokenCheckParam;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author changfeng
 * @description 单点登录相关接口
 * @date 2019/12/9
 */
@Api(tags = "用户登录", description = "用户登录相关接口")
@RestController
@RequestMapping("sso")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 登录--唯一一个手动记录日志，因为没有token
     */
    @ApiOperation(value = "登录")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<SsoLoginVo> doLogin(@RequestBody @Valid SsoLoginParam param, HttpServletRequest request) {
        return ResultData.success(loginService.login(param, request));
    }

    /**
     * token检查
     */
    @ApiOperation(value = "token检查")
    @PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData check(@RequestBody @Valid TokenCheckParam param) {
        loginService.tokenCheck(param);
        return ResultData.success();
    }

    /**
     * 登出--唯二一个手动记录日志，因为没有参数
     */
    @ApiOperation(value = "登出")
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData doLogout(HttpServletRequest req, HttpServletRequest request) {
        loginService.logout(req, request);
        return ResultData.success();
    }


}
