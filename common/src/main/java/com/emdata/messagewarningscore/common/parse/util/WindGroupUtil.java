package com.emdata.messagewarningscore.common.parse.util;

import com.emdata.messagewarningscore.common.exception.MetarParseException;
import com.emdata.messagewarningscore.common.parse.element.ElementContainer;
import com.emdata.messagewarningscore.common.parse.element.WindGroup;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 风组工具类
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/10 19:39
 */
public class WindGroupUtil {

    /**
     * 风组是不定风向的匹配
     */
    private static final Pattern VRB_PATTERN = Pattern.compile("^(VRB)(\\d{2}|P\\d{2})(MPS)$");

    /**
     * 风组是不定风向的匹配，阵风
     */
    private static final Pattern VRB_GUST_PATTERN = Pattern.compile("^(VRB)(\\d{2}|P\\d{2})(G\\d{2}|GP\\d{2})(MPS)$");

    /**
     * 常用风组的匹配
     */
    private static final Pattern COMMON_PATTERN = Pattern.compile("^(\\d{3})(\\d{2}|P\\d{2})(MPS)$");

    /**
     * 风组有阵风的匹配
     */
    private static final Pattern GUST_PATTERN = Pattern.compile("^(\\d{3})(\\d{2}|P\\d{2})(G\\d{2}|GP\\d{2})(MPS)$");

    /**
     * 风向区间的字符串匹配
     */
    private static final Pattern INTERVAL = Pattern.compile("^(\\d{3})(V)(\\d{3})$");


    public static void main(String[] args) {
        String[] strs = new String[]{"VRB03MPS 120V090", "10002GP49MPS 120V090", "aaa"};
        for (String str : strs) {
            ElementContainer<WindGroup> parse = parse(str.split(" "), 0, false);
            System.out.println(parse.get());
            System.out.println();
        }
        System.out.println(formatWindGroup("000GP4903MPS"));
    }

    /**
     * 解析报文，获取风组
     *
     * @param eles       报文元素数组
     * @param startIndex 风组开始索引
     * @return 风组信息
     */
    public static ElementContainer<WindGroup> parse(String[] eles, int startIndex) throws MetarParseException {
        return parse(eles, startIndex, true);
    }

    /**
     * 解析报文，获取风组
     *
     * @param eles  报文元素数组
     * @param index 风组开始索引
     * @param check 是否进行格式检查，true：检查不通过时，抛出异常，false：检查不通过时返回空
     * @return 风组信息
     */
    public static ElementContainer<WindGroup> parse(String[] eles, int index, boolean check) throws MetarParseException {
        if (index >= eles.length) {
            if (!check) {
                return new ElementContainer<>(null, index);
            }
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }

        // 风组开始
        String windStr = eles[index];

        WindGroup windGroup = new WindGroup();

        Matcher vrb = VRB_PATTERN.matcher(windStr);
        Matcher vrbGust = VRB_GUST_PATTERN.matcher(windStr);
        Matcher common = COMMON_PATTERN.matcher(windStr);
        Matcher gust = GUST_PATTERN.matcher(windStr);

        // 如果是VRB开头，不定风向
        if (vrb.matches()) {
            // 风速为第二个子串
            windGroup.setWindSpeed(parseSpeed(vrb.group(2)));
        } else if (vrbGust.matches()) {
            // 风速为第二个子串
            windGroup.setWindSpeed(parseSpeed(vrbGust.group(2)));
            // 阵风
            windGroup.setGustSpeed(parseSpeed(vrbGust.group(3)));
        } else if (common.matches()) {
            // 风向
            windGroup.setWindDirection(Double.valueOf(common.group(1)));
            // 风速为第二个子串
            windGroup.setWindSpeed(parseSpeed(common.group(2)));
        } else if (gust.matches()) {
            // 风向
            windGroup.setWindDirection(Double.valueOf(gust.group(1)));
            // 风速为第二个子串
            windGroup.setWindSpeed(parseSpeed(gust.group(2)));
            // 阵风
            windGroup.setGustSpeed(parseSpeed(gust.group(3)));
        } else if (check) {
            throw new MetarParseException("METAR风组格式错误：" + windStr);
        } else {
            return new ElementContainer<>(null, index);
        }

        index++;
        // 读取下一个，看是否满足风向区间的字符串
        if (index < eles.length) {
            String next = eles[index];
            Matcher matcher = INTERVAL.matcher(next);
            if (matcher.matches()) {
                // 指针向后移动
                index++;

                // 设置风向区间
                windGroup.setWindDirectionStart(Double.valueOf(matcher.group(1)));
                windGroup.setWindDirectionEnd(Double.valueOf(matcher.group(3)));
            }
        }

        return new ElementContainer<WindGroup>(windGroup, index);
    }


