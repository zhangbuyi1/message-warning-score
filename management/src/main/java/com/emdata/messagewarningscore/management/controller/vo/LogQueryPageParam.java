package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * 
 * @description 日志分页查询参数
 * @date 2019/12/14
 */
@Data
public class LogQueryPageParam extends BaseQuery{

    @ApiModelProperty(value = "操作用户")
    private String userName;

    @ApiModelProperty(value = "操作类型,0:登录 1:登出 10:查询 11:新增 12:更新 13:删除 20:下载 ", example = "1")
    private Integer type;

    /**
     * 是否成功 1是 0否
     */
    @ApiModelProperty(value = "是否成功 1是 0否", example = "1")
    private Integer success;

}
