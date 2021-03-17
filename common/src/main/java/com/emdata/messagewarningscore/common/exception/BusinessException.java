package com.emdata.messagewarningscore.common.exception;

/**
 * 自定义异常类
 * Created by changfeng on 2019/9/4.
 */
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public BusinessException(Integer code) {
        this.code = code;
    }

    public BusinessException(String message){
        super(message);
        this.code = -1;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code){
        this.code = code;
    }
}
