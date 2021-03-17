package com.emdata.messagewarningscore.common.parse;


import com.emdata.messagewarningscore.common.exception.MetarParseException;
import com.emdata.messagewarningscore.common.parse.bo.ChangeGroup;
import com.emdata.messagewarningscore.common.parse.bo.TrendElement;
import com.emdata.messagewarningscore.common.parse.element.CloudGroup;
import com.emdata.messagewarningscore.common.parse.element.ElementContainer;
import com.emdata.messagewarningscore.common.parse.element.Weather;
import com.emdata.messagewarningscore.common.parse.element.WindGroup;
import com.emdata.messagewarningscore.common.parse.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/10 11:58
 */
public class MetarTrendParseUtil {

    public static List<String> METAR_HEADERS = Arrays.asList("METAR", "SPECI");

    /**
     * 机场编码规则
     */
    private static final String AIRPORT_CODE = "^[A-Z]{4}$";

    /**
     * 温度/露点格式
     */
    private static final String TEMP_REGEX = "^(\\d{2}|M\\d{2})/(\\d{2}|M\\d{2})$";

    /**
     * 气压格式
     */
    private static final String AIR_PRESSES_REGEX = "^Q\\d{4}$";

    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmm"));

    public static void main(String[] args) {
        String metarStr = "METAR ZSQZ 020700Z 26005MPS 9999 RASN SCT026CB 29/24 Q1004 BECMG TL0730 NSW=";

        String yyyyMM = "202009";

        TrendElement parse;
        try {
            System.out.println(metarStr);
            parse = parse(metarStr, yyyyMM);
            System.out.println(parse);
            for (ChangeGroup trendCroup : parse.getTrendCroups()) {
                System.out.println(trendCroup);
            }
        } catch (MetarParseException e) {
            e.printStackTrace();
        }

//        File f = new File("E:\\data\\metar\\metar.txt");
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(f));
//            String metar = null;
//            while ((metar = br.readLine()) != null) {
//                System.out.println(metar);
//                if (metar.startsWith("#")) {
//                    continue;
//                }
//                MetarElement parse;
////                try {
//                parse = parse(metar, yyyyMM);
////                } catch (MetarParseException e) {
////                    e.printStackTrace();
////                    continue;
////                }
//
//                List<MetarTrendCroup> trendCroups = parse.getTrendCroups();
//                System.out.println(parse);
//                parse.setTrendCroups(null);
//                System.out.println(trendCroups);
//
//                System.out.println();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    /**
     * 解析趋势报文
     *
     * @param metarStr 报文
     * @param yyyyMM   年月
     * @return 报文对象
     * @throws MetarParseException
     */
    public static TrendElement parse(String metarStr, String yyyyMM) throws MetarParseException {
        TrendElement metar = new TrendElement();

        // 按照空格分割
        String[] ms = metarStr.trim().split("\\s+");

        // 判断=
        if (!ms[ms.length - 1].endsWith("=")) {
            throw new MetarParseException("报文必须以\"=\"结尾");
        }
        ms[ms.length - 1] = ms[ms.length - 1].replace("=", "");

        List<String> list = new ArrayList<>();
        for (String m : ms) {
            // 将一些不需要处理的内容移除
            if (m.startsWith("RE") || m.startsWith("WS") || m.matches("R[0-9]{2}") || m.matches("^R\\d{2}.+$") || m.matches("ALL") || m.matches("RWY")
                    || m.equals("COR") || m.equals("AUTO")) {
                continue;
            }
            list.add(m);
        }
        ms = list.toArray(new String[0]);

        int index = 0;

        // 报头
        ElementContainer<String> headerContainer = parseHeader(ms, index);
        metar.setHeader(headerContainer.get());

        index = headerContainer.getIndex();

        // 机场编码
        ElementContainer<String> codeContainer = parseCode(ms, index);
        metar.setAirportCode(codeContainer.get());

        index = codeContainer.getIndex();

        // 时间
        ElementContainer<Date> dateElementContainer = parseDate(ms, index, yyyyMM);
        metar.setReleaseTime(dateElementContainer.get());

        index = dateElementContainer.getIndex();

        // 风
        ElementContainer<WindGroup> windGroupContainer = WindGroupUtil.parse(ms, index);
        metar.setWindGroup(windGroupContainer.get());

        index = windGroupContainer.getIndex();

        // 能见度
        ElementContainer<Double> visContainer = VisibilityUtil.parse(ms, index);
        metar.setVisibility(visContainer.get());

        index = visContainer.getIndex();

        // 天气现象
        ElementContainer<List<Weather>> weatherContainer = WeatherUtil.parse(ms, index);
        metar.setWeathers(weatherContainer.get());

        index = weatherContainer.getIndex();

        // 云组
        ElementContainer<List<CloudGroup>> cloudContainer = CloudGroupUtil.parse(ms, index);
        metar.setCloudGroups(cloudContainer.get());

        index = cloudContainer.getIndex();

        // 温度/露点温度
        try {
            ElementContainer<List<Double>> tempContainer = parseTemp(ms, index);
            List<Double> temps = tempContainer.get();
            metar.setTemperature(temps.get(0));
            metar.setDewPointTemperature(temps.get(1));
            index = tempContainer.getIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 气压
        ElementContainer<Double> airPressureContainer = parseAirPressure(ms, index);
        metar.setAirPressure(airPressureContainer.get());

        index = airPressureContainer.getIndex();

        // 变化组
        ElementContainer<List<ChangeGroup>> trendCroupContainer = MetarTrendUtil.parse(ms, index, metar.getReleaseTime());
        metar.setTrendCroups(trendCroupContainer.get());

        return metar;
    }

    /**
     * 解析气压
     *
     * @param ms    元素数组
     * @param index 读取的索引
     * @return 气压
     */
    private static ElementContainer<Double> parseAirPressure(String[] ms, int index) {
        if (index >= ms.length) {
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }

        String str = ms[index];
        if (!str.matches(AIR_PRESSES_REGEX)) {
            throw new MetarParseException("METAR报头气压组格式错误：" + str);
        }

        Double p = Double.valueOf(str.substring(1));

        index++;
        return new ElementContainer<>(p, index);
    }

    /**
     * 解析温度/露点温度
     *
     * @param ms    元素数组
     * @param index 读取的索引
     * @return [温度, 露点温度]
     */
    private static ElementContainer<List<Double>> parseTemp(String[] ms, int index) {
        if (index >= ms.length) {
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }

        String str = ms[index];
        if (!str.matches(TEMP_REGEX)) {
            throw new MetarParseException("METAR报头温度组格式错误：" + str);
        }

        String[] ts = str.split("/");
        Double temperature = null;
        Double dewPointTemperature = null;

        if (ts[0].startsWith("M")) {
            temperature = 0 - Double.parseDouble(ts[0].substring(1));
        } else {
            temperature = Double.valueOf(ts[0]);
        }

        if (ts[1].startsWith("M")) {
            dewPointTemperature = 0 - Double.parseDouble(ts[1].substring(1));
        } else {
            dewPointTemperature = Double.valueOf(ts[1]);
        }

        index++;
        return new ElementContainer<>(Arrays.asList(temperature, dewPointTemperature), index);
    }

    /**
     * 解析报头
     *
     * @param ms    元素数组
     * @param index 读取的索引
     * @return 报头
     */
    private static ElementContainer<String> parseHeader(String[] ms, int index) {
        if (index >= ms.length) {
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }
        String header = ms[index];
        if (!METAR_HEADERS.contains(header)) {
            throw new MetarParseException("METAR报头不对：" + header);
        }
        index++;
        return new ElementContainer<>(header, index);
    }

    /**
     * 解析机场编码
     *
     * @param ms    元素数组
     * @param index 读取的索引
     * @return 机场编码
     */
    private static ElementContainer<String> parseCode(String[] ms, int index) {
        if (index >= ms.length) {
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }
        String code = ms[index];
        if (!code.matches(AIRPORT_CODE)) {
            throw new MetarParseException("METAR机场编码格式不对：" + code);
        }
        index++;
        return new ElementContainer<>(code, index);
    }

    /**
     * 解析发布时间
     *
     * @param ms    元素数组
     * @param index 读取的索引
     * @return 发布时间
     */
    private static ElementContainer<Date> parseDate(String[] ms, int index, String yyyyMM) {
        if (index >= ms.length) {
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }

        String timeStr = ms[index];
        if (!timeStr.endsWith("Z") || timeStr.length() != 7) {
            throw new MetarParseException("METAR报时间不对：" + timeStr);
        }
        timeStr = timeStr.substring(0, timeStr.lastIndexOf("Z"));
        // yyyyMMddHHmm
        timeStr = yyyyMM + timeStr;
        Date parse = null;
        try {
            parse = THREAD_LOCAL.get().parse(timeStr);
        } catch (ParseException e) {
            throw new MetarParseException("METAR报时间解析异常" + timeStr);
        }
        index++;
        return new ElementContainer<>(parse, index);
    }
}
