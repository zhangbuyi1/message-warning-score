package com.emdata.messagewarningscore.management.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
@Data
public class AreaBO {

    @ApiModelProperty(value = "地区/点编码")
    private String code;

    @ApiModelProperty(value = "地区/点名称")
    private String name;

    @ApiModelProperty(value = "是否有下级地点,0--没有，1--有", example = "1")
    private Integer hasChildren;

    public AreaBO() {
    }

    public AreaBO(String code, String name, Integer hasChildren) {
        this.code = code;
        this.name = name;
        this.hasChildren = hasChildren;
    }
}
