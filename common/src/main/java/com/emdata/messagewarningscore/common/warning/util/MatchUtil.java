package com.emdata.messagewarningscore.common.warning.util;/**
 * Created by zhangshaohu on 2020/11/27.
 */

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: zhangshaohu
 * @date: 2020/11/27
 * @description:
 */
public class MatchUtil {
    public static List<String> getList(String str, String pattern, List<String> strings) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(str);
        if (matcher.find()) {
            String group = matcher.group();
            strings.add(group);
            String replace = str.replace(group, "\n");
            getList(replace, pattern, strings);
        }
        return strings;
    }

    public static String get(String str, String pattern) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static String get(String str, List<String> pattern) {
        for (String pa : pattern) {
            String s = get(str, pa);
            if (!StringUtils.isEmpty(s)) {
                return s;
            }
        }
        return "";
    }
}