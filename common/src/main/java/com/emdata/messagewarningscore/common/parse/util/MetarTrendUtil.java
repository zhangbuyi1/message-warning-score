package com.emdata.messagewarningscore.common.parse.util;

import com.emdata.messagewarningscore.common.exception.MetarParseException;
import com.emdata.messagewarningscore.common.parse.bo.ChangeGroup;
import com.emdata.messagewarningscore.common.parse.bo.TrendTypeCodeEnum;
import com.emdata.messagewarningscore.common.parse.bo.TrendTypeTimeEnum;
import com.emdata.messagewarningscore.common.parse.element.CloudGroup;
import com.emdata.messagewarningscore.common.parse.element.ElementContainer;
import com.emdata.messagewarningscore.common.parse.element.Weather;
import com.emdata.messagewarningscore.common.parse.element.WindGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * METAR趋势报部分工具类
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/14 15:59
 */
@Slf4j
public class MetarTrendUtil {

    private static final String NO_SIGN = "NOSIG";

    /**
     * 时间指示码格式
     */
    private static final Pattern TIME_ENUM_REGEX = Pattern.compile("^(FM|TL|AT)(\\d{4})$");

    /**
     * 解析报文能见度
     *
     * @param eles  报文要素数组
     * @param index 能见度开始索引
     * @return 能见度，CAVOK=9999
     */
    public static ElementContainer<List<ChangeGroup>> parse(String[] eles, int index, Date startTime) throws MetarParseException {
        return parse(eles, index, startTime, true);
    }


    public static ElementContainer<List<ChangeGroup>> parse(String[] eles, int index, Date startTime, boolean check) {
        if (eles.length == index) {
            return new ElementContainer<>(null, index);
        }

        if (!checkIndex(eles, index, check)) {
            return new ElementContainer<>(null, index);
        }

        List<ChangeGroup> changeGroups = new ArrayList<>();

        String str = eles[index];
        if (NO_SIGN.equals(str)) {
            index++;
            return new ElementContainer<>(changeGroups, index);
        }

        do {
            // 读取变化组
            ElementContainer<ChangeGroup> trendCroupContainer = parseOne(eles, index, startTime, check);
            System.out.println(trendCroupContainer.get());
            if (trendCroupContainer.get() == null) {
                continue;
            }
            changeGroups.add(trendCroupContainer.get());

            index = trendCroupContainer.getIndex();
        } while (index < eles.length);

        return new ElementContainer<>(changeGroups, index);
    }

    private static ElementContainer<ChangeGroup> parseOne(String[] eles, int index, Date startTime, boolean check) throws MetarParseException {
        if (!checkIndex(eles, index, check)) {
            return new ElementContainer<>(null, index);
        }
        String str = eles[index];

        ChangeGroup m = new ChangeGroup();

        TrendTypeCodeEnum trendTypeCode;
        try {
            trendTypeCode = TrendTypeCodeEnum.valueOf(str);
            index++;
        } catch (IllegalArgumentException e) {
            throw new MetarParseException("METAR格式错误：" + str);
        }

        m.setTrendTypeCode(trendTypeCode);

        // 设置时间
        ElementContainer<Integer> timeContainer = setTime(eles, index, startTime, m, check);
        index = timeContainer.getIndex();

        // 判断分组
        ElementContainer<WindGroup> windContainer = WindGroupUtil.parse(eles, index, false);
        WindGroup windGroup = windContainer.get();

        index = windContainer.getIndex();

        // 判断能见度
        ElementContainer<Double> visContainer = VisibilityUtil.parse(eles, index, false);
        Double vis = visContainer.get();

        index = visContainer.getIndex();

        // 判断天气现象
        ElementContainer<List<Weather>> weatherContainer = WeatherUtil.parse(eles, index, false);
        List<Weather> weathers = weatherContainer.get();

        index = weatherContainer.getIndex();

        // 判断云组
        ElementContainer<List<CloudGroup>> cloudContainer = CloudGroupUtil.parse(eles, index, false);
        List<CloudGroup> cloudGroups = cloudContainer.get();

        index = cloudContainer.getIndex();

        // 判断，不能一个变化组都没有
        if (windGroup == null && vis == null
                && CollectionUtils.isEmpty(weathers) && CollectionUtils.isEmpty(cloudGroups)) {
            throw new MetarParseException("Metar报文变化组错误: " + eles[index]);
        }

        // 设置
        m.setWindGroup(windGroup);
        m.setVisibility(vis);
        m.setWeathers(weathers);
        m.setCloudGroups(cloudGroups);

        return new ElementContainer<>(m, index);
    }

    private static ElementContainer<Integer> setTime(String[] eles, int index, Date startTime, ChangeGroup m, boolean check) {
        if (!checkIndex(eles, index, check)) {
            return new ElementContainer<>(null, index);
        }

        // 时间组
        Date start = startTime;
        // 结束默认2小时后
        Date end = new Date(start.getTime() + 2 * 60 * 60 * 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        String str = eles[index];

        String str2 = "";
        // 获取第二个，有可能FM TL都有
        if (index + 1 < eles.length) {
            str2 = eles[index + 1];
        }

        TrendTypeTimeEnum timeEnum = TrendTypeTimeEnum.NO;

        // 时间
        Matcher matcher = TIME_ENUM_REGEX.matcher(str);
        // 如果符合改格式，则与FM/TL/AT，否则，开始结束数据就是有效时间段
        if (matcher.matches()) {
            index++;

            String MMhh = matcher.group(2);

            if (str.startsWith("FM")) {
                // 开始时间
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(MMhh.substring(0, 2)));
                calendar.set(Calendar.MINUTE, Integer.parseInt(MMhh.substring(2)));
                start = calendar.getTime();

                timeEnum = TrendTypeTimeEnum.FM;

                matcher = TIME_ENUM_REGEX.matcher(str2);
                // 结束时间
                if (str2.startsWith("TL") && matcher.matches()) {
                    index++;

                    MMhh = matcher.group(2);

                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(MMhh.substring(0, 2)));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(MMhh.substring(2)));
                    end = calendar.getTime();
                    timeEnum = TrendTypeTimeEnum.FM_TL;

                }
            } else if (str.startsWith("TL")) {
                // 结束时间
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(MMhh.substring(0, 2)));
                calendar.set(Calendar.MINUTE, Integer.parseInt(MMhh.substring(2)));
                end = calendar.getTime();

                timeEnum = TrendTypeTimeEnum.TL;
            } else if (str.startsWith("AT")) {
                // 都在这一刻
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(MMhh.substring(0, 2)));
                calendar.set(Calendar.MINUTE, Integer.parseInt(MMhh.substring(2)));
                start = end = calendar.getTime();

                timeEnum = TrendTypeTimeEnum.AT;
            }
        }


        // 设置
        m.setTrendTypeTime(timeEnum);
        m.setChangeStartTime(start);
        m.setChangeEndTime(end);

        return new ElementContainer<>(index, index);
    }

    /**
     * 检查所有，如果无需检查，且超过索引，返回true
     *
     * @param eles
     * @param index
     * @param check
     * @return
     */
    private static boolean checkIndex(String[] eles, int index, boolean check) {
        if (index >= eles.length) {
            if (check) {
                throw new MetarParseException("请检查METAR报文（要素不足）");
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}


















