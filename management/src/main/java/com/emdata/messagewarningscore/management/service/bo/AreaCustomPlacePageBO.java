package com.emdata.messagewarningscore.management.service.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: pupengfei
 * @date: 2020/2/26
 * @description:
 */
@Data
public class AreaCustomPlacePageBO {

    @ApiModelProperty(value = "id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "地点编码")
    private String placeCode;

    @ApiModelProperty(value = "地点名称")
    private String placeName;

    @ApiModelProperty(value = "省编码")
    private String provinceCode;

    @ApiModelProperty(value = "省名称")
    private String provinceName;

    @ApiModelProperty(value = "市编码")
    private String cityCode;

    @ApiModelProperty(value = "")
    private String cityName;

    @ApiModelProperty(value = "")
    private String districtCode;

    @ApiModelProperty(value = "")
    private String districtName;

    @ApiModelProperty(value = "")
    private String levelOneName;

    @ApiModelProperty(value = "")
    private String levelOneCode;

    @ApiModelProperty(value = "")
    private String levelTwoName;

    @ApiModelProperty(value = "")
    private String levelTwoCode;

    @ApiModelProperty(value = "")
    private String levelThreeName;

    @ApiModelProperty(value = "")
    private String levelThreeCode;

    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
