package com.emdata.messagewarningscore.core.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @date: 2021/1/13
 * @author: sunming
 */
@Data
public class VisCbaseRvrBO {
    @ApiModelProperty(value = "强度")
    private String preStrength;

    private Integer preVis;

    private Integer preCbase;

    private Integer preRvr;
    @ApiModelProperty(value = "实况强度")
    private String metarStrength;

    private Integer metarVis;

    private Integer metarCbase;

    private Integer metarRvr;
}
