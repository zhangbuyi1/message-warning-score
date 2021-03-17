package com.emdata.messagewarningscore.data.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc 字符串截取工具类
 * @Author lihongjiang
 * @Date 2020/12/23 16:59
 **/
public class StringUtil {

    /**
     * 替换符号
     **/
    public static String replaceSymbol(String text) {
        return text
                .replace("影响减弱，届时解除该份机场警报", "")
                // 机场
                .replace("中或大阵雨", "中阵雨或大阵雨")

                .replace("短时雷雨", "，雷雨")
                .replace("短时", "，")

                .replace("雨量中到大", "")
                .replace("轻度到中度", "")
                .replace("轻到中度", "")
                .replace("轻-中度", "")
                .replace("中等强度", "")
                .replace("中强度", "")
                .replace("中度", "")
                .replace("轻度", "")

                .replace("持续性", "")
                .replace("间歇性", "")
                .replace("下降至", "")
                .replace("上升至", "")
                .replace("稳定", "")
                .replace("维持", "")
                .replace("波动", "")
                .replace("以上", "")
                .replace("以", "")

                .replace("低空", "")
                .replace("天气", "")
                .replace("现象", "")
                .replace("为主", "")
                .replace("局地", "")
                .replace("出现", "")

                .replace("米/秒", "")
                .replace("米", "")

                .replace("在", "")
                .replace("等", "")
                .replace("的", "")
                .replace("（", "")
                .replace("）", "")

                .replace("，伴", "，")
                .replace("伴", "，")
                .replace("和", "，")
                .replace("或", "，")
                .replace("及", "，")
                .replace("的", "，")
                .replace("、", "，")
                .replace("。", "，")


                // 终端区
                .replace("局部", "，")

                .replace("云中隐嵌对流", "")
                .replace("南压至南部", "")
                .replace("有分散性", "")
                .replace("东部有", "")
                .replace("分散", "")
                .replace("大部", "")
                .replace("影响", "")
                .replace("较强", "")
                .replace("西部", "")
                .replace("北部", "")
                .replace("还将", "")
                .replace("发展", "")
//                .replace("局地", "") // 上面已有

                ;
    }

    /**
     * 判断字符串中，是否包含某个关键字
     *
     * @param textStr 字符串
     * @param keys 关键字集
     * @return 是/否
     **/
    public static Boolean isExistByKeyWord(String textStr, List<String> keys){
        for (String key : keys) {
            if (textStr.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过keys，获取字符串中包含的某个关键字
     *
     * @param textStr 字符串
     * @param keys 关键字集
     * @return 关键字
     **/
    public static String getKeyWord(String textStr, List<String> keys){
        for (String key : keys) {
            if (textStr.contains(key)) {
                return key;
            }
        }
        return "";
    }

    /**
     * 通过keys，删除关键字
     *
     * @param textStr 字符串
     * @param keys 关键字集
     * @return 字符串
     **/
    public static String deleteKey(String textStr, List<String> keys) {
        String newTextStr = textStr;
        for (String key : keys) {
            if (textStr.contains(key)) {
                newTextStr = textStr.replace(key, "");
            }
        }
        return newTextStr;
    }

    /**
     * 通过两个关键字获取之间的字符串（不包含关键字本身）
     *
     * @param textStr 字符串
     * @param startKey 开始关键字
     * @param endKey 结束关键字
     * @return 两个关键字获取之间的字符串
     **/
    public static String interceptByKey(String textStr, String startKey, String endKey) {
        String regex = startKey + "(.*)" + endKey;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textStr);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * 通过关键字删除之间的字符串
     *
     * @param textStr 字符串
     * @param startKey 开始关键字
     * @param endKey 结束关键字
     * @return 删除之后的字符串
     **/
    public static String deleteBetweenByKey(String textStr, String startKey, String endKey) {
        String regex = "(.*)" + startKey + "(.*)" + endKey + "(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textStr);
        while (matcher.find()) {
            return matcher.group(1) + startKey + matcher.group(3);
        }
        return textStr;
    }

    /**
     * 删除list中为空的数据
     *
     * @param texts 源数据
     * @return 新数据
     **/
    public static List<String> deleteIsEmptyData(List<String> texts){
        List<String> newTexts = Lists.newArrayList();
        for (String text : texts) {
            if(StringUtils.isNotBlank(text)){
                newTexts.add(text);
            }
        }
        return newTexts;
    }

}
