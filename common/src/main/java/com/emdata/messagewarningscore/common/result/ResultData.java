package com.emdata.messagewarningscore.common.result;

import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoukai
 * @date 2020/3/21 13:35
 */
@Data
public class ResultData<T> implements Serializable {
    private static final long serialVersionUID = -3124326062679793506L;

    private int code;
    private T data;
    private String msg;

    public ResultData() {
    }

    public ResultData(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public ResultData(int code, T data, String msg) {
        super();
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return this.code == ResultCodeEnum.SUCCESS.getCode();
    }

    public static <T> ResultData<T> success(T data) {
        return new ResultData<>(ResultCodeEnum.SUCCESS.getCode(), data, ResultCodeEnum.SUCCESS.getMessage());
    }

    public static  <T> ResultData<T> success() {
        return new ResultData<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    }

    public static <T> ResultData<T> error(int code) {
        return new ResultData<>(code, null, ResultCodeEnum.getMsgByCode(code));
    }
    public static <T> ResultData<T> error(String error) {
        return new ResultData<>(ResultCodeEnum.ERROR.getCode(), null, error);
    }

    public static <T> ResultData<T> error(int code, String error) {
        return new ResultData<>(code, null, error);
    }
}
