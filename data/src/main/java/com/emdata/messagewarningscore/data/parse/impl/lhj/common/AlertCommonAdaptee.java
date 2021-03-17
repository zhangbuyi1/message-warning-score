package com.emdata.messagewarningscore.data.parse.impl.lhj.common;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.enums.WarningNatureEnum;
import com.emdata.messagewarningscore.data.dao.entity.Weather;
import com.emdata.messagewarningscore.data.util.StringUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Desc AlertCommonAdaptee
 * @Author lihongjiang
 * @Date 2020/12/25 11:09
 **/
@Component
public class AlertCommonAdaptee extends AlertCommon {

    /**
     * 通过单位获取【主天气数值】
     *
     * @param alertContent 1
     * @param mainStr 1主天气 2附加天气
     * @param unit 1主天气 2附加天气
     **/
    public void setMainWeatherValue(AlertContentResolveDO alertContent, String mainStr, String unit){
        if (mainStr.contains(unit)) {
            String mainValue = mainStr.replace(unit, "");
            if (mainValue.contains("-")) {
                String[] split1 = mainValue.split("-");
//                alertContent.setWeatherMinNum(AirportUtils.deleteSpecialSymbol1(split1[0]));
//                alertContent.setWeatherMaxNum(AirportUtils.deleteSpecialSymbol1(split1[1]));
            }else{
                if (mainValue.contains("以上")) {
//                    alertContent.setWeatherMinNum(AirportUtils.deleteSpecialSymbol1(mainValue));
                }else {
//                    alertContent.setWeatherMaxNum(AirportUtils.deleteSpecialSymbol1(mainValue));
                }
            }
        }
    }

    /**
     * 通过单位获取【附加天气数值】
     *
     * @param alertContent 1
     * @param additionalStr 1主天气 2附加天气
     * @param unit 1主天气 2附加天气
     **/
    public void setAdditionalWeatherValue(AlertContentResolveDO alertContent, String additionalStr, String unit){
        if (additionalStr.contains(unit)) {
            String additionalValue = additionalStr.replace(unit, "");
            if (additionalValue.contains("-")) {
                String[] split1 = additionalValue.split("-");
//                alertContent.setAdditionalMinNum(AirportUtils.deleteSpecialSymbol1(split1[0]));
//                alertContent.setAdditionalMaxNum(AirportUtils.deleteSpecialSymbol1(split1[1]));
            }else{
//                alertContent.setAdditionalMinNum(AirportUtils.deleteSpecialSymbol1(additionalValue));
            }
        }
    }

    public String getRang(String textContent, String startKey, String endKey){
        String rang = StringUtil.interceptByKey(textContent, startKey, endKey);
        String original = originalThreadLocal.get();
        if (StringUtil.isExistByKeyWord(original, typhoons)) {
            if (original.contains("中心位于") && original.contains("中心附近")) {
                rang = StringUtil.interceptByKey(original, "中心位于", "中心附近");
            }
        }
        // 若依然是空值，则取虹桥机场等关键字
        if (StringUtils.isBlank(rang)) {
            if (textContent.contains("虹桥机场")) {
                rang = "虹桥机场";
            }
        }
        return rang;
    }

    /**
     * 获取强度（雷暴）
     **/
    public String getStrength(String textContent, List<String> keys){
        String strength = "中";
        // 强度
        String strengthText = textContent;
        if (strengthText.contains("伴")) {
            String[] strengths = strengthText.split("伴");
            strengthText = strengths[0];
        }
        // 匹配关键字，确认强弱。
        if (StringUtil.isExistByKeyWord(strengthText, keys)) {
            strength = "强";
        }
        return strength;
    }

    /**
     * 子天气类型名称（雷暴）
     **/
    public String getSubWeatherType(String textContent, String str5, String str6){
        // 子天气类型名称
        String subWeatherType = StringUtil.interceptByKey(textContent, str5, str6);
        if (subWeatherType.contains("天气")) {
            subWeatherType = StringUtil.interceptByKey(subWeatherType, "", "天气");
        }
        if (StringUtils.isBlank(subWeatherType)) {
            subWeatherType = StringUtil.getKeyWord(originalThreadLocal.get(), typhoons);
        }
        subWeatherType = AirportUtils.deleteSpecialSymbol(subWeatherType);
        return subWeatherType;
    }

