package com.emdata.messagewarningscore.common.accuracy.entity;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import lombok.Data;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description:
 */
@Data
public class MainWeather {
    /**
     * 类型
     */
    private EvaluationWeather type;
    /**
     * 主天气现象
     */
    private List<ForcastWeather> forcastWeathers;
}
