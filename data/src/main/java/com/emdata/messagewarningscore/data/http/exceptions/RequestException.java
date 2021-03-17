package com.emdata.messagewarningscore.data.http.exceptions;/**
 * Created by zhangshaohu on 2021/1/20.
 */

/**
 * @author: zhangshaohu
 * @date: 2021/1/20
 * @description: 调用请求异常
 */
public class RequestException extends RuntimeException {
    /**
     * 请求异常code
     */
    private Integer code;

    public RequestException(String message, Integer code) {
        super(message);
        this.code = code;
    }
}