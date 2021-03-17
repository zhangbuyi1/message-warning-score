package com.emdata.messagewarningscore.data.parse.impl.lhj.common;

import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.data.dao.entity.Weather;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Desc AlertCommon 警报公共类
 * @Author lihongjiang
 * @Date 2020/12/29 14:44
 **/
public class AlertCommon {

    // 正则表达式
    public static final String EXPRESSION = "(.*?)([0-3]?[0-9]日)?(([0-2]?[0-9])?:([0-6]?[0-9])?)?-([0-3]?[0-9]日)?([0-2]?[0-9])?:([0-6]?[0-9])?(.*?)";

    // 警报类型 1机场警报 2终端区警报 3区域警报
    public static ThreadLocal<String> alertType = ThreadLocal.withInitial(String::new);
    // 存储预报发布时间（格式：yyyy-mm）
    public static ThreadLocal<String> ymThreadLocal = ThreadLocal.withInitial(String::new);
    // 存储预报发布时间（格式：yyyy-mm-dd HH:mm:ss）
    public static ThreadLocal<String> ymdhmsThreadLocal = ThreadLocal.withInitial(String::new);
    // 多个预计时，保存第一个时间，防止后面的时间没有"天"（格式：yyyy-mm-dd HH:mm:ss）
    public static ThreadLocal<String> start1ThreadLocal = ThreadLocal.withInitial(String::new);
    // 设置预计个数（当前是第几个，默认为0）
    public static ThreadLocal<Integer> numThreadLocal = ThreadLocal.withInitial(() -> 0);
    // 存储原始预报内容
    public static ThreadLocal<String> originalThreadLocal = ThreadLocal.withInitial(String::new);
    // 存储预报内容
    public static ThreadLocal<String> contentThreadLocal = ThreadLocal.withInitial(String::new);
    // 预报内容实例
    public static ThreadLocal<AlertContentResolveDO> contentObject = ThreadLocal.withInitial(AlertContentResolveDO::new);

    // 终端的区域
    public static List<String> terminals = Lists.newLinkedList();
    // 区域的区域
    public static List<String> areas = Lists.newLinkedList();
    // 所有天气
    public static List<String> allWeather = Lists.newLinkedList();
    // 重要天气 priority强度级别 sort按字的数量先多后少规则排列
    public static List<Weather> weathers = Lists.newLinkedList();
    // 文本截取关键字
    public static List<String> keywords = Lists.newLinkedList();
    // 台风
    public static List<String> typhoons = Lists.newLinkedList();

