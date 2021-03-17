package com.emdata.messagewarningscore.common.common.aop;

import com.alibaba.fastjson.annotation.JSONField;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 〈一句话功能简述〉<br>
 * 〈日志基础信息记录类〉
 * 通过切面获取日志信息，封装为该类信息
 *
 * @author pupengfei
 * @create 2020/2/24
 * @since 1.0.0
 */
@Data
public class BaseLog {

    /**
     * 日志类型
     */
    private LogTypeEnum logType;

    /**
     * 日志内容
     */
    private String contents;

    /**
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    // 是否成功 1是 0否
    private Integer success;

    // 错误原因
    private String errorReason;

}