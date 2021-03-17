package com.emdata.messagewarningscore.common.parse.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.exception.MetarParseException;
import com.emdata.messagewarningscore.common.parse.element.ElementContainer;
import com.emdata.messagewarningscore.common.parse.element.Weather;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 天气现象解析工具类
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/11 14:50
 */
public class WeatherUtil {

    private static final String CAVOK = "CAVOK";

    /**
     * 使用简语NSW 编报天气现象组，表示重要天气现象全部结束
     */
    private static final String NSW = "NSW";

    /**
     * 特征描述
     */
    private static final List<String> FEATURE_LIST = Arrays.asList("TS", "FZ", "SH", "BL", "DR", "MI", "BC", "PR");

    /**
     * 天气现象，0：降水类，1：视程障碍现象类，2：其他类
     */
    private static final List<List<String>> WEATHER_TYPES = Arrays.asList(
            Arrays.asList("DZ", "RA", "SN", "SG", "PL", "GR", "GS"),
            Arrays.asList("FG", "BR", "SA", "DU", "HZ", "FU", "DRSA", "DRDU", "DRSN", "VA"),
            Arrays.asList("PO", "SQ", "FC", "DS", "SS")
    );

    /**
     * 解析报文天气现象
     *
     * @param eles  报文要素数组
     * @param index 天气现象开始索引
     * @return 天气现象组，如果不是天气现象组，则会返回空集合
     */
    public static ElementContainer<List<Weather>> parse(String[] eles, int index) throws MetarParseException {
        return parse(eles, index, true);
    }

    /**
     * 解析报文天气现象
     *
     * @param eles  报文要素数组
     * @param index 天气现象开始索引
     * @param check 是否进行格式检查，true：检查不通过时，抛出异常，false：检查不通过时返回空集合
     * @return 天气现象组，如果不是天气现象组，则会返回空集合
     */
    public static ElementContainer<List<Weather>> parse(String[] eles, int index, boolean check) throws MetarParseException {
        if (index >= eles.length) {
            if (!check) {
                return new ElementContainer<>(null, index);
            }
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }

        String w = eles[index];

        List<Weather> weathers = new ArrayList<>();

        // 如果是CAVOK、NSW，则没有天气现象
        if (CAVOK.equals(w)) {

        } else if ("//".equals(w)) {
            index++;
        } else if (NSW.equals(w)) {
            index++;
            weathers.add(new Weather());
        } else {
            Weather weather = null;
            // 接着解析后面的，如果有多个天气现象组，则添加到集合
            do {
                w = eles[index];
                try {
                    weather = null;
                    weather = parseWeather(w);
                } catch (MetarParseException e) {
                    if (check) {
                        throw e;
                    }
                }
                if (weather != null) {
                    weathers.add(weather);
                    index++;
                }
                // 如果解析不到天气现象了，说明天气现象组结束了
            } while (weather != null && index < eles.length);
        }

        // 判断顺序是否符合
        for (int i = 0; i < weathers.size() - 1; i++) {
            Weather w1 = weathers.get(i);
            Weather w2 = weathers.get(i + 1);

            if (w1.getOrder() > w2.getOrder()) {
                throw new MetarParseException("METAR格式，天气现象顺序错误：" + weathers);
            }
        }

        return new ElementContainer<>(weathers, index);
    }

