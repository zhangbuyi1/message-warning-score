package com.emdata.messagewarningscore.common.accuracy.config;/**
 * Created by zhangshaohu on 2020/12/21.
 */

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description: 降雪、冻降水 配置文件
 */
@Data
public class RaSnConfig extends AirportCode {
    /**
     * 小雪（各地根据用户服务协议确定小
     * 雪是否评定及起评的标准，上报备案）、中到大雪、冻降水。
     * 米雪、冰粒、雨夹雪等固态降水等同于降雪。
     */
    private List<String> rasns = new ArrayList<String>() {{
        add("-SN");
        add("SN");
        add("+SN");
        add("SG");
        add("PL");
        add("SNRA");
    }};
    /**
     * 降雪伴随的现象：跑道积雪、高吹雪、低吹雪等现象
     */
    private List<String> accompanyWeather = new ArrayList<String>() {
        {
            add("BLSN");
            add("DRSN");
        }
    };

    /**
     * 若预报的伴随天气在预报时段外前后 2h 内均未出现，
     * 则该份预报成功质量得分扣 2 分
     * 雷暴天气减分阈值
     */
    private Integer accompanyReductionScoreTime = 2;
}