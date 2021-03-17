package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * @description 权限关系vo
 * @date 2020/2/26
 */
@Data
public class AuthorityRelationVo {

    @ApiModelProperty(value = "authorityId", example = "1")
    private Integer authorityId;

    private String authorityName;

    private AuthorityRelationVo childVo;
}
