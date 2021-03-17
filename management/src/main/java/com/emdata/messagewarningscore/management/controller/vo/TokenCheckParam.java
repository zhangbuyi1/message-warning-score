package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author changfeng
 * @description token检查
 * @date 2019/12/11
 */
@Data
public class TokenCheckParam {

    @ApiModelProperty(value = "请求地址", required = true)
    @NotBlank(message = "请求地址不能为空")
    private String uri;

    @ApiModelProperty(value = "token", required = true)
    @NotBlank(message = "token不能为空")
    private String token;
}