    /**
     * 预测开始和结束时间（默认雷暴）
     **/
    public void setForecastTime(AlertContentResolveDO alertContent, String dateTimeRange, String yearMonth, String ymdhms, String start1){
        // 若不存在"-"，则在前面加上该符号。
        if (!dateTimeRange.contains("-")) {
            dateTimeRange = dateTimeRange + "- ";
        }
        String[] dateTimeRanges = dateTimeRange.split("-");
        List<String> timeTextList = Arrays.asList(dateTimeRanges);
        // 默认取预报发布时间
        String startDateTime = ymdhms;
        // 开始时间
        String start = timeTextList.get(0);
        if (StringUtils.isNotBlank(start)) {
            List<String> startTime = AirportUtils.getDateTime(start);
            String day = startTime.get(0);
            if (StringUtils.isBlank(day)) {
                if (StringUtils.isNotBlank(start1)) {
                    day = start1.substring(8, 10);
                }else{
                    day = ymdhms.substring(8, 10);
                }
            }
            startDateTime = yearMonth + "-" + day + " " + startTime.get(1) + ":00";
        }
        Date startDate = null;
        if (StringUtils.isNotBlank(startDateTime)) {
            startDate = Java8DateUtils.parseDateTimeStr(startDateTime, Java8DateUtils.DATE_TIME);
        }
        // 默认取预报发布时间
        String endDateTime = "";
        // 结束时间
        String end = timeTextList.get(1);
        if (StringUtils.isNotBlank(end)) {
            List<String> endTime = AirportUtils.getDateTime(end);
            String endDate = endTime.get(0);
            if (StringUtils.isBlank(endDate)) {
                endTime.set(0, startDateTime.substring(8, 10));
            }
            endDateTime = yearMonth + "-" + endTime.get(0) + " " + endTime.get(1) + ":00";
        }
        Date endDate = null;
        if (StringUtils.isNotBlank(endDateTime)) {
            endDate = Java8DateUtils.parseDateTimeStr(endDateTime, Java8DateUtils.DATE_TIME);
        }
        // 如果开始时间大于结束时间，说明跨天了，结束时间需要加24小时。
        if (null != startDate && null != endDate) {
            if (startDate.compareTo(endDate) > 0) {
                endDate = Java8DateUtils.addHour(endDate, 24L);
            }
        }
        // 预报开始时间
        alertContent.setPredictStartTime(startDate);
        // 预报结束时间
        alertContent.setPredictEndTime(endDate);
    }

    /**
     * 伴随天气（雷暴）
     **/
    public String getAccompanyWeather(String content){
        String accompanyWeather = "";
        // 先截伴
        if (content.contains("伴")) {
            String[] accompany = content.split("伴");
            accompanyWeather = accompany[1];
        }
        // 再截其它
        if (accompanyWeather.contains("。届时虹桥机场及五边还将出现")) {
            accompanyWeather = accompanyWeather.replace("。届时虹桥机场及五边还将出现", "，");
        }
        if (accompanyWeather.contains("届时解除")) {
            accompanyWeather = accompanyWeather.replace("届时解除", "，");
        }
        return AirportUtils.deleteSpecialSymbol(accompanyWeather);
    }

    /**
     * 警告性质（雷暴）
     **/
    public String getWarningNature(String content){
        // 内容包含更正，则为更正；否则是首份。例：虹桥机场气象台对2020年06月27日第4份机场警报进行更正，更正后的警报内容为：...
        if (content.contains("更正")) {
            return WarningNatureEnum.UPDATE.getMessage();
        }else {
            return WarningNatureEnum.FIRST.getMessage();
        }
    }

