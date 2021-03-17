package com.emdata.messagewarningscore.common.warning.entity;/**
 * Created by zhangshaohu on 2020/11/27.
 */

import cn.hutool.core.date.DateUtil;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author: zhangshaohu
 * @date: 2020/11/27
 * @description:
 */
public class WarningDate {
    public static void main(String[] args) {
        String[] ss = {"能见度", "RVR", "垂直能见度"};
        String text = "【终端区天气预警】受冷槽影响，苏皖北部有带状对流云团，云顶高度8-9千米，以40公里/小时速度向东南方向移动，强度维持。预计上海终端区19日02:00-10:00分散阵雨或雷雨；虹桥及浦东机场19日04:00-09:00小到中阵雨伴CB。\n" +
                "11月18日20:03\n";
        WarningDate warningDate = new WarningDate(text);
        ChildWarningDate childDate = warningDate.getChildDate("预计北京时间22日17:00至24日18:30影响机场");
        int a = 0;

    }

    /**
     * 主时间
     */
    private Date mainDate;

    public Date getMainDate() {
        return mainDate;
    }

    /**
     * 默认主正则
     */
    private List<String> mainPattern = new ArrayList<String>() {{
        add("(发布时间)[^\\d]+\\d{4}-\\d{2}-\\d{2}\\s(\\d{2}):\\d{2}");
    }};
    //11月18日20:03
    private Map<String, String> mainMap = new HashMap<String, String>() {{
        put("(发布时间)[^\\d]+\\d{4}-\\d{2}-\\d{2}\\s(\\d{2}):\\d{2}", "发布时间：yyyy-MM-dd HH:mm");
        put("\\d{2}月\\d{2}日\\d{2}:\\d{2}", "MM月dd日HH:mm");
    }};
    private List<String> startTimePattern = new ArrayList<String>() {{
        add("(\\d+)[^\\d](\\d+)[^\\d](\\d+)");
    }};
    private List<String> endTimePattern = new ArrayList<String>() {{
        add("(\\d+)[^\\d](\\d+)[^\\d](\\d+)");
        add("(\\d+)[^\\d](\\d+)");
    }};

    /**
     * 构造器
     *
     * @param message
     */
    public WarningDate(String message) {
        String s = MatchUtil.get(message, mainPattern);
        if (StringUtils.isEmpty(s)) {
            // 从数据库查询正则规则
        }
        this.mainDate = DateUtil.parse(s, "发布时间：yyyy-MM-dd HH:mm");
    }

    /**
     * @param message
     * @param thisDate
     */
    public WarningDate(String message, Date thisDate) {
        for (Map.Entry<String, String> mainEntry : mainMap.entrySet()) {
            String patt = mainEntry.getKey();
            String value = mainEntry.getValue();
            String s = MatchUtil.get(message, patt);
            if (!StringUtils.isEmpty(s)) {
                if (value.contains("yyyy")) {
                    this.mainDate = DateUtil.parse(s, "发布时间：yyyy-MM-dd HH:mm");
                }
                this.mainDate = getTime(thisDate, DateUtil.parse(s, value));
                return;
            }

        }

    }

    /**
     * 从一段话中获取精确时间 并且返回
     *
     * @param message
     * @return
     */
    public ChildWarningDate getChildDate(String message) {
        return getChildDate(message, this.startTimePattern, this.endTimePattern);
    }

    /**
     * 从一段话中获取精确时间 并且返回
     *
     * @param message
     * @return
     */
    public ChildWarningDate getChildDate(String message, List<String> startTimePattern, List<String> endTimePattern) {
        String start = getStartTime(startTimePattern, message);
        String end = getEndTime(start, endTimePattern, message);
        ChildWarningDate childWarningDate = new ChildWarningDate();
        Date startDate = getTime(start, mainDate);
        Calendar instance = Calendar.getInstance();
        instance.setTime(startDate);
        Date endDate = getTime(end, mainDate, instance.get(Calendar.DAY_OF_MONTH) + "");
        if (startDate.getTime() < endDate.getTime()) {
            childWarningDate.setStartDate(startDate);
            childWarningDate.setEndDate(endDate);
        } else {
            childWarningDate.setStartDate(endDate);
            childWarningDate.setEndDate(startDate);
        }
        childWarningDate.setStart(start);
        childWarningDate.setEnd(end);
        return childWarningDate;
    }

