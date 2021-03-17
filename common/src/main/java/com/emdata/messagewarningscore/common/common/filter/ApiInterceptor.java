package com.emdata.messagewarningscore.common.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.emdata.messagewarningscore.common.common.token.LoginTokenInfo;
import com.emdata.messagewarningscore.common.common.token.TokenUtil;
import com.emdata.messagewarningscore.common.common.utils.RedisUtil;
import com.emdata.messagewarningscore.common.common.utils.ThreadLocalUtils;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description 拦截器
 */
@Slf4j
public class ApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestMethod = request.getMethod();
        response.setCharacterEncoding("utf-8");
        // 获取到token
        String token = request.getHeader("token");
        ResultData resultData = new ResultData();
        if (StringUtils.isEmpty(token)) {
            resultData.setCode(ResultCodeEnum.TOKEN_EMPTY.getCode());
            resultData.setMsg(ResultCodeEnum.TOKEN_EMPTY.getMessage());
            response.getWriter().write(JSONObject.toJSONString(resultData));
            return false;
        } else {
            // 验证token是否过期
            LoginTokenInfo loginTokenInfo = null;
            try {
                loginTokenInfo = TokenUtil.verifyToken(token);
            } catch (TokenExpiredException e) {
                // 过期
                resultData.setCode(ResultCodeEnum.TOKEN_EXPIRED.getCode());
                resultData.setMsg(ResultCodeEnum.TOKEN_EXPIRED.getMessage());
                response.getWriter().write(JSONObject.toJSONString(resultData));
                return false;
            } catch (JWTVerificationException e) {
                // 无效
                resultData.setCode(ResultCodeEnum.TOKEN_ERROR.getCode());
                resultData.setMsg(ResultCodeEnum.TOKEN_ERROR.getMessage());
                response.getWriter().write(JSONObject.toJSONString(resultData));
                return false;
            }

            //验证token是否有效
            String appRedisToken = RedisUtil.get(loginTokenInfo.cacheKey());
            //如果token无效，重新登录
            if (!token.equals(appRedisToken)) {
                log.info("token:{}", token);
                log.warn("token无效");
                resultData.setCode(ResultCodeEnum.TOKEN_ERROR.getCode());
                resultData.setMsg(ResultCodeEnum.TOKEN_ERROR.getMessage());
                response.getWriter().write(JSONObject.toJSONString(resultData));
                return false;
            }
        }

        // 如果机场uuid 为空
        ResultData commonResultData = saveThreadLocal(request);
        if (ResultCodeEnum.AIRPORT_CODE_EMPTY.getCode() == commonResultData.getCode()) {
            response.getWriter().write(JSONObject.toJSONString(commonResultData));
            return false;
        }
        return true;
    }

    public ResultData saveThreadLocal(HttpServletRequest request) {
        ResultData resultData = new ResultData();
        String airportCode = request.getHeader("airportCode");
        if (StringUtils.isEmpty(airportCode)) {
            // 从token中取
            String token = request.getHeader("token");
            LoginTokenInfo loginTokenInfo = TokenUtil.verifyToken(token);
            airportCode = loginTokenInfo.getExtraInfo().get("airportCode");
            if (StringUtils.isEmpty(airportCode)) {
                resultData.setCode(ResultCodeEnum.AIRPORT_CODE_EMPTY.getCode());
                resultData.setMsg(ResultCodeEnum.AIRPORT_CODE_EMPTY.getMessage());
                return resultData;
            } else {
                ThreadLocalUtils.setAirportCode(airportCode);
                resultData.setCode(200);
                return resultData;
            }
        } else {
            ThreadLocalUtils.setAirportCode(airportCode);
            resultData.setCode(200);
            return resultData;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Thread thread = Thread.currentThread();

    }


}