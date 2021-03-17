package com.emdata.messagewarningscore.data.http.entity;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@Data
public class MethodInfo {
    private String url;
    /**
     * 請求類型
     */
    private HttpMethod httpMethod;
    /**
     * url傳參
     */
    private Map<String, Object> urlParam;
    /**
     * body
     */
    private Object body;

    private Map<String, Object> requstParam;
    /**
     * 返回值
     */
    private Class<?> returnType;

}