    /**
     * 获取开始时间
     *
     * @param startTimePattern
     * @param message
     * @return
     */
    private String getStartTime(List<String> startTimePattern, String message) {
        return MatchUtil.get(message, startTimePattern);
    }

    /**
     * 获取结束时间
     *
     * @param start
     * @param endTimePattern
     * @param message
     * @return
     */
    private String getEndTime(String start, List<String> endTimePattern, String message) {
        String replace = message.replace(start, "");
        return MatchUtil.get(replace, endTimePattern);
    }

    private Date getTime(String message, Date thisDate) {
        String pa = "([^\\d])+";
        Pattern compile = Pattern.compile(pa);
        String[] split = compile.split(message);
        String day = split[0];
        String hour = split[1];
        String min = split[2];
        Date time = getTime(thisDate, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));
        return time;
    }

    private Date getTime(String message, Date thisDate, String day) {
        String pa = "([^\\d])+";
        Pattern compile = Pattern.compile(pa);
        String[] split = compile.split(message);
        if (split.length < 3) {
            String hour = split[0];
            String min = split[1];
            Date time = getTime(thisDate, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));
            return time;
        } else {
            day = split[0];
            String hour = split[1];
            String min = split[2];
            Date time = getTime(thisDate, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));
            return time;
        }
    }

    /**
     * 只有月 日 小时
     *
     * @param thisDate 当前时间
     * @return
     */
    private Date getTime(Date thisDate, Date splitDate) {
        List<Date> dates = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        instance.setTime(thisDate);
        int thisYear = instance.get(Calendar.YEAR);
        instance.setTime(splitDate);
        instance.set(Calendar.YEAR, thisYear);
        dates.add(instance.getTime());
        instance.set(Calendar.YEAR, thisYear + 1);
        dates.add(instance.getTime());
        instance.set(Calendar.YEAR, thisYear - 1);
        dates.add(instance.getTime());
        Date date = dates.stream().reduce((s1, s2) -> {
            if (Math.abs(s1.getTime() - thisDate.getTime()) < Math.abs(s2.getTime() - thisDate.getTime())) {
                return s1;
            } else {
                return s2;
            }
        }).get();
        return date;
    }

    private Date getTime(Date thisDate, Integer day, Integer hour, Integer min) {
        List<Date> dates = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        instance.setTime(thisDate);

        instance.set(Calendar.DAY_OF_MONTH, day);
        instance.set(Calendar.HOUR_OF_DAY, hour);
        instance.set(Calendar.MINUTE, min);
        Date thisMonthTime = instance.getTime();
        /**
         * 加一月
         */
        instance.add(Calendar.MONTH, 1);
        instance.set(Calendar.DAY_OF_MONTH, day);
        instance.set(Calendar.HOUR_OF_DAY, hour);
        instance.set(Calendar.MINUTE, min);
        Date nextMonthTime = instance.getTime();
        instance.setTime(thisDate);
        instance.add(Calendar.MONTH, -1);
        instance.set(Calendar.DAY_OF_MONTH, day);
        instance.set(Calendar.HOUR_OF_DAY, hour);
        instance.set(Calendar.MINUTE, min);
        Date upMonthTime = instance.getTime();
        dates.add(thisMonthTime);
        dates.add(nextMonthTime);
        dates.add(upMonthTime);
        Date date = dates.stream().reduce((s1, s2) -> {
            if (Math.abs(s1.getTime() - thisDate.getTime()) < Math.abs(s2.getTime() - thisDate.getTime())) {
                return s1;
            } else {
                return s2;
            }
        }).get();
        return date;
    }

    @Data
    public class ChildWarningDate {
        /**
         * 开始时间
         */
        private Date startDate;
        /**
         * 结束时间
         */
        private Date endDate;
        /**
         * 报文
         */
        private String start;
        /**
         *
         */
        private String end;
    }

}