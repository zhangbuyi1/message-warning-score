package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.dao.entity.UserAirportDO;
import com.emdata.messagewarningscore.management.controller.vo.LogWriteParam;
import com.emdata.messagewarningscore.management.controller.vo.SsoLoginParam;
import com.emdata.messagewarningscore.management.controller.vo.SsoLoginVo;
import com.emdata.messagewarningscore.management.controller.vo.TokenCheckParam;
import com.emdata.messagewarningscore.common.common.token.LoginTokenInfo;
import com.emdata.messagewarningscore.common.common.token.TokenUtil;
import com.emdata.messagewarningscore.common.common.utils.Guid;
import com.emdata.messagewarningscore.common.common.utils.RedisUtil;
import com.emdata.messagewarningscore.common.common.utils.RequestUtil;
import com.emdata.messagewarningscore.common.common.utils.password.PasswordUtils;
import com.emdata.messagewarningscore.management.entity.AuthorityDO;
import com.emdata.messagewarningscore.management.entity.UserDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.IUserAirportService;
import com.emdata.messagewarningscore.management.service.LogService;
import com.emdata.messagewarningscore.management.service.LoginService;
import com.emdata.messagewarningscore.management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author changfeng
 * @description
 * @date 2019/12/11
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private IUserAirportService iUserAirportService;

    @Override
    public SsoLoginVo login(SsoLoginParam param, HttpServletRequest request) {
        // 密码校验
        LambdaQueryWrapper<UserDO> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.eq(UserDO::getName, param.getUsername()).eq(UserDO::getState, StateEnum.ON.getCode());
        UserDO user = userService.getOne(lqwUser);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_EMPTY.getCode(), ResultCodeEnum.USER_EMPTY.getMessage());
        }
        // 密码加密校验
        if (!PasswordUtils.matches(user.getSalt(), param.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCodeEnum.PASSWORD_ERROR.getCode(), ResultCodeEnum.PASSWORD_ERROR.getMessage());
        }
//        if (!param.getPassword().equals(user.getPassword())) {
//            throw new BusinessException(ResultCodeEnum.PASSWORD_ERROR.getCode(), ResultCodeEnum.PASSWORD_ERROR.getMessage());
//        }
        // 根据用户查询机场代码
        LambdaQueryWrapper<UserAirportDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserAirportDO::getUserId, user.getId());
        UserAirportDO userAirportDO = iUserAirportService.getOne(lqw);
        if (userAirportDO == null) {
            throw new BusinessException(ResultCodeEnum.USER_AIRPORT_EMPTY.getCode(), ResultCodeEnum.USER_AIRPORT_EMPTY.getMessage());
        }
        String airportCode = userAirportDO.getAirportCode();
        // 获取token
        LoginTokenInfo loginTokenInfo = new LoginTokenInfo();
        loginTokenInfo.setUserId(user.getId().toString());
        loginTokenInfo.setUserName(user.getName());
        loginTokenInfo.setPhone(user.getMobile());
        loginTokenInfo.setUuid(Guid.newGUID());
        loginTokenInfo.setLoginTime(new Date());
        Map<String, String> map = new HashMap<>();
        map.put("airportCode", airportCode);
        loginTokenInfo.setExtraInfo(map);
        String token = TokenUtil.createToken(loginTokenInfo);
        log.debug("loginTokenInfo.cacheKey()" + loginTokenInfo.cacheKey());
        // 登陆成功token存入redis
        RedisUtil.addKey(loginTokenInfo.cacheKey(), token, loginTokenInfo.getExpireTime(),
                TimeUnit.MINUTES);
        SsoLoginVo ssoLoginVo = new SsoLoginVo(token);
        BeanUtils.copyProperties(user, ssoLoginVo);
        ssoLoginVo.setAirportCode(airportCode);
        ssoLoginVo.setUserId(user.getId());
        // 手动记录日志
        LogWriteParam logWriteParam = new LogWriteParam();
        logWriteParam.setIp(RequestUtil.getIpAddress(request));
        logWriteParam.setUserName(param.getUsername());
        logWriteParam.setLogType(LogTypeEnum.LOGIN);
        logWriteParam.setOperateTime(new Date());
        logService.addLog(logWriteParam);
        return ssoLoginVo;
    }

    @Override
    public void tokenCheck(TokenCheckParam param) {
        // 根据token获取用户信息
        LoginTokenInfo loginTokenInfo = TokenUtil.verifyToken(param.getToken());
        String tokenInRedis = RedisUtil.get(loginTokenInfo.cacheKey());
        if (tokenInRedis == null) {
            throw new BusinessException(ResultCodeEnum.TOKEN_ERROR.getCode(), ResultCodeEnum.TOKEN_ERROR.getMessage());
        }
        if (!param.getToken().equals(tokenInRedis)) {
            throw new BusinessException(ResultCodeEnum.TOKEN_REMOTE_LOGIN.getCode(), ResultCodeEnum.TOKEN_REMOTE_LOGIN.getMessage());
        }
        // 先查禁用权限列表
        List<AuthorityDO> unauthorityList = userService.queryUnauthorityListByUserId(Integer.valueOf(loginTokenInfo.getUserId()));
        if (CollectionUtils.isNotEmpty(unauthorityList)) {
            for (AuthorityDO unAuth : unauthorityList) {
                if (Pattern.matches("^" + unAuth.getUrl() + ".*", param.getUri().split("\\|")[1])) {
                    throw new BusinessException(ResultCodeEnum.TOKEN_LIMIT.getCode(), ResultCodeEnum.TOKEN_LIMIT.getMessage());
                }
            }
        }
        // 查询权限列表
        List<AuthorityDO> authorityList = userService.queryAuthorityListByUserId(Integer.valueOf(loginTokenInfo.getUserId()));
        if (CollectionUtils.isEmpty(authorityList)) {
            throw new BusinessException(ResultCodeEnum.TOKEN_LIMIT.getCode(), ResultCodeEnum.TOKEN_LIMIT.getMessage());
        }
        // 校验权限
        for (AuthorityDO auth : authorityList) {
            if (Pattern.matches("^" + auth.getUrl() + ".*", param.getUri().split("\\|")[1])) {
                return;
            }
        }
        // 没有请求权限
        throw new BusinessException(ResultCodeEnum.TOKEN_LIMIT.getCode(), ResultCodeEnum.TOKEN_LIMIT.getMessage());
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletRequest request) {
        // 获取token
        String token = req.getHeader("token");
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ResultCodeEnum.TOKEN_EMPTY.getCode(), ResultCodeEnum.TOKEN_EMPTY.getMessage());
        }
        LoginTokenInfo loginTokenInfo = TokenUtil.verifyToken(token);
        if (loginTokenInfo == null) {
            throw new BusinessException(ResultCodeEnum.TOKEN_ERROR.getCode(), ResultCodeEnum.TOKEN_ERROR.getMessage());
        }
        String tokenKey = loginTokenInfo.cacheKey();
        // 删除token
        RedisUtil.del(tokenKey);
        // 手动记录日志
        LogWriteParam logWriteParam = new LogWriteParam();
        logWriteParam.setIp(RequestUtil.getIpAddress(request));
        logWriteParam.setUserName(loginTokenInfo.getUserName());
        logWriteParam.setLogType(LogTypeEnum.LOGOUT);
        logWriteParam.setOperateTime(new Date());
        logService.addLog(logWriteParam);
    }

}
