package com.emdata.messagewarningscore.data.util;

import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.data.parse.bo.AreaAndTimeBO;

import java.util.*;

/**
 * @description: 根据警报文本里发布时间和出现时段字符串解析预报的开始时间和结束时间
 * @date: 2020/12/9
 * @author: sunming
 */
public class ParseTextTimeUtil {


    // 26日02:00-07:00
    private static String timePatternOne = "\\d{2}日\\d{2}:\\d{2}-\\d{2}:\\d{2}";

    //6日02:00-07:00
    private static String timePatternOneTwo = "\\d{1}日\\d{2}:\\d{2}-\\d{2}:\\d{2}";

    // 25日22:00-26日04:00
    private static String timePatternTwo = "\\d{2}日\\d{2}:\\d{2}-\\d{2}日\\d{2}:\\d{2}";
    // 5日22:00-6日04:00
    private static String timePatternTwoTwo = "\\d{1}日\\d{2}:\\d{2}-\\d{1}日\\d{2}:\\d{2}";

    // 25日18-21时
    private static String timePatternThree = "\\d{2}日\\d{2}-\\d{2}时";
    // 25日18时-21时
    private static String timePatternThreeThree = "\\d{2}日\\d{2}时-\\d{2}时";
    // 21日17时-22日05时
    private static String timePatternThreeFour = "\\d{2}日\\d{2}时-\\d{2}日\\d{2}时";
    // 15日14-19
    private static String timePatternThreeTwo = "\\d{2}日\\d{2}-\\d{2}";

    // 目前-18:00
    private static String timePatternFour = "目前-\\d{2}:\\d{2}";
    // 目前-12日xx:00
    private static String timePatternFive = "目前-\\d{2}日\\d{2}:\\d{2}";

    private static String timePatternSix = "\\d{2}日\\d{2}时-\\d{2}时，\\d{2}时-\\d{2}时";

    /**
     * 获取发布时间
     *
     * @param releaseTime
     * @return
     */
    public static Date obtainReleaseTime(String releaseTime) {
        String releaseTimeRep = releaseTime.replace("年", "-").replace("月", "-")
                .replace("日", " ").replace("时", ":").replace("分", ":00");
        Date releaseDateTime = Java8DateUtils.parseDateTimeStr(releaseTimeRep, Java8DateUtils.DATE_TIME);
        return releaseDateTime;
    }

    /**
     * 通过发布时间获取到开始时间和结束时间
     *
     * @param releaseTime 发布时间 yyyy-MM-dd HH:mm:ss
     * @param existTime   出现时段
     */
    public static List<AreaAndTimeBO.StartAndEndTimeBO> obtainTime(String releaseTime, String existTime) {
        // 将中文符号替换为英文
        existTime = existTime.replace("：", ":").replace("--", "-");
        List<String> existTimes = new ArrayList<>();
        if (existTime.matches(timePatternSix)) {
            String[] split = existTime.split("，");
            existTimes.add(split[0]);
            String t = split[0].substring(0, 3) + split[1];
            existTimes.add(t);
        } else {
            String[] split = existTime.split("</br>");
            existTimes.addAll(Arrays.asList(split));
        }
        List<AreaAndTimeBO.StartAndEndTimeBO> timeBOList = new ArrayList<>();
        for (String time : existTimes) {
            AreaAndTimeBO.StartAndEndTimeBO timeBO = new AreaAndTimeBO.StartAndEndTimeBO();
            Date releaseDateTime = obtainReleaseTime(releaseTime);
            timeBO.setReleaseTime(releaseDateTime);
            Calendar c = Calendar.getInstance();
            c.setTime(releaseDateTime);
            if (time.matches(timePatternTwo) || time.matches(timePatternTwoTwo) || time.matches(timePatternThreeFour)) {
                String[] split = time.split("-");
                String startStr = split[0];
                String endStr = split[1];
                timeBO.setStartTime(setDayAndHourDate(startStr, c));
                timeBO.setEndTime(setDayAndHourDate(endStr, c));

            } else if (time.matches(timePatternThree) || time.matches(timePatternThreeThree) || time.matches(timePatternThreeTwo)
                    || time.matches(timePatternOne) || time.matches(timePatternOneTwo)) {
                String day = time.substring(0, time.indexOf("日"));
                String startHour = time.substring(time.indexOf("日") + 1, time.indexOf("日") + 3);
                String endHour = time.substring(time.indexOf("-") + 1, time.indexOf("-") + 3);
                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startHour));
                c.set(Calendar.MINUTE, 0);
                timeBO.setStartTime(c.getTime());
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHour));
                timeBO.setEndTime(c.getTime());
            } else if (time.matches(timePatternFour)) {// 目前-18:00
                String endHour = time.substring(time.indexOf("-") + 1, time.indexOf("-") + 3);
                timeBO.setStartTime(releaseDateTime);
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHour));
                c.set(Calendar.MINUTE, 0);
                timeBO.setEndTime(c.getTime());
            } else if (time.matches(timePatternFive)) {
                String endDay = time.substring(time.indexOf("-") + 1, time.indexOf("-") + 3);
                String endHour = time.substring(time.indexOf("日") + 1, time.indexOf("日") + 3);
                timeBO.setStartTime(releaseDateTime);
                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDay));
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHour));
                c.set(Calendar.MINUTE, 0);
                timeBO.setEndTime(c.getTime());
            }
            timeBOList.add(timeBO);
        }


        return timeBOList;
    }

    /**
     * 获取date类型的时间
     *
     * @param time 26日22:00
     * @return
     */
    public static Date setDayAndHourDate(String time, Calendar c) {
        String day = time.substring(0, time.indexOf("日"));
        String hour = time.substring(time.indexOf("日") + 1, time.indexOf("日") + 3);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        c.set(Calendar.MINUTE, 0);
        return c.getTime();
    }

}
