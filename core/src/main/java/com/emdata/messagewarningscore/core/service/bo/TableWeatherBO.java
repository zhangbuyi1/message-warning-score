package com.emdata.messagewarningscore.core.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 重要天气自动记录表格中返回实体类
 * @date: 2020/12/15
 * @author: sunming
 */
@Data
public class TableWeatherBO {

    @ApiModelProperty(value = "时间点")
    private String hour;

    private List<WeatherInfo> weatherInfoList;

    @Data
    public static class WeatherInfo {
        @ApiModelProperty(value = "天气类型")
        private String type;

        @ApiModelProperty(value = "强度值")
        private String strength;

        @ApiModelProperty(value = "能见度")
        private Integer vis;
        @ApiModelProperty(value = "云底高")
        private Integer cBase;
        @ApiModelProperty(value = "rvr")
        private Integer rvr;
    }
}
