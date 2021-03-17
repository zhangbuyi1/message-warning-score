package com.emdata.messagewarningscore.management.controller.vo;

import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author changfeng
 * @description 日志记录请求参数
 * @date 2019/12/11
 */
@Data
public class LogWriteParam {

    // 菜单id
    private Integer menuId;

    // 用户名
    private String userName;

    // 日志类型
    private LogTypeEnum logType;

    // 操作时间
    private Date operateTime;

    // 内容
    private String contents;

    // ip
    private String ip;

    // 是否成功 1是 0否
    private Integer success;

    // 错误原因
    private String errorReason;
}