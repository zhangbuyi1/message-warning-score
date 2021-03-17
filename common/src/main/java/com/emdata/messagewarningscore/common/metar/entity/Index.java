package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2020/10/12.
 */


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: zhangshaohu
 * @date: 2020/10/12
 * @description:
 */
@Data
public class Index {
    /**
     * 报文头开始index目标
     */
    protected Integer startIndex = 0;
    /**
     * 报文头结束index 目标
     */
    protected Integer endIndex = 1;

    /**
     * 实际元素开始位置
     */
    protected Integer actualStartIndex = -1;
    /**
     * 实际元素结束索引
     */
    protected Integer actualEndIndex = -1;

    /**
     * 根据正则表达式取值
     *
     * @param message
     * @param pattern
     * @return
     */
    protected String match(String message, String pattern) {
        return matchAn(message, pattern);
    }


    protected String matchAll(String message, String pattern, StringBuilder weather) {
        return get(message, pattern, weather);
    }

    public static String matchAn(String message, String pattern) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(message);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        return null;
    }

    /**
     * @param message
     * @param pattern
     * @return
     */
    public static String get(String message, String pattern) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(message);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        return null;
    }

    public static void main(String[] args) {
        String s = get("ZSNB", "((DZ|RA|SN|SG|PL|GR|GS)+)", new StringBuilder());

    }

    /**
     * 从所有天气中搜索满足正则的天气
     *
     * @param message
     * @param pattern
     * @param weather
     * @return
     */
    public static String get(String message, String pattern, StringBuilder weather) {
        message = addSpace(message);

        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(message);
        if (matcher.find()) {
            String group = matcher.group();
            weather.append(group.trim());
            weather.append(" ");
            String replace = message.replace(group + " ", " ");
            if (replace.trim().equals(message.trim())) {
                return null;
            }
            get(replace, pattern, weather);
        }
        if (StringUtils.isEmpty(weather.toString())) {
            return null;
        }
        return weather.toString().trim();
    }

    private static String deleteGroup(String message, String group) {
        final boolean[] isWeather = {false};
        String reduce = Arrays.stream(message.trim().split(" ")).map(s -> {
            if (StringUtils.isEmpty(s.replace(group, "").trim())) {
                isWeather[0] = true;
                return null;
            }
            if (isWeather[0]) {
                return s;
            }
            return s.replace(group.trim(), "").trim();
        }).filter(s -> {
            return StringUtils.isNotEmpty(s);
        }).reduce("", (s, s2) -> {
            return s + " " + s2;
        });
        return reduce;
    }


    /**
     * @param message
     * @param element
     */
    protected void indexActual(String message, String element) {
        if (StringUtils.isNotEmpty(element)) {
            String[] split = element.split(" ");
            String start = split[0];
            String end = split[split.length - 1];
            int startIndex = message.indexOf(start);
            int endIndex = message.lastIndexOf(end);
            this.actualStartIndex = startIndex;
            this.actualEndIndex = endIndex;
        }
    }


    /**
     * 前后加空格
     *
     * @param message
     * @return
     */
    protected static String addSpace(String message) {
        return " " + message + " ";
    }

}