    private static Double parseSpeed(String speedStr) {
        if (speedStr.startsWith("G")) {
            speedStr = speedStr.substring(1);
        }
        if (speedStr.startsWith("P")) {
            speedStr = speedStr.substring(1);
            // 当预报风速大于等于50米/秒时，“ff”组前加指示码P，编报P49MPS，返回50
            double d = Double.parseDouble(speedStr);
            if (d < 49) {
                throw new MetarParseException("风速小于49时，前边不能加\"P\"");
            }
            return d + 1;

        }
        return Double.valueOf(speedStr);
    }


    /**
     * 拼接风组
     *
     * @param windGroup 风组实体类
     * @return 风组字符串
     */
    public static String combineWindGroup(WindGroup windGroup) {
        String windGroupStr = "";
        String windDirSpe = combineWindDirSpe(windGroup);
        String windSession = combineWindSession(windGroup);
        if (StringUtils.isNotBlank(windDirSpe)) {
            windGroupStr = windGroupStr + windDirSpe;
        }
        if (StringUtils.isNotBlank(windSession)) {
            windGroupStr = windGroupStr + " " + windSession;
        }
        return windGroupStr.trim();
    }

    /**
     * 拼接风组
     *
     * @param windGroup 风组实体类
     * @return 风组字符串
     */
    public static String combineWindDirSpe(WindGroup windGroup) {
        Double windDirection = windGroup.getWindDirection();
        Double windSpeed = windGroup.getWindSpeed();
        Double gustSpeed = windGroup.getGustSpeed();
        StringBuilder windDirectionSpeed = new StringBuilder();
        if (windDirection == null) {
            windDirectionSpeed.append("VRB");
        } else {
            DecimalFormat format = new DecimalFormat("000");
            windDirectionSpeed.append(format.format(windDirection));
        }
        if (windSpeed == null) {
            windDirectionSpeed.append("00");
        } else {
            DecimalFormat format = new DecimalFormat("00");
            if (windSpeed > 49) {
                windDirectionSpeed.append("P49");
            } else {
                windDirectionSpeed.append(format.format(windSpeed));
            }
        }
        // 阵风
        if (gustSpeed != null) {
            DecimalFormat format = new DecimalFormat("00");
            windDirectionSpeed.append("G");
            if (gustSpeed > 49) {
                windDirectionSpeed.append("P49");
            } else {
                windDirectionSpeed.append(format.format(gustSpeed));
            }
        }
        String windGroupStr = windDirectionSpeed.toString().trim() + "MPS";
        return windGroupStr;
    }

    /**
     * 拼接风向区间
     *
     * @param windGroup 风组实体类
     * @return 风向区间字符串
     */
    public static String combineWindSession(WindGroup windGroup) {
        String windSession = "";
        Double windDirectionStart = windGroup.getWindDirectionStart();
        Double windDirectionEnd = windGroup.getWindDirectionEnd();
        StringBuilder windSessionBuilder = new StringBuilder();
        DecimalFormat format = new DecimalFormat("000");
        if (windDirectionStart != null && windDirectionEnd != null) {
            windSessionBuilder.append(format.format(windDirectionStart)).append("V")
                    .append(format.format(windDirectionEnd));
        }
        windSession = windSessionBuilder.toString().trim();
        return windSession;
    }

    /**
     * 格式化风组，如果风速为00，则风向为000；如果风向为000，则要改写成360
     *
     * @param windGroup 风组
     * @return 装换后的风组
     */
    public static String formatWindGroup(String windGroup) {
        if (StringUtils.isNotBlank(windGroup)) {
            // 如果风向为000
            if (windGroup.startsWith("000")) {
                windGroup = windGroup.replace(windGroup.substring(0, 3), "360");
            }
            windGroup = windGroup.replace("MPS", "");
            String substring = windGroup.substring(windGroup.length() - 2);
            // 如果风速为00
            if (substring.equals("00")) {
                // 将风向改为000
                windGroup = windGroup.replace(windGroup.substring(0, 3), "000");
            }
            windGroup = windGroup + "MPS";
        }
        return windGroup;
    }

}
