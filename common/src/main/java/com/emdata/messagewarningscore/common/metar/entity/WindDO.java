package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2020/10/9.
 */

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/10/9
 * @description:
 */
@Data
@Slf4j
public class WindDO extends Index implements Cloneable {
    /**
     * ================================================测试main方法=================================================================
     */
    public static void main(String[] args) {
        WindDO windDO = new WindDO(200, 232);
        System.out.println(windDO);
    }

    @Override
    public WindDO clone() {
        try {
            return (WindDO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ==============================================报文风组用到的公共属性=====================================================================
     */
    public static final String WINDPATTERN = "(\\s)(((([0-2][0-9][0-9])|(3[0-5][0-9])|(360))|VRB)((P49)|([0-4][0-9])|(([0-4][0-9])G([0-4][0-9]))|(P49(G([0-4][0-9])))|(P49GP49))(MPS)(\\s)(([0-2][0-9][0-9])|(3[0-5][0-9])|(360))(V)(([0-2][0-9][0-9])|(3[0-5][0-9])|(360)))|(((([0-2][0-9][0-9])|(3[0-5][0-9])|(360))|VRB)(P49|([0-4][0-9])|(([0-4][0-9])G([0-4][0-9]))|(P(49G([0-4][0-9])))|(P49GP49))(MPS))";
    /**
     * 基本风是否大于49米
     */
    public static final String WIND_IS_GT49 = "\\d{3}P49";
    /**
     * 阵风是否大于49米
     */
    public static final String WIND_GUST_GT49 = "GP49";
    /**
     * 阵风正则
     */
    public static final String WIND_GUST_WIND = "(G\\d{2})|(GP49)";
    /**
     * 风组格式错误
     */
    public static final Integer WIND_ERROR_CODE = -1;

    /**
     * 不定风向唯一标识映射
     */
    private static final Integer VRB_IDENTIFICATION = -1;
    /**
     * 不定风向唯一标识
     */
    private static final String VRB = "VRB";
    /**
     * 风组结束标识
     */
    private static final String WIND_IDENTIFICATION = "MPS";
    /**
     * 风向区间唯一标识
     */
    private static final String REGION_END_IDENTIFICATION = "V";
    /**
     * 阵风标识
     */
    private static final String WIND_GUST = "G";
    /**
     * 风速大于49唯一标识
     */
    private static final String WIND_GT49_IDENTIFICATION = "P";
    /**
     * ===============================================风组属性========================================================================
     */
    private final Integer start = 4;
    private final Integer end = 5;
    /**
     * 风组字符串
     */
    public String windString;
    /**
     * 风向区间字符串
     */
    public String windRegionString;
    /**
     * 风向
     */
    private Integer windDirection;
    /**
     * 风速
     */
    private Integer windSpeed;
    /**
     * 风向区间开始
     */
    private Integer startWindDirection;
    /**
     * 风向区间结束
     */
    private Integer endWindDirection;
    /**
     * 阵风风速
     */
    private Integer gustWindSpeed = 0;
    /**
     * 是否是大于49米的风
     */
    private Boolean windSpeedGt49 = false;
    /**
     * 阵风是否是大于49米的风
     */
    private boolean gustWindSpeedGt49 = false;
    /**
     *==================================================风组构造器=====================================================================
     *
     */

    /**
     * 无参构造器
     */
    public WindDO() {
    }

    public WindDO(Integer windSpeed, Integer windDirection) {
        this.windSpeed = windSpeed < 3 ? 3 : windSpeed;
        if (windSpeed > 49) {
            this.windSpeedGt49 = true;
            this.windSpeed = 49;
        }
        this.windDirection = windDirection;
        this.startIndex = start;
        this.endIndex = end;
    }

    /**
     * 构造器
     *
     * @param wind       风组
     * @param windRegion 风向区间
     */
    public WindDO(String wind, String windRegion) {
        wind(wind, windRegion);
    }

    /**
     * 构造器
     * 风组的组成为 风向风速G阵风以及
     *
     * @param message 报文
     */
    public WindDO(String message) {
        message = addSpace(message);
        String windGroup = matchAll(message, WINDPATTERN, new StringBuilder());
        if (StringUtils.isNotEmpty(windGroup)) {
            String[] split = windGroup.split(" ");
            if (split.length == 2) {
                wind(split[0], split[1]);
            } else {
                wind(split[0]);
            }
            indexActual(message, windGroup);
        }
    }


/**
 *
 *===========================================根据报文内容返回风组对象方法=============================================================================
 */

    /**
     * 构建一个风组对象
     *
     * @param message 保证当前报文有且只有一个风组 当出现多个风组 取第一个
     * @return
     */
    public static WindDO buildWindDO(String message) {
        WindDO windDO = buildWindDO(message, WINDPATTERN);
        return windDO;
    }

    /**
     * 构建一个风组对象
     *
     * @param message
     * @return
     */
    public static WindDO buildWindDO(String message, String windPattern) {
        String windGroup = getWindGroup(message, 0, windPattern);
        if (StringUtils.isNotEmpty(windGroup)) {
            String[] split = windGroup.split(" ");
            if (split.length == 2) {
                WindDO windDO = null;
                windDO = new WindDO(split[0], split[1]);
                return windDO;
            } else {
                WindDO windDO = null;
                windDO = new WindDO(split[0]);
                return windDO;
            }
        } else {
            return null;
        }

    }
    /**
     *==============================================风组私有方法========================================================================
     *
     */

    /**
     * 得到风组
     *
     * @param message
     * @param index
     * @return
     */
    private static String getWindGroup(String message, Integer index, String windPattern) {
        String windGroup = "";
        Pattern p = Pattern.compile(windPattern);
        for (int i = index; i >= 0; i--) {
            Matcher m = p.matcher(message);
            if (m.find()) {
                windGroup = m.group();
                message = message.replace(windGroup, "");
            }
        }
        return windGroup.trim();
    }


    /**
     * @param wind
     * @param windRegion
     */
    private void wind(String wind, String windRegion) {
        wind(wind);
        this.startWindDirection = calculateStartWindRegion(windRegion);
        this.endWindDirection = calculateEndWindRegion(windRegion);
        this.windRegionString = windRegion;
        this.startIndex = start;
        this.endIndex = end;
    }

    /**
     * @param wind 风组
     */
    private void wind(String wind) {
        this.windSpeed = calculateWindSpeed(wind);
        this.windDirection = calculateWindDiretion(wind);
        this.gustWindSpeed = calculateGustWind(wind);
        this.gustWindSpeedGt49 = calculateGustIsGt49(wind);
        this.windSpeedGt49 = calculateGt49(wind);
        this.windString = wind;
        this.startIndex = start;
        this.endIndex = end;

    }
    /**
     *==================================================报文风组公共方法======================================================================
     *
     */

    /**
     * 返回风向
     *
     * @param windString
     * @return
     */
    public static Integer calculateWindDiretion(String windString) {
        if (windString.startsWith(VRB)) {
            return VRB_IDENTIFICATION;
        }
        return Integer.parseInt(windString.substring(0, 3));
    }


    /**
     * 返回风速
     *
     * @param windString
     * @return
     */
    public static Integer calculateWindSpeed(String windString) {
        if (calculateGt49(windString)) {
            return 50;
        }
        return Integer.parseInt(windString.substring(3, 5));
    }

    /**
     * 返回风向区间开始风向
     *
     * @param windRegionString
     * @return
     */
    public static Integer calculateStartWindRegion(String windRegionString) {
        List<Integer> collect = Arrays.stream(windRegionString.split(REGION_END_IDENTIFICATION)).map(s -> {
            return Integer.parseInt(s);
        }).collect(Collectors.toList());
        return collect.get(0);
    }

    /**
     * 返回风向区间结束风向
     *
     * @param windRegionString
     * @return
     */
    public static Integer calculateEndWindRegion(String windRegionString) {
        List<Integer> collect = Arrays.stream(windRegionString.split(REGION_END_IDENTIFICATION)).map(s -> {
            return Integer.parseInt(s);
        }).collect(Collectors.toList());
        return collect.get(1);
    }

    /**
     * 返回阵风
     * 没有阵风默认为0
     *
     * @param windString
     * @return
     */
    public static Integer calculateGustWind(String windString) {
        Pattern compile = Pattern.compile(WIND_GUST_WIND);
        Matcher matcher = compile.matcher(windString);
        if (matcher.find()) {
            if (calculateGustIsGt49(windString)) {
                return 49;
            }
            String group = matcher.group();
            return Integer.parseInt(group.replace(WIND_GUST, ""));
        }
        return 0;
    }

    /**
     * 计算是否是有阵风
     *
     * @param windString
     * @return
     */
    public static boolean calculateIsGust(String windString) {
        Pattern compile = Pattern.compile(WIND_GUST_WIND);
        Matcher matcher = compile.matcher(windString);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断阵风风速是否大于49米
     *
     * @return
     */
    public static boolean calculateGustIsGt49(String windString) {
        Pattern compile = Pattern.compile(WIND_GUST_GT49);
        Matcher matcher = compile.matcher(windString);
        return matcher.find();
    }

    /**
     * 风速是否大于49米
     *
     * @param windString
     * @return
     */
    public static boolean calculateGt49(String windString) {
        Pattern compile = Pattern.compile(WIND_IS_GT49);
        Matcher matcher = compile.matcher(windString);
        return matcher.find();
    }


    /**
     * 风组与风向区间
     *
     * @return
     */
    public String toWindAndWindRegionString() {
        StringBuilder wind = new StringBuilder();
        wind.append(toString());
        if (isWindDrectionRegion()) {
            wind.append(" ");
            String startWindDirection = String.format("%3d", this.startWindDirection).replace(" ", "0");
            String endWindDirection = String.format("%3d", this.endWindDirection).replace(" ", "0");
            wind.append(startWindDirection);
            wind.append(REGION_END_IDENTIFICATION);
            wind.append(endWindDirection);
        }

        return wind.toString();
    }

    /**
     * 是否存在阵风
     *
     * @return
     */
    public boolean isGustWindSpeed() {
        return this.gustWindSpeed != null && this.gustWindSpeed != 0;
    }

    /**
     * 判断是否存在风向区间
     *
     * @return
     */
    public boolean isWindDrectionRegion() {
        return this.startWindDirection != null && this.endWindDirection != null;
    }

    /**
     * 风组
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder wind = new StringBuilder();
        // 风向
        if (this.windDirection.equals(VRB_IDENTIFICATION)) {
            wind.append(VRB);
        } else {
            String windDirection = String.format("%03d", this.windDirection);
            // 北风为360，静风为000
            if (this.windDirection.equals(0) && !this.windSpeed.equals(0)) {
                windDirection = "360";
            }
            wind.append(windDirection);
        }
        if (this.windSpeedGt49) {
            wind.append(WIND_GT49_IDENTIFICATION);
        }
        String windSpeed = String.format("%02d", this.windSpeed);
        if (this.windDirection != -1) {
            windSpeed = String.format("%02d", this.windSpeed < 3 ? 3 : this.windSpeed);
        }

        wind.append(windSpeed);
        if (isGustWindSpeed()) {
            if (this.gustWindSpeedGt49) {
                wind.append(WIND_GT49_IDENTIFICATION);
            }
            String gustWindSpeed = String.format("%02d", this.gustWindSpeed);
            wind.append(WIND_GUST).append(gustWindSpeed);
        }
        wind.append(WIND_IDENTIFICATION);

        return wind.toString();
    }
}