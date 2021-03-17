package com.emdata.messagewarningscore.common.common.token;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: pupengfei
 * @date: 2019/3/21
 * @description
 */
public class TokenUtil {

    private static final String HMAC_KEY = "9527car#!B";

    private static final String ISSUER = "meteorology_user_service";

    /**
     * 验证token，并返回储存的信息
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    public static LoginTokenInfo verifyToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(HMAC_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build(); //Reusable verifier instance

        DecodedJWT jwt = verifier.verify(token);
        // 获取相关信息
//        String subject = jwt.getSubject();
//
//        List<String> audience = jwt.getAudience();
//        System.out.println(subject);
//        System.out.println(audience.get(0));
//
//        System.out.println(jwt.getIssuer());

        Map<String, Claim> claims = jwt.getClaims();
        Claim claim = claims.get("loginTokenInfo");
        LoginTokenInfo loginTokenInfo = JSONObject.parseObject(claim.asString(), LoginTokenInfo.class);
        return loginTokenInfo;
    }

    public static String createToken(LoginTokenInfo loginTokenInfo) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(HMAC_KEY);
        //头部信息
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        Date nowDate = new Date();
        // 设置过期时间
        Date expireDate = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(loginTokenInfo.getExpireTime()));

        String token = JWT.create()
                /*设置头部信息 Header*/
                .withHeader(map)
                /*设置 载荷 Payload*/
                // 签名是有谁生成 例如 服务器
                .withIssuer(ISSUER)
                //签名的主题
                .withSubject(ISSUER + " for " + loginTokenInfo.getLoginTerminal())
                //定义在什么时间之前，该jwt都是不可用的.
                //.withNotBefore(new Date())
                //签名的观众 也可以理解谁接受签名的
                .withAudience(loginTokenInfo.getLoginTerminal())
                //生成签名的时间
                .withIssuedAt(nowDate)
                //签名过期的时间
                .withExpiresAt(expireDate)
                .withClaim("loginTokenInfo", JSONObject.toJSONString(loginTokenInfo))
                /*签名 Signature */
                .sign(algorithm);
        return token;
    }
}
