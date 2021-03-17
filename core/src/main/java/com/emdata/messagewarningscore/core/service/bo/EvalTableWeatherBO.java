package com.emdata.messagewarningscore.core.service.bo;

import com.emdata.messagewarningscore.common.parse.element.Weather;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 评估表格返回实体类
 * @date: 2020/12/16
 * @author: sunming
 */
@Data
public class EvalTableWeatherBO {


    @ApiModelProperty(value = "评估结果")
    private String evalResult;

    @ApiModelProperty(value = "提前量")
    private Double lead;

    @ApiModelProperty(value = "偏差量")
    private Double deviation;

    @ApiModelProperty(value = "重合率")
    private Double coinRate;

    private List<WeatherScore> weatherList;

    @Data
    public static class WeatherScore {
        @ApiModelProperty(value = "日期(yyyy-MM-dd)")
        private String hour;

        private WeatherScoreInfo warn;

        private WeatherScoreInfo metar;
    }

    @Data
    public static class WeatherScoreInfo {
        @ApiModelProperty(value = "名称")
        private String name;

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