    /**
     * 解析天气现象
     *
     * @param wStr 天气现象字符串
     * @return 不符合返回null，符合返回天气现象
     */
    @Nullable
    public static Weather parseWeather(String wStr) throws MetarParseException {
        String str = wStr;
        if (str.length() < 2) {
            throw new MetarParseException("天气现象格式错误: " + wStr);
        }
        // 强弱
        Weather.Strength strength = Weather.Strength.MIDDLE;
        if (str.startsWith("+")) {
            str = str.substring(1);
            strength = Weather.Strength.STRONG;
        } else if (str.startsWith("-")) {
            str = str.substring(1);
            strength = Weather.Strength.WEAK;
        }

        // 特征描述
        String feature = null;
        String f = str.substring(0, 2);
        if (FEATURE_LIST.contains(f)) {
            feature = f;
            str = str.substring(2);
        }

        // 天气类型
        String type = null;
        int order = 0;
        out:
        for (int i = 0; i < WEATHER_TYPES.size(); i++) {
            List<String> types = WEATHER_TYPES.get(i);

            // 天气类型，可能是SNRA，则天气现象包含其中一个，则表示属于该类
            for (int j = 0; j < types.size(); j++) {
                // 不包含天气现象
                if (!str.contains(types.get(j))) {
                    continue;
                }

                // 如果天气现象长度大于2，说明有多种天气现象，判断每个天气现象是否对的
                int si = 0;
                while (si < str.length()) {
                    int e = Math.min(si + 2, str.length());
                    String t = str.substring(si, e);
                    if (!types.contains(t)) {
                        throw new MetarParseException("天气现象格式错误: " + wStr + " \"" + t + "\"");
                    }
                    si += 2;
                }

                type = str;
                order = i;
                break out;
            }
        }

        // 如果天气现象不符合，返回null
        if (type == null && feature == null) {
            return null;
        }

        Weather w = new Weather();
        w.setStrength(strength);
        w.setFeature(feature);
        w.setType(type);
        w.setOrder(order);

        if (!checkWeather(w)) {
            throw new MetarParseException("天气现象格式错误: " + wStr);
        }

        return w;
    }

    public static boolean checkWeather(Weather w) {
        // DR 和BL 只能与DU、SA 和SN 简语结合使用。例如：BLSN
        List<String> db = Arrays.asList("DR", "BL");
        List<String> dss = Arrays.asList("DU", "SA", "SN");
        if (db.contains(w.getFeature())) {
            return dss.contains(w.getType());
        }

        String type = w.getType();

        // SH 只能与简语RA、SN、SG、GS 和GR 中的一个或几个结合使用，以表示有阵性降水。例如：SHSN
        List<String> rs = Arrays.asList("RA", "SN", "GS", "GR");
        if ("SH".equals(w.getFeature())) {
            // 如果天气现象长度大于2，说明有多种天气现象
            if (checkType(type, rs)) return false;
        }

        // TS 只能与简语RA、SN、PL、SG、GS 和GR 中的一个或几个结合使用，以表示机场有雷暴并伴有降水。例如：TSSNGS
        List<String> tsr = Arrays.asList("RA", "SN", "PL", "SG", "GR");
        if ("TS".equals(w.getFeature()) && StringUtils.isNotBlank(type)) {
            // 如果天气现象长度大于2，说明有多种天气现象
            if (checkType(type, tsr)) return false;
        }

        // FZ 只能与简语FG、DZ 和RA 结合使用。例如：FZRA
        List<String> fd = Arrays.asList("FG", "DZ", "RA");
        if ("FZ".equals(w.getFeature())) {
            return fd.contains(w.getType());
        }

        return true;
    }

    private static boolean checkType(String type, List<String> tsr) {
        int i = 0;
        while (i < type.length()) {
            int e = Math.min(i + 2, type.length());
            String t = type.substring(i, e);
            if (!tsr.contains(t)) {
                return true;
            }
            i += 2;
        }
        return false;
    }

    /**
     * 合并天气现象
     *
     * @param weathers 天气现象集合
     * @return 天气现象字符串
     */
    public static String combineWeather(List<Weather> weathers) {
        String weather = "";
        if (CollectionUtils.isNotEmpty(weathers)) {
            // 将weather按照order值正序排序
            weathers.sort(Comparator.comparing(Weather::getOrder));
            StringBuilder builder = new StringBuilder();
            for (Weather we : weathers) {
                String splice = we.splice();
                builder.append(splice).append(" ");
            }
            weather = builder.toString().trim();
        }
        return weather;
    }


}