    /**
     * 天气信息
     **/
    public void setWeatherInfo(AlertContentResolveDO alertContent, String textContent, List<String> subWeatherTypes){
        // 天气名称
        String allWeather = StringUtil.interceptByKey(textContent, "重要截取", "。");
        // 替换符号
        allWeather = StringUtil.replaceSymbol(allWeather);
        // 若只剩下逗号，则清空天气。
        if ("，".equals(allWeather) || "，，".equals(allWeather) || "，，，".equals(allWeather)) {
            allWeather = "";
        }
        // 天气名称，若最后仍然是空的，则从"子天气类型"中取出匹配的关键字
        if (StringUtils.isBlank(allWeather)) {
            String subWeatherType = alertContent.getSubWeatherType();
            if (StringUtils.isNotBlank(subWeatherType)) {
                allWeather = StringUtil.getKeyWord(subWeatherType, subWeatherTypes);
            }
            if (2 == alertContent.getWarningType()) {
                if(subWeatherType.contains("风切变") && subWeatherType.contains("颠簸")){
                    allWeather = allWeather.concat("，").concat("风切变").concat("，").concat("颠簸");
                }
            }
        }
        alertContent.setWeatherName(allWeather);
        // 所有天气
        List<ForcastWeather> allWeatherList = Lists.newArrayList();
        String[] weatherNames = allWeather.split("，");
        for (String weatherName : weatherNames) {
            List<Weather> sorts = Lists.newArrayList(weathers);
            sorts.sort(Comparator.comparing(Weather::getSort));
            for (Weather weather : sorts) {
                Optional<String> first = Arrays.stream(weather.getNames().split(",")).filter(weatherName::contains).findFirst();
                if (first.isPresent() && (!weatherName.contains("结束"))) {
                    String describe = first.get();
                    ForcastWeather wea = new ForcastWeather();
                    wea.setDescribe(describe);
                    wea.setCode(weather.getCode());
                    wea.setPriority(weather.getPriority());
                    weatherName = weatherName.replace(describe, "");
                    if (StringUtils.isNotBlank(weatherName)) {
                        if (weatherName.contains("~")) {
                            // 有短时
                            String[] split1 = weatherName.split("~");
                            // 天气大小值
                            String s0 = split1[0];
                            if (s0.contains("-")) {
                                String[] split11 = s0.split("-");
                                wea.setMin(Integer.parseInt(split11[0]));
                                wea.setMax(Integer.parseInt(split11[1]));
                            }else{
                                if (StringUtils.isNotBlank(s0)) {
                                    wea.setMin(Integer.parseInt(s0));
                                    wea.setMax(Integer.parseInt(s0));
                                }
                            }
                            // 天气短时大小值
                            String s1 = split1[1];
                            if (s1.contains("-")) {
                                String[] split12 = s1.split("-");
                                wea.setShortMin(Integer.parseInt(split12[0]));
                                wea.setShortMax(Integer.parseInt(split12[1]));
                            }else{
                                if (StringUtils.isNotBlank(s1)) {
                                    wea.setShortMin(Integer.parseInt(s1));
                                    wea.setShortMax(Integer.parseInt(s1));
                                }
                            }
                        }else{
                            // 无短时
                            if (weatherName.contains("-")) {
                                String[] split1 = weatherName.split("-");
                                wea.setMin(Integer.parseInt(split1[0]));
                                wea.setMax(Integer.parseInt(split1[1]));
                            }else{
                                if (StringUtils.isNotBlank(weatherName)) {
                                    wea.setMin(Integer.parseInt(weatherName));
                                    wea.setMax(Integer.parseInt(weatherName));
                                }
                            }
                        }
                    }
                    allWeatherList.add(wea);
                    break;
                }
            }
        }
        // 所有天气重排序
        allWeatherList.sort(Comparator.comparing(ForcastWeather::getPriority));
        // 主要天气
        List<ForcastWeather> mainWeatherList = Lists.newArrayList();
        // 附加天气
        List<ForcastWeather> additionalWeatherList = Lists.newArrayList();
        for(int i=0; i<allWeatherList.size(); i++){
            if (0 == i) {
                mainWeatherList.add(allWeatherList.get(i));
            }else{
                additionalWeatherList.add(allWeatherList.get(i));
            }
        }
        alertContent.setMainWeatherJson(mainWeatherList);
        alertContent.setAddiWeatherJson(additionalWeatherList);
    }

}
