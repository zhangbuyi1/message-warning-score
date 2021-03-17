package com.emdata.messagewarningscore.data.parse.impl.lhj.weatherService.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommonAdaptee;
import com.emdata.messagewarningscore.data.parse.impl.lhj.weatherService.WeatherService;
import com.emdata.messagewarningscore.data.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Desc ThunderstormServiceImpl
 * @Author lihongjiang
 * @Date 2020/12/24 16:33
 **/
@Slf4j
@Service
public class ThunderstormServiceImpl extends AlertCommonAdaptee implements WeatherService {

    private static List<String> STRENGTHS = new ArrayList<>();
    private static List<String> units = new ArrayList<>();
    private static List<String> subWeatherTypes = new ArrayList<>();

    static {
        // 天气强度名称
        STRENGTHS.add("较强");
        STRENGTHS.add("大风");
        STRENGTHS.add("大雨");
        STRENGTHS.add("强降水");
        STRENGTHS.add("强对流");
        STRENGTHS.add("大阵雨");
        STRENGTHS.add("雷雨");
        // 天气单位
        units.add("公里");
        units.add("km");
        units.add("米");
        // 子天气类型
        subWeatherTypes.add("对流云团");
        subWeatherTypes.add("降水云团");
        subWeatherTypes.add("阵雨云团");
        subWeatherTypes.add("降雨云团");
    }

    @Override
    public AlertContentResolveDO parseTextWeather(AlertOriginalTextDO airportAlert) {
        // 文本内容
        String textContent = contentThreadLocal.get();
        // 文本内容实例
        AlertContentResolveDO alertContent = new AlertContentResolveDO();
        alertContent.setWarningType(airportAlert.getAlertType());
        alertContent.setAffectedArea(WarningTypeEnum.AIRPORT_WARNING.getWarningType());
        alertContent.setWarningNature(super.getWarningNature(textContent));
//        // 这两个字段用于测试
//        alertContent.setTextContent(originalThreadLocal.get());
//        alertContent.setReadableTextContent(textContent);
        // 天气类型 1雷暴 2能见度 3低云 4降雪 5风
        alertContent.setWeatherType(1);
        // 范围
        alertContent.setPredictRange(super.getRang(textContent, keywords.get(0), keywords.get(1)));
        // 强度
        alertContent.setStrength(super.getStrength(textContent, STRENGTHS));
        // 子天气类型
        alertContent.setSubWeatherType(super.getSubWeatherType(textContent, keywords.get(1), keywords.get(2)));
        // 预测开始和结束时间
        String dateTimeRange = StringUtil.interceptByKey(textContent, keywords.get(3), keywords.get(4));
        // 1、若时间里包含了天气，则删除天气。
        List<String> collect1 = allWeather.stream().filter(dateTimeRange::contains).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(collect1)) {
            for (String wea : collect1) {
                dateTimeRange = dateTimeRange.replace(wea, "");
            }
        }
        super.setForecastTime(alertContent, dateTimeRange, ymThreadLocal.get(), ymdhmsThreadLocal.get(), start1ThreadLocal.get());
        // 开始时间不为空，且为首个预计，则设置首个预计的开始时间。
        if(1 == numThreadLocal.get()){
            String format = Java8DateUtils.format(alertContent.getPredictStartTime(), Java8DateUtils.DATE_TIME);
            start1ThreadLocal.set(format);
        }
        // 天气信息（主附天气信息：名称/最小值/最大值）
        super.setWeatherInfo(alertContent, textContent, subWeatherTypes);
        return alertContent;
    }

}
