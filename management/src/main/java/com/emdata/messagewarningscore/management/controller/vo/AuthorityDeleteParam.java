package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author changfeng
 * @version 1.0
 * @description 权限删除入参
 * @date 2019/12/14
 */
@Data
public class AuthorityDeleteParam {

    @ApiModelProperty(value = "主键id", example = "1")
    @NotNull(message = "主键id不能为空")
    private Integer id;

}
