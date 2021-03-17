package com.emdata.messagewarningscore.data.parse.impl.lhj.common;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.emdata.messagewarningscore.common.dao.entity.ImportantWeatherDO;
import com.emdata.messagewarningscore.data.util.StringUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Desc AirportUtils
 * @Author lihongjiang
 * @Date 2020/12/25 10:52
 **/
@Component
public class AirportUtils {

    // 删除特别的符号（只针对雷暴）
    public static String deleteSpecialSymbol(String inStr){
        String outStr = inStr;
        if (StringUtils.isNotBlank(outStr)) {
            if(outStr.contains("届时解除该份机场警报")){
                outStr = outStr.replace("届时解除该份机场警报", "");
            }
        }
        if (StringUtils.isNotBlank(outStr)) {
            if(outStr.contains("（雷暴）")){
                outStr = outStr.replace("（雷暴）", "");
            }
        }
        if (StringUtils.isNotBlank(outStr)) {
            if (outStr.contains("（") && !outStr.contains("）")) {
                outStr = outStr.replace("（", "");
            }
        }
        if (StringUtils.isNotBlank(outStr)) {
            if (!outStr.contains("（") && outStr.contains("）")) {
                outStr = outStr.replace("）", "");
            }
        }
        if (StringUtils.isNotBlank(outStr)) {
            if(outStr.substring(outStr.length()-1).contains("。")){
                outStr = outStr.substring(0, outStr.length()-1);
            }
        }
        if (StringUtils.isNotBlank(outStr)) {
            if(outStr.substring(outStr.length()-1).contains("，")){
                outStr = outStr.substring(0, outStr.length()-1);
            }
        }
        if (StringUtils.isNotBlank(outStr)) {
            if(outStr.substring(outStr.length()-1).contains("等")){
                outStr = outStr.substring(0, outStr.length()-1);
            }
        }
        if (StringUtils.isNotBlank(outStr)) {
            if(outStr.substring(outStr.length()-2).contains("天气")){
                outStr = outStr.substring(0, outStr.length()-2);
            }
        }
        return outStr;
    }

    // 删除特别的符号（只针对风）
    public static String deleteSpecialSymbol1(String inStr){
        String outStr = inStr;
        if (StringUtils.isNotBlank(inStr)) {
            if (outStr.contains("的")) {
                outStr = outStr.replace("的", "");
            }
            if (outStr.contains("以上")) {
                outStr = outStr.replace("以上", "");
            }
            if (outStr.contains("波动")) {
                outStr = outStr.replace("波动", "");
            }
            if (outStr.contains("。")) {
                outStr = outStr.replace("。", "");
            }
        }
        return outStr;
    }

    // 9:00，虹桥机场能见度维持在800米以上，RVR短时在350米
    public static List<String> getDateTime(String textStr){
        List<String> dateTime = Lists.newArrayList();
        if (textStr.contains("日")) {
            String[] times = textStr.split("日");
            List<String> timeList = StringUtil.deleteIsEmptyData(Arrays.asList(times));
            // 添加天日
            addDay(dateTime, timeList.get(0).trim());
            // 添加时间
            addTime(dateTime, timeList.get(1).trim());
        }else{
            dateTime.add("");
            addTime(dateTime, textStr);
        }
        return dateTime;
    }

    public static void addDay(List<String> dateTime, String textStr){
        String text = textStr;
        if (textStr.length()<2) {
            text = "0" + textStr;
        }
        dateTime.add(text);
    }

    public static void addTime(List<String> dateTime, String textStr){
        String time = "";
        if (StringUtils.isNotBlank(textStr)) {
            String replace = textStr.replace("：", ":");
            if (replace.contains(":")) {
                String[] split = replace.split(":");
                String prefix = split[0];
                if (prefix.length()<2) {
                    prefix = "0" + prefix;
                }
                time = prefix + ":" + split[1].substring(0, 2);
            }
        }
        dateTime.add(time);
    }

    public static ImportantWeatherDO getImportantWeather(String str){
        ImportantWeatherDO importantWeather = new ImportantWeatherDO();
        importantWeather.setCode("");
        importantWeather.setName(str);
        return importantWeather;
    }

}
