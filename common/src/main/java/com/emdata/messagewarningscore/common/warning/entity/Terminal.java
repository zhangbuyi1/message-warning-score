package com.emdata.messagewarningscore.common.warning.entity;/**
 * Created by zhangshaohu on 2020/12/9.
 */

import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import com.sun.org.apache.regexp.internal.RE;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/12/9
 * @description:
 */
public class Terminal {
    public static void main(String[] args) {
        String text = "【终端区天气预警】受浅层切变影响，" +
                "预计上海终端区有中等及以上降水云团发展，（并伴有颠簸和风切变），云顶高度6-8千米。" +

                "北京时间15日08:00-19:00小到中阵雨影响上海终端区，" +

                "其中11:00-19:00终端区局地短时大阵雨，" +

                "虹桥、浦东机场及五边，降水以小到中阵雨为主，" +

                "其中12:00-16:00短时大阵雨。" +
                "09月15日07:56";

        Terminal.Ter(text);
    }

    /**
     * 所有时间的正则
     * 预计目前-18:00
     * 13:00-18:00
     * 4日08:00-18:00
     * 17日01:00-09:00
     */
    static String datePatt = "(目前-\\d{2}:\\d{2})|(\\d{2}日\\d{2}:\\d{2}-\\d{2}:\\d{2})|(\\d{1}日\\d{2}:\\d{2}-\\d{2}:\\d{2})|(\\d{2}:\\d{2}-\\d{2}:\\d{2})";
    /**
     * 拆分的所有
     */
    List<String> terminal = new ArrayList<>();
    static List<String> weatherKeyword = new ArrayList<String>() {{
        add("对流云团");
        add("阵雨伴CB");
        add("短时大阵雨");
        add("小到中阵雨");
    }};
    static List<String> dirKeyword = new ArrayList<String>() {{
        add("上海终端区");
        add("终端区");
        add("上海两场");
        add("上海及浦东");
        add("虹桥、浦东机场及五边");
        add("上海");
        add("虹桥");
    }};
    static String splitPatt = "\\。|\\.|\\,|\\，|\\；|\\;";

    public static void Ter(String txt) {
        Pattern compile = Pattern.compile(splitPatt);
        String[] split = compile.split(txt);
        final StringBuilder[] all = {new StringBuilder()};
        List<String> collect = Arrays.stream(split).map(s -> {
            all[0].append(s + ",");
            String section = all[0].toString();
            if (isContainsKeyWord(section) && isContainsDate(section) && isContainsDirKeyWord(section)) {
                all[0] = new StringBuilder();
                return section;
            }
            return null;
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());
        List<String> strings = new ArrayList<>();
        collect.stream().map(s -> {
            String[] split1 = s.split(splitPatt);
            final StringBuilder[] alla = {new StringBuilder()};
            List<String> collect1 = Arrays.stream(split1).map(c -> {
                alla[0].append(s + ",");
                String section = alla[0].toString();
                if (isContainsKeyWord(section) && isContainsDirKeyWord(section)) {
                    all[0] = new StringBuilder();
                    return section;
                }
                return null;
            }).filter(c -> {
                return c != null;
            }).collect(Collectors.toList());
            strings.addAll(collect1);
            return collect1;
        }).collect(Collectors.toList());
        int a = 0;
    }

    private Terminal(String text) {
        Pattern compile = Pattern.compile(splitPatt);
        String[] split = compile.split(text);
        final StringBuilder[] all = {new StringBuilder()};
        List<String> collect = Arrays.stream(split).map(s -> {
            all[0].append(s + ",");
            String section = all[0].toString();
            if (isContainsKeyWord(section) && isContainsDate(section) && isContainsDirKeyWord(section)) {
                all[0] = new StringBuilder();
                return section;
            }
            return null;
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());
        // 得到相同地点的拆分
        List<String> strings = getTerminal(text, collect);
        // 将相同地点的拆分 拆分成  单个时间地点元素
        List<List<String>> collect1 = strings.stream().map(s -> {
            List<String> list = MatchUtil.getList(s, datePatt, new ArrayList<>());
            // 如果当前存在两个时间
            if (list.size() >= 2) {
                // 先取得地点
                String dir = getDir(s);
                // 根据时间拆分
                List<String> splitByDate = splitByDate(list, s);
                return splitByDate.stream().map(e -> {
                    return e.replace(dir, "") + "--->" + dir;
                }).collect(Collectors.toList());
            }
            return Arrays.asList(s);
        }).collect(Collectors.toList());
        ArrayList<String> strings1 = new ArrayList<>();
        collect1.stream().map(s -> {
            strings1.addAll(s);
            return s;
        }).collect(Collectors.toList());
        this.terminal = strings1;
    }

    public List<String> splitByDate(List<String> list, String all) {
        Pattern compile = Pattern.compile(splitPatt);
        String[] split = compile.split(all);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            stringBuilder.append(split[i]);
            String an = stringBuilder.toString();


        }
        List<String> allList = new ArrayList<>();
        String reduce = list.stream().reduce("", (s1, s2) -> {
            if (StringUtils.isEmpty(s1)) {
                allList.add(all.substring(0, all.indexOf(s2) + s2.length()));
            }
            allList.add(all.substring(all.indexOf(s1) + s1.length(), all.indexOf(s2) + s2.length()));
            return s2;
        });
        allList.add(all.substring(all.indexOf(reduce) + reduce.length(), all.length()));
        return allList;
    }

    /**
     * 对齐方法
     *
     * @return
     */
    public List<String> padding() {


        return null;
    }

    /**
     * 将同一地点 不同时间的要素放在一起
     *
     * @param text
     * @param collect
     * @return
     */
    private List<String> getTerminal(String text, List<String> collect) {
        ArrayList<String> strings = new ArrayList<>();
        String reText = text;
        for (int i = 0; i < collect.size(); i++) {
            if (i == collect.size() - 1) {
                String red = reText.substring(0, reText.length());
                reText = reText.replace(red, "");
                strings.add(red);
            } else {
                String s = collect.get(i);
                String red = reText.substring(0, s.length());
                reText = reText.replace(red, "");
                strings.add(red);
            }
        }
        return strings;
    }

    private static boolean isContainsKeyWord(String txt) {
        Optional<String> first = weatherKeyword.stream().filter(k -> {
            return txt.contains(k);
        }).findFirst();
        return first.isPresent();
    }

    private static boolean isContainsDate(String txt) {
        String s = MatchUtil.get(txt, datePatt);
        return !s.isEmpty();
    }

    private static boolean isContainsDirKeyWord(String txt) {
        Optional<String> first = dirKeyword.stream().filter(k -> {
            return txt.contains(k);
        }).findFirst();
        return first.isPresent();
    }

    private String getDir(String txt) {
        Optional<String> first = dirKeyword.stream().filter(k -> {
            return txt.contains(k);
        }).findFirst();
        return first.get();

    }

    @Data
    class DateAndIndex {
        private String date;
        private int index;

        private boolean isDelete;

        public DateAndIndex(String date, int index) {
            this.date = date;
            this.index = index;
        }

    }

}