    static {
        // 降水
        weathers.add(new Weather("TS", "TSRA", "雷阵雨,中雷雨,雷雨,", 0, 6));
        weathers.add(new Weather("TS", "TS", "雷暴,雷电,强对流,降水云团,对流云团,热对流,对流,", 1, 1));
        weathers.add(new Weather("TS", "+SHRA", "中到大阵雨,大阵雨,", 2, 2));
        weathers.add(new Weather("TS", "+RA", "强降水,中到大雨,大雨,", 3, 3));
        weathers.add(new Weather("TS", "SHRA", "小到中阵雨,中阵雨,", 4, 4));
        weathers.add(new Weather("TS", "-SHRA", "阵雨,", 5, 8));
        weathers.add(new Weather("TS", "-TSRA", "小雷雨,弱雷雨,小雷阵雨,弱雷阵雨,", 6, 0));
        weathers.add(new Weather("TS", "RA", "小到中雨,降水,", 7, 5));
        weathers.add(new Weather("TS", "-SHRA", "小阵雨,", 8, 7));
        weathers.add(new Weather("TS", "-RA", "雨夹雪转小雨,小雨夹雪,", 9, 9));
        weathers.add(new Weather("TS", "-RA", "雨夹雪,弱降水,弱降雨,小雨,雨带,", 10, 10));

        // 冰雹
        weathers.add(new Weather("GRGS", "GR", "雹,雷伴雹,", 101, 11));
        // 小冰雹
        weathers.add(new Weather("GRGS","GS", "小冰雹,霰,", 102, 11));
        // 风
        weathers.add(new Weather("WIND", "+WIND", "强阵风,偏北大风,西北大风,北大风,大风,", 201, 12));
        weathers.add(new Weather("WIND", "WIND", "阵风,", 202, 13));
        weathers.add(new Weather("WIND", "-WIND", "东北风到偏南风,偏北到西北风,西北到偏西风,东南偏南风,偏南风,西北风,偏北风,正侧风,", 203, 14));
        weathers.add(new Weather("WIND", "-WIND", "风切变,", 204, 15));
        // 雪
        weathers.add(new Weather("SN", "SHSN", "阵雪,大雪,", 301, 16));
        weathers.add(new Weather("SN", "-SN", "雨夹雪转小到中雪,雨夹雪转中雪,雨夹雪转小雪,", 302, 9));
        weathers.add(new Weather("SN", "-SN", "小到中雪,中雪,", 303, 17));
        weathers.add(new Weather("SN", "-SN", "小雪,积雪,雪,积冰,结冰,", 304, 18));
        // 能见度
        weathers.add(new Weather("VIS", "VIS", "能见度,", 401, 20));
        weathers.add(new Weather("VIS", "RVR", "RVR,大雾,雾,", 402, 21));
        // 低云
        weathers.add(new Weather("CLOUD", "CLOUD", "云底高,云高,低云,", 501, 19));
        weathers.add(new Weather("CLOUD", "CLOUD", "CB,阴,", 502, 22));
        weathers.add(new Weather("CLOUD","FC", "龙卷,", 503, 23));
        // 霜
        weathers.add(new Weather("FROST", "FROST", "霜,", 601, 24));
        // 动作
        weathers.add(new Weather("ACTION", "ACTION", "颠簸,", 701, 25));

        // 设置文本截取关键字
        keywords.add("在");
        keywords.add("有");
        keywords.add("顶高");
        keywords.add("预计北京时间");
        keywords.add("重要截取");
        keywords.add("高");

        // 台风
        typhoons.add("黑格比");

        // 终端的区域
        terminals.add("终加两");
        terminals.add("三航A");
        terminals.add("三航B");
        terminals.add("两航A");
        terminals.add("两航B");
        terminals.add("终端");
        terminals.add("两场");
        terminals.add("浦东");
        terminals.add("虹桥");
        terminals.add("SASAN");
        terminals.add("PIKAS");
        terminals.add("NXD");
        terminals.add("AND");

        // 区域的区域
//        areas.add("航路");
//        areas.add("航站");
        areas.add("两场");
        areas.add("沪广航路");
        areas.add("京沪航路");
        areas.add("肥波杭青浦京昌");
        areas.add("虹浦杭波昌福厦");
        areas.add("虹浦杭波温福厦");
        areas.add("肥京杭波温福厦");
        areas.add("昌杭温波虹浦");
        areas.add("济青肥京虹浦");
        areas.add("虹浦肥京杭波");
        areas.add("青肥京虹浦");
        areas.add("肥京杭虹浦");
        areas.add("昌杭虹浦波");
        areas.add("昌杭虹浦");
        areas.add("杭波虹浦");
        areas.add("京杭温昌");
        areas.add("肥京虹浦");
        areas.add("杭虹浦");
        areas.add("杭波温");
        areas.add("虹浦京");
        areas.add("昌温");
        areas.add("厦福");
        areas.add("肥京");
        areas.add("杭宁");
        areas.add("两航B");
        areas.add("终端");
        areas.add("A599上饶-蟠龙段");
        areas.add("A599南浔-蟠龙段");
        areas.add("A599南浔-上饶段");
        areas.add("A599南浔-上饶");
        areas.add("A599南浔-桐庐");
        areas.add("A599九亭-上饶");
        areas.add("A599九亭-P215段");
        areas.add("A599九亭-桐庐");
        areas.add("A599桐庐-蟠龙段");
        areas.add("A599桐庐-蟠龙");
        areas.add("A599桐庐-上饶段");
        areas.add("A599桐庐-上饶");
        areas.add("A599桐庐-南浔");
        areas.add("A599");
        areas.add("A593无锡-邳县段");
        areas.add("A593P41-DALIM段");
        areas.add("A593P44-SASAN");
        areas.add("A593P44-南翔");
        areas.add("A593PIMOL-南翔段");
        areas.add("A593LAGAL-SASAN段");
        areas.add("A593LAGAL-PIMOL段");
        areas.add("A593PIMOL-SASAN");
        areas.add("A593PIMOL-邳县段");
        areas.add("A593邳县-无锡段");
        areas.add("A593邳县-PIMOL段");
        areas.add("A593邳县-DALIM段");
        areas.add("A593邳县-SASAN");
        areas.add("A593SASAN-邳县段");
        areas.add("A593");
        areas.add("A470DAGMO-杏林段");
        areas.add("A470半塔集-邳县段");
        areas.add("A470半塔集-溧水段");
        areas.add("A470半塔集-桐庐段");
        areas.add("A470半塔集-笕桥");
        areas.add("A470桐庐-杏林段");
        areas.add("A470桐庐-笕桥段");
        areas.add("A470桐庐-DAGMO");
        areas.add("A470笕桥-DAGMO段");
        areas.add("A470笕桥-云和段");
        areas.add("A470笕桥-P30段");
        areas.add("A470邳县-溧水段");
        areas.add("A470邳县-笕桥");
        areas.add("A470溧水-桐庐");
        areas.add("A470溧水-云和段");
        areas.add("A470溧水-云和");
        areas.add("A470溧水-邳县段");
        areas.add("A470溧水-半塔集");
        areas.add("A470云和-福清");
        areas.add("A470");
        areas.add("B221薛家岛-ODULO段");
        areas.add("B221薛家岛-PINOT段");
        areas.add("B221薛家岛-IBEGI段");
        areas.add("B221NINAS-嵊州段");
        areas.add("B221ELAGO-嵊州段");
        areas.add("B221ODULO县-IDVEL");
        areas.add("B221ODULO-TOGUG");
        areas.add("B221ELAGO-东山段");
        areas.add("B221ELAGO-东山");
        areas.add("B221PINOT-东山段");
        areas.add("B221庵东-连江段");
        areas.add("B221庵东-东山");
        areas.add("B221东山-连江段");
        areas.add("B221东山-连江");
        areas.add("B221");
        areas.add("B208P280-阜阳段");
        areas.add("B208合肥-阜阳段");
        areas.add("B208阜阳-合肥段");
        areas.add("B208");
        areas.add("H28半塔集-薛家岛段");
        areas.add("H28");
        areas.add("H24向塘-桐庐段");
        areas.add("H24桐庐-P263段");
        areas.add("H24桐庐-OVTAN段");
        areas.add("H24向塘-OVTAN段");
        areas.add("H24P49-桐庐");
        areas.add("H24");
        areas.add("H22东山-ODOPI段");
        areas.add("H22P132-ODOPI段");
        areas.add("H22");
        areas.add("H2P215-杏林段");
        areas.add("H2P48-厦门段");
        areas.add("H2P48-杏林");
        areas.add("H2W13IPRAG-OKATO段");
        areas.add("H2景德镇-青州段");
        areas.add("H2景德镇-青州");
        areas.add("H2景德镇-P48段");
        areas.add("H2合肥-景德镇");
        areas.add("H2合肥-P215段");
        areas.add("H2合肥-P48段");
        areas.add("H2");
        areas.add("M503");
        areas.add("R343溧水-MIDOX段");
        areas.add("R343无锡-MIDOX段");
        areas.add("R343MIDOX-溧水段");
        areas.add("R343MIDOX-SASAN段");
        areas.add("R343溧水-无锡段");
        areas.add("R343合肥-SASAM段");
        areas.add("R343合肥-SASAN");
        areas.add("R343全段");
        areas.add("R343");
        areas.add("W127合肥-徐州段");
        areas.add("W19向塘-蟠龙段");
        areas.add("W19OSONO-潘龙段");
        areas.add("W19");
        areas.add("W13IPRAG-OKATO段");
        areas.add("W13");
        areas.add("浦东机场");
        areas.add("虹桥机场");
        areas.add("杭州机场");
        areas.add("宁波机场");
        areas.add("温州机场");
        areas.add("合肥机场");
        areas.add("南京机场");
        areas.add("青岛机场");
        areas.add("济南机场");
        areas.add("南昌机场");
        areas.add("福州机场");
        areas.add("厦门机场");
        areas.add("SASAN");
        areas.add("PIKAS");


        // 所有天气
        allWeather.add("雷暴");
        allWeather.add("中到大阵雨");
        allWeather.add("小到中阵雨");
        allWeather.add("小到中雨");
        allWeather.add("大阵雨");
        allWeather.add("中阵雨");
        allWeather.add("小阵雨");
        allWeather.add("雷阵雨");
        allWeather.add("雷雨");
        allWeather.add("阵雨");

        allWeather.add("雨夹雪转小到中雪");
        allWeather.add("雨夹雪转中雪");
        allWeather.add("雨夹雪转小雪");
        allWeather.add("雨夹雪转小雨");

        allWeather.add("中到大雨");
        allWeather.add("强降水");
        allWeather.add("小雨夹雪");
        allWeather.add("雨夹雪");
        allWeather.add("小雨");

        allWeather.add("大雪");
        allWeather.add("小到中雪");
        allWeather.add("中雪");
        allWeather.add("小雪");
        allWeather.add("积雪");
        allWeather.add("结冰");

        allWeather.add("云底高");
        allWeather.add("云高");
        allWeather.add("低云");
        allWeather.add("能见度");
        allWeather.add("RVR");
        allWeather.add("大雾");
        allWeather.add("雾");

        allWeather.add("东北风到偏南风");
        allWeather.add("偏北到西北风");
        allWeather.add("西北到偏西风");
        allWeather.add("偏北大风");
        allWeather.add("偏南风");
        allWeather.add("正侧风");
        allWeather.add("强阵风");
        allWeather.add("阵风");
        allWeather.add("大风");
        allWeather.add("风切变");

        allWeather.add("阴");
        allWeather.add("CB");

        allWeather.add("降水云团");
        allWeather.add("对流");
        allWeather.add("颠簸");

    }
}
