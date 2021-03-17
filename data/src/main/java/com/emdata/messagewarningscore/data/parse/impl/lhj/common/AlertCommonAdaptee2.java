package com.emdata.messagewarningscore.data.parse.impl.lhj.common;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.ImportantWeatherDO;
import com.emdata.messagewarningscore.common.enums.WarningNatureEnum;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.data.util.StringUtil;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

/**
 * @Desc AlertCommonAdaptee2
 * @Author lihongjiang
 * @Date 2020/12/29 14:47
 **/
public class AlertCommonAdaptee2 extends AlertCommon {

    /**
     * 获取更正后的内容
     **/
    public String getCorrectedContent(String originalContent){
        if (originalContent.contains("更正后的警报内容为：")) {
            return originalContent.split("更正后的警报内容为：")[1];
        }
        return originalContent;
    }

    public String deleteOtherKeys(String originalContent){
        if (originalContent.contains("强度维持")) {
            return originalContent.replace("强度维持", "强度不变");
        }
        return originalContent;
    }

    /**
     * 预计 公共
     **/
    public String expectedCommon(String textContent){
        return textContent
                .replace("预计1日", "预计北京时间1日")
                .replace("预计2日", "预计北京时间2日")
                .replace("预计3日", "预计北京时间3日")
                .replace("预计4日", "预计北京时间4日")
                .replace("预计5日", "预计北京时间5日")
                .replace("预计6日", "预计北京时间6日")
                .replace("预计7日", "预计北京时间7日")
                .replace("预计8日", "预计北京时间8日")
                .replace("预计9日", "预计北京时间9日")

                .replace("预计01日", "预计北京时间01日")
                .replace("预计02日", "预计北京时间02日")
                .replace("预计03日", "预计北京时间03日")
                .replace("预计04日", "预计北京时间04日")
                .replace("预计05日", "预计北京时间05日")
                .replace("预计06日", "预计北京时间06日")
                .replace("预计07日", "预计北京时间07日")
                .replace("预计08日", "预计北京时间08日")
                .replace("预计09日", "预计北京时间09日")

                .replace("预计10日", "预计北京时间10日")
                .replace("预计11日", "预计北京时间11日")
                .replace("预计12日", "预计北京时间12日")
                .replace("预计13日", "预计北京时间13日")
                .replace("预计14日", "预计北京时间14日")
                .replace("预计15日", "预计北京时间15日")
                .replace("预计16日", "预计北京时间16日")
                .replace("预计17日", "预计北京时间17日")
                .replace("预计18日", "预计北京时间18日")
                .replace("预计19日", "预计北京时间19日")
                .replace("预计20日", "预计北京时间20日")
                .replace("预计21日", "预计北京时间21日")
                .replace("预计22日", "预计北京时间22日")
                .replace("预计23日", "预计北京时间23日")
                .replace("预计24日", "预计北京时间24日")
                .replace("预计25日", "预计北京时间25日")
                .replace("预计26日", "预计北京时间26日")
                .replace("预计27日", "预计北京时间27日")
                .replace("预计28日", "预计北京时间28日")
                .replace("预计29日", "预计北京时间29日")
                .replace("预计30日", "预计北京时间30日")
                .replace("预计31日", "预计北京时间31日")


                .replace("预计1:", "预计北京时间1:")
                .replace("预计2:", "预计北京时间2:")
                .replace("预计3:", "预计北京时间3:")
                .replace("预计4:", "预计北京时间4:")
                .replace("预计5:", "预计北京时间5:")
                .replace("预计6:", "预计北京时间6:")
                .replace("预计7:", "预计北京时间7:")
                .replace("预计8:", "预计北京时间8:")
                .replace("预计9:", "预计北京时间9:")

                .replace("预计01:", "预计北京时间01:")
                .replace("预计02:", "预计北京时间02:")
                .replace("预计03:", "预计北京时间03:")
                .replace("预计04:", "预计北京时间04:")
                .replace("预计05:", "预计北京时间05:")
                .replace("预计06:", "预计北京时间06:")
                .replace("预计07:", "预计北京时间07:")
                .replace("预计08:", "预计北京时间08:")
                .replace("预计09:", "预计北京时间09:")

                .replace("预计10:", "预计北京时间10:")
                .replace("预计11:", "预计北京时间11:")
                .replace("预计12:", "预计北京时间12:")
                .replace("预计13:", "预计北京时间13:")
                .replace("预计14:", "预计北京时间14:")
                .replace("预计15:", "预计北京时间15:")
                .replace("预计16:", "预计北京时间16:")
                .replace("预计17:", "预计北京时间17:")
                .replace("预计18:", "预计北京时间18:")
                .replace("预计19:", "预计北京时间19:")
                .replace("预计20:", "预计北京时间20:")
                .replace("预计21:", "预计北京时间21:")
                .replace("预计22:", "预计北京时间22:")
                .replace("预计23:", "预计北京时间23:")
                .replace("预计24:", "预计北京时间24:")

                .replace("，1日", "，预计北京时间1日")
                .replace("，2日", "，预计北京时间2日")
                .replace("，3日", "，预计北京时间3日")
                .replace("，4日", "，预计北京时间4日")
                .replace("，5日", "，预计北京时间5日")
                .replace("，6日", "，预计北京时间6日")
                .replace("，7日", "，预计北京时间7日")
                .replace("，8日", "，预计北京时间8日")
                .replace("，9日", "，预计北京时间9日")

                .replace("，01日", "，预计北京时间01日")
                .replace("，02日", "，预计北京时间02日")
                .replace("，03日", "，预计北京时间03日")
                .replace("，04日", "，预计北京时间04日")
                .replace("，05日", "，预计北京时间05日")
                .replace("，06日", "，预计北京时间06日")
                .replace("，07日", "，预计北京时间07日")
                .replace("，08日", "，预计北京时间08日")
                .replace("，09日", "，预计北京时间09日")

                .replace("，10日", "，预计北京时间10日")
                .replace("，11日", "，预计北京时间11日")
                .replace("，12日", "，预计北京时间12日")
                .replace("，13日", "，预计北京时间13日")
                .replace("，14日", "，预计北京时间14日")
                .replace("，15日", "，预计北京时间15日")
                .replace("，16日", "，预计北京时间16日")
                .replace("，17日", "，预计北京时间17日")
                .replace("，18日", "，预计北京时间18日")
                .replace("，19日", "，预计北京时间19日")
                .replace("，20日", "，预计北京时间20日")
                .replace("，21日", "，预计北京时间21日")
                .replace("，22日", "，预计北京时间22日")
                .replace("，23日", "，预计北京时间23日")
                .replace("，24日", "，预计北京时间24日")
                .replace("，25日", "，预计北京时间25日")
                .replace("，26日", "，预计北京时间26日")
                .replace("，27日", "，预计北京时间27日")
                .replace("，28日", "，预计北京时间28日")
                .replace("，29日", "，预计北京时间29日")
                .replace("，30日", "，预计北京时间30日")

                .replace(" 1日", "，预计北京时间1日")
                .replace(" 2日", "，预计北京时间2日")
                .replace(" 3日", "，预计北京时间3日")
                .replace(" 4日", "，预计北京时间4日")
                .replace(" 5日", "，预计北京时间5日")
                .replace(" 6日", "，预计北京时间6日")
                .replace(" 7日", "，预计北京时间7日")
                .replace(" 8日", "，预计北京时间8日")
                .replace(" 9日", "，预计北京时间9日")

                .replace(" 01日", "，预计北京时间01日")
                .replace(" 02日", "，预计北京时间02日")
                .replace(" 03日", "，预计北京时间03日")
                .replace(" 04日", "，预计北京时间04日")
                .replace(" 05日", "，预计北京时间05日")
                .replace(" 06日", "，预计北京时间06日")
                .replace(" 07日", "，预计北京时间07日")
                .replace(" 08日", "，预计北京时间08日")
                .replace(" 09日", "，预计北京时间09日")

                .replace(" 10日", "，预计北京时间10日")
                .replace(" 11日", "，预计北京时间11日")
                .replace(" 12日", "，预计北京时间12日")
                .replace(" 13日", "，预计北京时间13日")
                .replace(" 14日", "，预计北京时间14日")
                .replace(" 15日", "，预计北京时间15日")
                .replace(" 16日", "，预计北京时间16日")
                .replace(" 17日", "，预计北京时间17日")
                .replace(" 18日", "，预计北京时间18日")
                .replace(" 19日", "，预计北京时间19日")
                .replace(" 20日", "，预计北京时间20日")
                .replace(" 21日", "，预计北京时间21日")
                .replace(" 22日", "，预计北京时间22日")
                .replace(" 23日", "，预计北京时间23日")
                .replace(" 24日", "，预计北京时间24日")
                .replace(" 25日", "，预计北京时间25日")
                .replace(" 26日", "，预计北京时间26日")
                .replace(" 27日", "，预计北京时间27日")
                .replace(" 28日", "，预计北京时间28日")
                .replace(" 29日", "，预计北京时间29日")
                .replace(" 30日", "，预计北京时间30日")
                .replace(" 31日", "，预计北京时间31日")

                .replace("，1:", "，预计北京时间1:")
                .replace("，2:", "，预计北京时间2:")
                .replace("，3:", "，预计北京时间3:")
                .replace("，4:", "，预计北京时间4:")
                .replace("，5:", "，预计北京时间5:")
                .replace("，6:", "，预计北京时间6:")
                .replace("，7:", "，预计北京时间7:")
                .replace("，8:", "，预计北京时间8:")
                .replace("，9:", "，预计北京时间9:")

                .replace("，01:", "，预计北京时间01:")
                .replace("，02:", "，预计北京时间02:")
                .replace("，03:", "，预计北京时间03:")
                .replace("，04:", "，预计北京时间04:")
                .replace("，05:", "，预计北京时间05:")
                .replace("，06:", "，预计北京时间06:")
                .replace("，07:", "，预计北京时间07:")
                .replace("，08:", "，预计北京时间08:")
                .replace("，09:", "，预计北京时间09:")

                .replace("，10:", "，预计北京时间10:")
                .replace("，11:", "，预计北京时间11:")
                .replace("，12:", "，预计北京时间12:")
                .replace("，13:", "，预计北京时间13:")
                .replace("，14:", "，预计北京时间14:")
                .replace("，15:", "，预计北京时间15:")
                .replace("，16:", "，预计北京时间16:")
                .replace("，17:", "，预计北京时间17:")
                .replace("，18:", "，预计北京时间18:")
                .replace("，19:", "，预计北京时间19:")
                .replace("，20:", "，预计北京时间20:")
                .replace("，21:", "，预计北京时间21:")
                .replace("，22:", "，预计北京时间22:")
                .replace("，23:", "，预计北京时间23:")
                .replace("，24:", "，预计北京时间24:")
                .replace("，25:", "，预计北京时间25:")
                .replace("，26:", "，预计北京时间26:")
                .replace("，27:", "，预计北京时间27:")
                .replace("，28:", "，预计北京时间28:")
                .replace("，29:", "，预计北京时间29:")
                .replace("，30:", "，预计北京时间30:")
                .replace("，31:", "，预计北京时间31:")

                .replace("。1:", "，预计北京时间1:")
                .replace("。2:", "，预计北京时间2:")
                .replace("。3:", "，预计北京时间3:")
                .replace("。4:", "，预计北京时间4:")
                .replace("。5:", "，预计北京时间5:")
                .replace("。6:", "，预计北京时间6:")
                .replace("。7:", "，预计北京时间7:")
                .replace("。8:", "，预计北京时间8:")
                .replace("。9:", "，预计北京时间9:")

                .replace("。01:", "，预计北京时间01:")
                .replace("。02:", "，预计北京时间02:")
                .replace("。03:", "，预计北京时间03:")
                .replace("。04:", "，预计北京时间04:")
                .replace("。05:", "，预计北京时间05:")
                .replace("。06:", "，预计北京时间06:")
                .replace("。07:", "，预计北京时间07:")
                .replace("。08:", "，预计北京时间08:")
                .replace("。09:", "，预计北京时间09:")

                .replace("。10:", "，预计北京时间10:")
                .replace("。11:", "，预计北京时间11:")
                .replace("。12:", "，预计北京时间12:")
                .replace("。13:", "，预计北京时间13:")
                .replace("。14:", "，预计北京时间14:")
                .replace("。15:", "，预计北京时间15:")
                .replace("。16:", "，预计北京时间16:")
                .replace("。17:", "，预计北京时间17:")
                .replace("。18:", "，预计北京时间18:")
                .replace("。19:", "，预计北京时间19:")
                .replace("。20:", "，预计北京时间20:")
                .replace("。21:", "，预计北京时间21:")
                .replace("。22:", "，预计北京时间22:")
                .replace("。23:", "，预计北京时间23:")
                .replace("。24:", "，预计北京时间24:")
                .replace("。25:", "，预计北京时间25:")
                .replace("。26:", "，预计北京时间26:")
                .replace("。27:", "，预计北京时间27:")
                .replace("。28:", "，预计北京时间28:")
                .replace("。29:", "，预计北京时间29:")
                .replace("。30:", "，预计北京时间30:")
                .replace("。31:", "，预计北京时间31:")

                .replace("；19:", "，预计北京时间19:")

                ;
    }

    /**
     * 预计 机场
     **/
    public String expectedAirport(String textContent){
        // 预计北京时间转换
        String replace = textContent
                .replace("预计“黑格比”", "")

                .replace("预计虹桥机场于北京时间", "虹桥机场，预计北京时间")
                .replace("预计北京时间北京时间", "预计北京时间")
                .replace("预计北京时间：目前", "预计北京时间")
                .replace("预计目前-北京时间", "预计北京时间-")
                .replace("预计北京时间目前", "预计北京时间")
                .replace("预计于北京时间", "预计北京时间")
                .replace("预计北京时间：", "预计北京时间")
                .replace("预计：目前", "预计北京时间")
                .replace("预计目前", "预计北京时间")


                .replace("， 虹桥机场 10:", "，预计北京时间10:")
                .replace("，浦东机场11:", "，预计北京时间11:")
                .replace("虹桥、浦东机场 ", "预计北京时间")
                .replace("； 上海两场", "。预计北京时间")

                ;
        return expectedCommon(replace);
    }

    /**
     * 终端区的删除多余的内容
     **/
    public String terminalDelete(String content){
        String deleted = StringUtil.deleteBetweenByKey(content, "概率", "%");
        return deleted
                // 删除多余且影响分割的关键字
                .replace("云顶高度8-10千米，以约30公里/小时速度向东移动", "")
                .replace("自南向北有降水过程，局部有", "")
                .replace("雷雨云顶高8-9公里", "雷雨")
                .replace("上海两场强降水情况：", "")
                .replace("上海两场大风情况：", "")
                .replace("明晨南部地区局地CB", "")
                .replace("午间系统南压", "")
//                .replace("期间终端区内", "")
                .replace("，主要影响", "")
                .replace("等进出港点", "")
                .replace("另外", "")
                .replace("其中", "")
                .replace("m/s", "")

                .replace("下午至上半夜有", "12:00-24:00")
                .replace("0之后转阴有时有", "0-24:00")
                .replace("0之后", "0-24:00")
                .replace("0后", "0-24:00")
                ;
    }

    /**
     * 终端区的预计
     **/
    public String expectedTerminal(String textContent){
        // 预计北京时间转换
        String replace = textContent
                .replace("预计今日上午影响上海终端区东南部，午后进一步加强北抬，影响时间：", "预计北京时间终端")
                .replace("预计上海终端区有中等及以上降水云团发展", "上海终端区有中等及以上降水云团")
                .replace("预计上海终端区将有分散的对流云团发展", "上海终端区有分散的对流云团")
                .replace("预计8日上海终端区有较强对流云团发展", "上海终端区有较强对流云团")
                .replace("预计8日上海终端区有对流云团发展", "上海终端区有对流云团")
                .replace("预计北京时间上海终端区有对流云团发展", "上海终端区有对流云团")
                .replace("预计上海终端区有对流云团发展", "上海终端区有对流云团")


                .replace("预计系统性对流云带", "对流")

                .replace("预计对流云团北京时间", "对流云团，预计北京时间")

                .replace("预计影响时间：上海终端区", "预计北京时间终端")
                .replace("预计上海终端区北京时间", "预计北京时间终端")
                .replace("预计上海终端区南部", "预计北京时间终端")
                .replace("预计上海两场目前-", "预计北京时间两场目前-")

                .replace("预计今日上午", "预计北京时间今日上午")
                .replace("目前至北京时间", "预计北京时间-")
                .replace("预计影响时间：", "预计北京时间")
                .replace("预计虹桥", "预计北京时间虹桥")
                .replace("预计浦东", "预计北京时间浦东")
                .replace("预计今日", "预计北京时间今日")
                .replace("。北京时间", "，预计北京时间")
                .replace("，北京时间", "，预计北京时间")
                .replace("预计上海", "预计北京时间上海")
                .replace("预计目前-", "预计北京时间-")

                .replace("预计虹桥、浦东机场", "预计北京时间两场")
                ;
        return expectedCommon(replace);
    }

    /**
     * 终端区的区域
     **/
    public String terminalArea(String areaContent){
        return areaContent
                // 终端+两场
                .replace("上海终端区及上海两场还将有", "终加两")
                // 终端对流
                .replace("在上海终端区有较强的降水云团发展东移", "终端较强降水云团")
                .replace("上海终端区内多分散性对流发展", "终端对流")
                .replace("在上海终端区北部有对流发展", "终端对流")
                .replace("以分散孤立对流影响上海终端区（雷雨云团覆盖率5%）", "终端，雷雨，对流")
                .replace("以团状对流影响上海终端区（雷雨云团覆盖率10%）", "终端，雷雨，对流")
                // 终端
                .replace("上海终端区阴有", "终端，阴，")
                .replace("影响上海终端区，期间终端区内多分散性", "终端")
                .replace("影响上海终端区，有分散性", "终端")
                .replace("影响上海终端区西部和南部", "终端")
                .replace("影响上海终端区西部和北部", "终端")
                .replace("影响上海终端区北部和东部", "终端")
                .replace("影响上海终端区，局部有", "终端")
                .replace("影响上海终端区，局地有", "终端")
                .replace("上海终端区西部和北部有", "终端")
                .replace("上海终端区内多分散性", "终端")
                .replace("上海终端区有较大范围", "终端")
                .replace("影响上海终端区中北部", "终端")
                .replace("终端，期间终端区内多", "终端")
                .replace("在上海终端区南部有", "终端")
                .replace("上海终端区南部局地", "终端")
                .replace("上海终端区有分散的", "终端")
                .replace("影响上海终端区，有", "终端")
                .replace("终端区中南部有分散", "终端")
                .replace("上海终端区多分散性", "终端")
                .replace("上海终端区有分散性", "终端")
                .replace("上海终端区北京时间", "终端")
                .replace("影响上海终端区大部", "终端")
                .replace("上海终端区北部局地", "终端")
                .replace("上海终端区有分散", "终端")
                .replace("上海终端区东部有", "终端")
                .replace("上海终端区局地有", "终端")
                .replace("上海终端区局部有", "终端")
                .replace("终端区中南部局地", "终端")
                .replace("终端区中南部有", "终端")
                .replace("影响上海终端区", "终端")
                .replace("影响终端区东部", "终端")
                .replace("终端区中北部有", "终端")
                .replace("终端区有分散性", "终端")
                .replace("终端区有系统性", "终端")
                .replace("上海终端区有", "终端")
                .replace("上海终端区", "终端")
                .replace("影响终端区", "终端")
                .replace("终端区转为", "终端")
                .replace("终端区有", "终端")
                .replace("终端区", "终端")
                // 两场
                .replace("虹桥机场及五边和浦东机场及五边", "两场")
                .replace("虹桥机场及五边、浦东机场及五边", "两场")
                .replace("虹桥机场、浦东机场有间歇性", "两场")
                .replace("虹桥机场及浦东机场北京时间", "两场")
                .replace("虹桥机场和浦东机场期间有", "两场")
                .replace("虹桥机场和浦东机场五边", "两场")
                .replace("虹桥、浦东机场及五边，", "两场")
                .replace("虹桥和浦东机场及五边：", "两场")
                .replace("虹桥与浦东机场及周边", "两场")
                .replace("虹桥机场、浦东机场：", "两场")
                .replace("虹桥机场、浦东机场", "两场")
                .replace("虹桥机场及浦东机场", "两场")
                .replace("虹桥机场和浦东机场", "两场")
                .replace("浦东和虹桥机场", "两场")
                .replace("虹桥和浦东机场", "两场")
                .replace("浦东及虹桥机场", "两场")
                .replace("虹桥及浦东机场", "两场")
                .replace("虹桥、浦东机场", "两场")
                .replace("浦东、虹桥机场", "两场")
                .replace("两场短时分散性", "两场")
                .replace("上海两场上午有", "6:00-12:00两场")
                .replace("两场分散性", "两场")
                .replace("虹桥和浦东", "两场")
                .replace("浦东和虹桥", "两场")
                .replace("虹桥及浦东", "两场")
                .replace("浦东及虹桥", "两场")
                .replace("虹桥、浦东", "两场")
                .replace("上海两场有", "两场")
                .replace("上海两场：", "两场")
                .replace("上海两场", "两场")
                .replace("两场有", "两场")
                // 三航
                .replace("（NXD，AND，SASAN）", "三航A")
                .replace("（SASAN、NXD、AND）", "三航A")
                .replace("（NXD、SASAN、PIKAS）", "三航B")
                .replace("（PIKAS、SASAN、NXD）", "三航B")
                // 两航
                .replace("（NXD、AND）", "两航A")
                .replace("（AND、NXD）", "两航A")
                .replace("NXD、AND", "两航A")
                .replace("AND、NXD", "两航A")
                .replace("NXD和AND", "两航A")
                .replace("AND和NXD", "两航A")
                .replace("（SASAN、PIKAS）", "两航B")
                .replace("（PIKAS、SASAN）", "两航B")
                .replace("PIKAS、SASAN", "两航B")
                .replace("SASAN、PIKAS", "两航B")
                // 其它关键字
                .replace("0，浦东", "0浦东")
                .replace("0、浦东", "0浦东")
                // 单个机场（只能放在最后替换）
                .replace("浦东机场及五边有", "浦东")
                .replace("虹桥机场或五边", "虹桥")
                .replace("浦东机场或五边", "浦东")
                .replace("虹桥机场及周边", "虹桥")
                .replace("浦东机场及周边", "浦东")
                .replace("虹桥机场北五边", "虹桥")
                .replace("浦东机场北五边", "浦东")
                .replace("虹桥机场及五边", "虹桥")
                .replace("浦东机场及五边", "浦东")
                .replace("虹桥机场", "虹桥")
                .replace("浦东机场", "浦东")
                .replace("：", ":")
                ;
    }

    /**
     * 终端区的分割符
     **/
    public String terminalDelimiter(String areaContent){
        if (areaContent.contains("0终加两")) {
            areaContent = areaContent.replace("0终加两", "0终加两，");
        }else if(areaContent.contains("0终端")){
            areaContent = areaContent.replace("0终端", "0终端，");
        }
        return areaContent
                .replace("及以上", "以上")
                .replace("0有间歇性", "0，有间歇性")
                .replace("0短时", "0，短时")
                .replace("以及", "，")
                .replace("及", "，")
                .replace("。", "，")
                .replace("；", "，")
                .replace(",", "，")
                .replace("、", "，")
                ;
    }

    /**
     * 终端区的其它替换
     **/
    public String terminalOther(String areaContent){
        return areaContent
                .replace("有12m/s以上正侧风影响", "正侧风12以上")
                .replace("降水以小到中阵雨为主", "小到中阵雨")
                .replace("有对流天气", "对流")
                .replace("有间歇性", "")
                .replace("局地分散", "")
                .replace("时伴有", "伴")
                .replace("0有", "0")
                .replace("14阵风", "14，阵风")
                .replace("15阵风", "15，阵风")
                .replace("16阵风", "16，阵风")

                .replace("CB云", "CB")

                .replace("概率", "")
                .replace("%", "")
                ;
    }

    /**
     * 区域，预计
     **/
    public String expectedArea(String textContent){
        // 预计北京时间转换
        String replace = textContent
                // 其它
                .replace("受高空槽东移影响，预计后半夜到明日山东南部和东部、苏皖地区有东北-西南走向较强降水云带影响，隐嵌对流", "在山东南部和东部、苏皖地区有东北-西南走向较强降水云带有隐嵌对流")
                .replace("预计虹桥机场26日05:30能见度下降至600-800米，RVR短时350-550米；26日08:00以后", "预计虹桥机场26日05:30-26日08:00能见度下降至600-800米，RVR短时350-550米；26日08:00以后")
                .replace("目前合肥、宁波、杭州机场已出现了大雾天气，青岛、浦东机场出现了部分雾天气，预计南京、南昌机场也将出现大雾天气，预计上述机场目前-08:30前后维持大雾或部分雾天气，08:30-09:30大雾逐步消散", "预计北京时间目前-08:30肥波杭青浦京昌大雾或雾")
                .replace("云底高60-80米，24日20:00之后短时30-50米", "云底高60-80米，短时30-50米")
                .replace("预计20日白天雨带维持，夜间雨带南移并加强，21日晨在苏南浙北地区有较强对流发展", "白天雨带维持，夜间雨带南移并加强，次日晨在苏南浙北地区有较强对流发展")
                .replace("米克拉对福建、江西东南部有较大影响。11日主要影响航路：", "")
                .replace("其外围云系对浙江北部、上海、江苏南部有较大影响，主要影响航路：", "")
                .replace("对浙江北部、上海、江苏南部有较大影响，主要影响航路：", "")
                .replace("对浙江、上海、江苏东南部有较大影响，主要影响航路：", "")
                .replace("对浙江、上海地区有较大影响，主要影响航路：", "")
                .replace("台风“黑格比”5日05:00", "台风“黑格比”")
                .replace("台风“黑格比”4日21:00", "台风“黑格比”")
                .replace("台风“黑格比”4日14:00", "台风“黑格比”")
                .replace("台风黑格比4日03:30", "台风黑格比")
                // 1
                .replace("21:00中心位于", "中心位于")
                .replace("14:00中心位于", "中心位于")
                .replace("07:00中心位于", "中心位于")
                .replace("05:00中心位于", "中心位于")
                // 终端
                .replace("预计上海终端区目前到明晨受", "预计北京时间终端目前-06:00")
                .replace("预计影响上海终端区18", "预计北京时间终端18")
                .replace("预计上海终端区：25", "预计北京时间终端25")
                .replace("预计上海终端区", "预计北京时间终端")
                // 两场
                .replace("预计雷阵雨影响航站：虹桥和浦东", "预计北京时间两场雷阵雨")
                .replace("预计影响航站：上海两场", "预计北京时间两场")
                .replace("预计杭州、宁波机场", "预计北京时间杭宁")
                .replace("预计两场", "预计北京时间两场")
                // 多机场
                .replace("预计影响航站：南昌、温州（短时雷雨）、虹桥、浦东、杭州、宁波（短时大阵雨）", "预计北京时间目前-18:00昌温雷雨，杭波虹浦大阵雨")
                .replace("预计影响航站：南昌、杭州、虹桥、浦东（短时雷雨）", "预计北京时间目前-18:00昌杭虹浦雷雨")
                .replace("预计影响航站：合肥、南京、虹桥、浦东", "预计北京时间目前-18:00肥京虹浦")
                .replace("预计后半夜到明日上午影响航站：南昌、杭州、虹桥、浦东、宁波，上述机场将出现短时雷阵雨或中到大阵雨", "预计北京时间23:59-06:00昌杭虹浦波雷阵雨或中到大阵雨")
                .replace("预计影响航站：29日夜间虹桥、浦东、杭州、宁波、南昌、福州、厦门有阵雨伴CB或雷雨天气", "预计北京时间30日00:00-06:00虹浦杭波昌福厦阵雨伴CB或雷雨")
                .replace("主要影响航站：青岛（中到大雨，短时雷暴）、虹桥、浦东、南京（分散对流）", "预计北京时间目前-00:00青岛机场中到大雨，雷暴，预计北京时间虹浦京对流")
                .replace("主要影响航站：青岛（小到中阵雨，短时强阵风和风切变）", "预计北京时间目前-00:00青岛机场小到中阵雨，强阵风和风切变")
                .replace("预计影响航站：虹桥、浦东、合肥、南京、杭州、宁波，上述航站间接性雷雨或中到大阵雨", "预计北京时间虹浦肥京杭波雷雨或中到大阵雨")
                .replace("预计5日杭州、上海两场、宁波、温州、福州、厦门将先后出现阵雨或雷雨天气", "预计北京时间5日06:00-18:00虹浦杭波温福厦阵雨或雷雨")
                .replace("预计5日白天合肥、南京、杭州、宁波、温州、福州、厦门将先后出现雷阵雨天气", "预计北京时间5日06:00-18:00肥京杭波温福厦雷阵雨")
                .replace("预计南京、青岛、南昌、杭州短时有雷阵雨天气", "预计北京时间南京机场雷阵雨，青岛机场雷阵雨，杭州机场雷阵雨，南昌机场雷阵雨")
                .replace("预计夜间到明日影响航站：合肥、南京，上述机场将出现中到大阵雨短时伴有雷暴", "预计北京时间肥京23:59-06:00中到大阵雨或雷暴")
                .replace("预计8日济南、青岛、合肥、南京、虹桥、浦东先后出现阵雨或雷雨天气", "预计北京时间8日06:00-18:00济青肥京虹浦阵雨或雷雨")
                .replace("预计14日合肥、南京、杭州、虹桥、浦东先后出现阵雨或雷雨天气", "预计北京时间14日06:00-18:00肥京杭虹浦阵雨或雷雨")
                .replace("预计影响航站：合肥、南京，上述机场将出现中到大阵雨短时伴有雷暴", "预计北京时间06:00-18:00肥京中到大阵雨或雷暴")
                .replace("；杭州、宁波、虹桥、浦东等机场4日也将有大风及短时强降水影响", "预计北京时间4日06:00-18:00杭波虹浦大风及强降水")
                .replace("预计8日青岛、合肥、南京、虹桥、浦东先后出现阵雨或雷雨天气", "预计北京时间8日06:00-18:00青肥京虹浦阵雨或雷雨")
                .replace("主要影响航站：青岛（小到中阵雨，短时强阵风和风切变）", "预计北京时间青岛机场小到中阵雨，短时强阵风和风切变")
                .replace("主要影响航站：杭州、虹桥、浦东将有大风及短时强降水影响", "预计北京时间杭虹浦大风及强降水")
                .replace("预计杭州、宁波、温州将出现阵雨或雷雨天气", "预计北京时间06:00-18:00杭波温阵雨或雷雨")
                .replace("预计半夜到明晨影响航站：合肥、南京，短时雷阵雨", "预计北京时间23:59-06:00肥京雷阵雨")
                .replace("南京、杭州、温州、南昌机场目前已出现大雾天气，预计14日", "预计北京时间京杭温昌14日")
                .replace("预计影响航站：合肥、南京，间歇性雷阵雨", "预计北京时间肥京06:00-18:00雷阵雨")
                .replace("南昌、杭州、温州、宁波、虹桥、浦东", "昌杭温波虹浦")
                .replace("；影响航站：合肥、南京、青岛", "肥京青")
                .replace("主要影响航站：厦门、福州", "厦福")
                // 虹桥机场
                .replace("虹桥机场已出现部分雾，预计", "预计北京时间虹桥机场")
                .replace("。预计虹桥机场于23", "预计北京时间虹桥机场23")
                .replace("预计影响航站：虹桥", "预计北京时间虹桥机场")
                .replace("；预计虹桥12", "预计北京时间虹桥机场12")
                .replace("，虹桥29", "预计北京时间虹桥机场29")
                .replace("，虹桥25", "预计北京时间虹桥机场25")
                .replace("、虹桥18", "预计北京时间虹桥机场18")
                .replace("；虹桥16", "预计北京时间虹桥机场16")
                .replace("；虹桥15", "预计北京时间虹桥机场15")
                .replace("，虹桥14", "预计北京时间虹桥机场14")
                // 浦东机场
                .replace("主要影响航站：浦东将有短时强降水影响", "预计北京时间浦东机场强降水")
                .replace("浦东机场已出现50-90米的低云天气，预计", "预计北京时间浦东机场")
                .replace("受辐射雾影响，浦东机场", "受辐射雾影响，预计北京时间浦东机场")
                .replace("预计浦东机场云底高于北京时间", "预计北京时间浦东机场云底高")
                .replace("，浦东机场于22", "预计北京时间浦东机场22")
                .replace("，浦东29", "预计北京时间浦东机场29")
                .replace("、浦东25", "预计北京时间浦东机场25")
                .replace("、浦东20", "预计北京时间浦东机场20")
                .replace("、浦东19", "预计北京时间浦东机场19")
                .replace("、浦东16", "预计北京时间浦东机场16")
                .replace("、浦东15", "预计北京时间浦东机场15")
                .replace("，浦东15", "预计北京时间浦东机场15")
                .replace("，浦东12", "预计北京时间浦东机场12")
                // 南京机场
                .replace("南京机场已出现能见度100米、RVR125米的冻雾天气，预计", "预计北京时间南京机场")
                .replace("南京机场目前能见度和RVR低于200米，预计", "预计北京时间南京机场")
                .replace("其中南京机场", "预计北京时间南京机场")
                .replace("，南京29", "预计北京时间南京机场29")
                .replace("、南京25", "预计北京时间南京机场25")
                .replace("，南京21", "预计北京时间南京机场21")
                .replace("、南京14", "预计北京时间南京机场14")
                .replace("、南京12", "预计北京时间南京机场12")
                .replace("，南京08", "预计北京时间南京机场08")
                .replace("，南京6", "预计北京时间南京机场06")
                // 杭州机场
                .replace("杭州机场目前RVR在400-1000米波动，预计13", "预计北京时间杭州机场13")
                .replace("预计影响航站：杭州机场15", "预计北京时间杭州机场15")
                .replace("预计影响航站：杭州25", "预计北京时间杭州机场25")
                .replace("预计影响航站：杭州22", "预计北京时间杭州机场22")
                .replace("，杭州29", "预计北京时间杭州机场29")
                .replace("、杭州25", "预计北京时间杭州机场25")
                .replace("，杭州21", "预计北京时间杭州机场21")
                .replace("、杭州20", "预计北京时间杭州机场20")
                .replace("，杭州16", "预计北京时间杭州机场16")
                .replace("，杭州14", "预计北京时间杭州机场14")
                .replace("，杭州12", "预计北京时间杭州机场12")
                // 合肥机场

                .replace("合肥机场目前RVR低于550米，预计", "预计北京时间合肥机场")
                .replace("合肥机场预计", "预计北京时间合肥机场")
                .replace("预计影响航站：合肥29", "预计北京时间合肥机场29")
                .replace("，合肥21", "预计北京时间合肥机场21")
                .replace("预计影响航站：合肥19", "预计北京时间合肥机场19")
                .replace("预计影响航站：合肥13", "预计北京时间合肥机场13")
                .replace("预计影响航站：合肥12", "预计北京时间合肥机场12")
                .replace("预计影响航站：合肥10", "预计北京时间合肥机场10")
                .replace("预计影响航站：合肥07", "预计北京时间合肥机场07")
                .replace("预计影响航站：合肥6", "预计北京时间合肥机场06")
                .replace("；合肥机场15", "预计北京时间合肥机场15")
                // 南昌机场
                .replace("预计影响航站：南昌08", "预计北京时间南昌机场08")
                .replace("预计影响航站：南昌06", "预计北京时间南昌机场06")
                .replace("、南昌机场21", "预计北京时间南昌机场21")
                .replace("、南昌21", "预计北京时间南昌机场21")
                .replace("，南昌21", "预计北京时间南昌机场21")
                .replace("、南昌18", "预计北京时间南昌机场18")
                .replace("，南昌07", "预计北京时间南昌机场07")
                // 宁波机场
                .replace("、宁波机场21", "预计北京时间宁波机场21")
                .replace("，宁波22", "预计北京时间宁波机场22")
                // 温州机场
                .replace("主要影响航站：温州4", "预计北京时间温州机场04")
                .replace("主要影响航站：温州3", "预计北京时间温州机场03")
                .replace("、温州机场21", "预计北京时间温州机场21")
                // 青岛机场
                .replace("；青岛机场15", "预计北京时间青岛机场15")
                .replace("，青岛17", "预计北京时间青岛机场17")
                // 济南机场
                .replace("，济南机场14", "预计北京时间济南机场14")
                .replace("，济南20", "预计北京时间济南机场20")
                // 厦门

                // 福州



                .replace("预计影响上海终端区西北部和北部（SASAN、PIKAS）：", "预计北京时间SASAN、PIKAS")

                .replace("上述机场将出现停场航空器、跑道", "预计北京时间上述机场")
                .replace("以上机场将出现停场航空器、跑道", "预计北京时间上述机场")
                .replace("预计上述机场目前", "预计北京时间上述机场目前")
                .replace("上述机场将出现", "预计北京时间上述机场")
                // 航路
                .replace("0江西中北部、浙江西部有分散对流云团发展，影响航路：A599", "0A599")
                .replace("预计航路25日18:30-26日05:00：", "预计北京时间25日18:30-26日05:00")
                .replace("预计目前到明晨影响航路：", "预计北京时间目前-06:00")
                .replace("预计影响航路：A599", "预计北京时间A599")
                .replace("预计影响航路：B208", "预计北京时间B208")
                .replace("影响航路：B208", "预计北京时间B208")
                .replace("11日主要影响航路：", "预计北京时间11日")

                .replace("预计影响京沪航路", "预计北京时间京沪航路")
                .replace("、沪广航路", "预计北京时间沪广航路")
                .replace("主要影响航路：", "预计北京时间")
                .replace("预计影响航路：", "预计北京时间")
                .replace("预计航路", "预计北京时间")
                .replace("对流自西向东影响航路：", "")
                .replace("影响航路：", "")
                .replace("影响航路", "")

                .replace("，云和-福清", "，A470云和-福清")
                .replace("，东山-连江", "预计北京时间B221东山-连江")
                .replace("，桐庐-蟠龙", "预计北京时间A599桐庐-蟠龙")
                .replace("，桐庐-DAGMO", "预计北京时间A470桐庐-DAGMO")
                .replace("，景德镇-青州", "预计北京时间H2景德镇-青州")
                .replace("、A599", "预计北京时间A599")
                .replace("、A593", "预计北京时间A593")
                .replace("、A470", "预计北京时间A470")
                .replace("、B221", "预计北京时间B221")
                .replace("；B221", "预计北京时间B221")
                .replace("，B208", "预计北京时间B208")
                .replace("、R343", "预计北京时间R343")
                .replace("、H24", "预计北京时间H24")
                .replace("、H22", "预计北京时间H22")
                .replace("、H2", "预计北京时间H2")
                .replace("，W127", "预计北京时间W127")
                .replace("、W19", "预计北京时间W19")
                .replace("，W13", "预计北京时间W13")
                // 航站
                .replace("预计今夜至明晨将影响航站：", "预计北京时间23:59-06:00")
                .replace("预计雷阵雨影响航站：", "预计北京时间航站雷阵雨")
                .replace("上述航站间接性", "预计北京时间上述航站")
                .replace("上述航站短时", "预计北京时间上述航站")
                .replace("主要影响航站：", "预计北京时间")
                .replace("预计影响航站：", "预计北京时间")
                .replace("影响航站：", "预计北京时间")
                // 时间
                .replace("预计受辐射降温影响，", "预计北京时间")
                .replace("预计于北京时间", "预计北京时间")
                .replace("主要影响机场：", "预计北京时间")
                // 预计机场
                .replace("预计南京、南昌机场也将出现大雾天气", "预计北京时间南京机场、南昌机场大雾")
                .replace("受部分雾影响，预计虹桥机场", "预计北京时间虹桥机场")
                .replace("预计浦东机场北京时间", "预计北京时间浦东机场")
                .replace("预计浦东机场", "预计北京时间浦东机场")
                .replace("预计虹桥机场", "预计北京时间虹桥机场")
                .replace("预计杭州机场", "预计北京时间杭州机场")
                .replace("预计南京机场", "预计北京时间南京机场")
                .replace("预计合肥机场", "预计北京时间合肥机场")
                .replace("预计南京", "预计北京时间南京机场")
                .replace("预计杭州", "预计北京时间杭州机场")
                // 机场预计
                .replace("虹桥机场预计", "预计北京时间虹桥机场")
                .replace("杭州机场预计", "预计北京时间杭州机场")
                // 南方机场
                .replace("合肥、南京，短时", "合肥机场、南京机场")
                .replace("浦东机场有", "浦东机场")
                // 1

                ;
        // 合肥31日02:00-08:00小到中雪或大雪；南京31日05:00-09:00雨夹雪转中雪；青岛30日18:00-31日03:00雨夹雪转小到中雪
        replace = replace
                .replace("合肥31日", "预计北京时间合肥机场31日")
                .replace("南京31日", "预计北京时间南京机场31日")
                .replace("青岛30日", "预计北京时间青岛机场30日")
        ;
        return expectedCommon(replace);
    }

    /**
     * 区域，时间
     **/
    public String timeArea(String areaContent){
        String replace = areaContent.replace("预计未来", "未来");
        String keyword = "未来24小时";
        long hour = 24L;
        // 发布时间
        Date startTime = Java8DateUtils.parseDateTimeStr(ymdhmsThreadLocal.get(), Java8DateUtils.DATE_TIME);
        String start = Java8DateUtils.format(startTime, Java8DateUtils.DAY_HOUR_MINUTE);
        String other1 = "未来12小时";
        String other2 = "未来6小时";
        if (replace.contains(other1)) {
            keyword = other1;
            hour = 12L;
        }else if (replace.contains(other2)) {
            keyword = other2;
            hour = 6L;
        }
        Date endTime = Java8DateUtils.addHour(startTime, hour);
        String end = Java8DateUtils.format(endTime, Java8DateUtils.DAY_HOUR_MINUTE);
        return replace.replace(keyword, "预计北京时间" + start + "-" + end);
    }

    /**
     * 区域，删除多余内容
     **/
    public String deleteArea(String content){
        return content
                // 目前浦东平流条件减弱，但近海湿度仍较大，
                // 预计北京时间浦东目前-25日03:00重要截取 RVR300-550米；
                // 预计北京时间浦东25日03:00-09:00重要截取 大雾、能见度小于800米、RVR300-550米，
                // 预计北京时间浦东25日06:00-08:00重要截取 能见度300-500、RVR150-300。

                .replace("江西中北部、浙江西部有分散对流云团发展", "对流云团")
                .replace("有分散对流", "对流")
                .replace("大雾天气", "大雾")
                .replace("雾天气", "雾")

                .replace("东跑道视程（RVR）短时有", "RVR")
                .replace("东跑道视程（RVR）目前至", "RVR目前-")
                .replace("所有跑道", "")
                .replace("东跑道", "")

//                .replace("浦东机场11日03:30-08:30出现RVR350-550米，", "浦东已经出现RVR350-550米，")
                .replace("南京机场目前能见度和RVR低于200米，", "南京")
                .replace("合肥机场目前RVR低于550米，", "合肥")

                .replace("有短时波动（范围", "为")
                .replace("好转至", "为")

                .replace("沿海跑道", "")
                .replace("的波动", "")

                .replace("能见度50米，RVR150米", "能见度50米、RVR150米")

                .replace("预计山东、苏北、安徽西北部将出现雨夹雪或雪", "")
                .replace("预计白天到上半夜影响京沪方向各主要航路。", "")
                .replace("预计影响上海终端区：", "")

                .replace("将出现停场航空器", "")
                .replace("机场出现了部分", "")
                .replace("机场也将出现", "")
                .replace("将先后出现", "")
                .replace("将有短时", "")
                .replace("将有", "")

                .replace("华东中部航段", "")
                .replace("方向有分散", "")
                .replace("雨量中到大", "")


                .replace("前后维持", "")
                .replace("维持在", "")
                .replace("维持", "")
                .replace("间歇性", "")
                .replace("主导", "")


                .replace("等机场4日也将有", "4日00:00-23:59")

                .replace("自北向南有", "")
                .replace("自西向东有", "")
                .replace("短时有", "")
                .replace("有部分", "")

                // 夜间时间
                .replace("夜间影响航路：A599", "23:59-06:00A599")
                .replace("后半夜到明晨短时", "23:59-06:00")
                .replace("目前到明晨", "目前-06:00")
                .replace("夜间", "20:00-06:00")
                // 白天时间
                .replace("白天局部有", "08:00-18:00")
                .replace("白天", "08:00-18:00")
                // 之前时间
                .replace("11日08:30之前", "目前-11日08:30")
                .replace("24日22:00前", "目前-24日22:00")
                .replace("20:00前有", "目前-20:00")
                .replace("09:00前", "目前-09:00")

                // 之后时间
                .replace("0时之后，RVR", "0-00:00RVR")
                .replace("0之后，两场", "0-00:00两场")
                .replace("0以后有", "0-00:00")
                .replace("0以后", "0-00:00")
                .replace("0之后", "0-00:00")
                .replace("0后", "0-00:00")
                .replace("0起", "0-00:00")
                // 时间
                .replace("24:00", "00:00")
                // 1
                .replace("0至0", "0-0")
                .replace("0至1", "0-1")

                .replace("、29日", "，29日")
                .replace("0机场", "0")
                .replace("0有", "0")

                .replace("：R", "R")
                .replace("等", "")
                ;
    }

    /**
     * 区域，区域
     **/
    public String areaArea(String areaContent) {
        return areaContent
                .replace("影响上海终端区南部", "终端")
                .replace("终端区西部和北部", "终端")
                .replace("上海终端区北部有", "终端")
                .replace("影响上海终端区", "终端")
                .replace("上海终端区有", "终端")
                .replace("上海终端区：", "终端")
                .replace("上海终端区", "终端")

                .replace("虹桥机场和浦东机场：", "两场")
                .replace("虹桥机场和浦东机场", "两场")
                .replace("虹桥、浦东机场", "两场")
                .replace("虹桥、浦东短时", "两场")
                .replace("虹桥、浦东：", "两场")
                .replace("虹桥、浦东", "两场")
                .replace("虹桥和浦东", "两场")

                .replace("上海两场机场", "两场")
                .replace("上海两场短时", "两场")
                .replace("上海两场：", "两场")
                .replace("上海两场", "两场")
                .replace("两场短时", "两场")

                .replace("PIKAS、SASAN", "两航B")
                .replace("SASAN、PIKAS", "两航B")

                .replace("0南京短时", "0南京机场")
                .replace("南京：", "南京机场")
                .replace("杭州：", "杭州机场")
                .replace("合肥：", "合肥机场")
                .replace("南昌：", "南昌机场")
                .replace("宁波：", "宁波机场")
                ;
    }

    /**
     * 区域，分割符
     **/
    public String delimiterArea(String areaContent){
        return areaContent
                .replace("系统性", "")
                .replace("其中", "")
                .replace("另外", "")
                .replace("（）", "")
                .replace("米", "")
                .replace("。", "，")
                .replace("；", "，")
                .replace(",", "，")
//                .replace("、", "，")
                ;
    }

    public String addTextContent(String splitOne, String original){
        if (StringUtils.isBlank(splitOne)) {
            if (!original.contains("在") || !original.contains("有")) {
                List<String> address = Lists.newArrayList();
                address.add("虹桥机场");
                splitOne = "在" + StringUtil.getKeyWord(original, address) + "有低云，";
            }
        }else{
            splitOne = splitOne.replace("并伴有颠簸和风切变", "");
        }
        return splitOne;
    }

    // 目前，...
    public String currently(String textContent){
        String substring = textContent.substring(0, 3);
        if (substring.contains("目前，")) {
            textContent = textContent.substring(3);
        }else if (substring.contains("目前")){
            textContent = textContent.substring(2);
        }
        return textContent;
    }

    // 受...影响，...
    public String influences(String textContent){
        if (textContent.contains("受") && textContent.contains("影响，")) {
            String[] split = textContent.split("影响，");
            textContent = split[1];
        }
        return textContent;
    }

    // "在"字转换
    public String in(String textContent){
        String substring1 = textContent.substring(0, 1);
        if (!substring1.contains("在")) {
            textContent = "在" + textContent;
        }
        return textContent;
    }

    // 伴随
    public String Accompanying1(String textContent){
        // 有"伴"字
        if (textContent.contains("伴")) {
            // 替换"伴"关键字
            textContent = textContent
                    .replace("期间还将伴", "伴")
                    .replace("短时伴", "伴")
                    .replace("或伴有", "伴")
                    .replace("并伴随", "伴")
                    .replace("并伴有", "伴")
                    .replace("并伴", "伴")
                    .replace("伴随", "伴")
                    .replace("伴有", "伴")
                    .replace("，伴", "伴")
            ;
            // 替换"其它"关键字
            textContent = textContent
                    .replace("0本场短时", "0影响机场，期间机场将出现")
                    .replace("0，机场出现", "0影响机场，期间机场将出现")
                    .replace("0，虹桥机场有", "0影响机场，期间机场将出现")
                    .replace("0虹桥机场将出现", "0影响机场，期间机场将出现")
                    .replace("0影响虹桥机场，期间机场", "0影响机场，期间机场将出现")
                    .replace("0影响机场，届时机场出现", "0影响机场，期间机场将出现")
                    .replace("0影响机场，期间机场维持", "0影响机场，期间机场将出现")
                    .replace("0影响机场，期间机场还将出现", "0影响机场，期间机场将出现")
                    .replace("0影响机场及五边，期间机场有", "0影响机场，期间机场将出现")
                    .replace("雨带影响机场及五边，期间机场有", "影响机场，期间机场将出现")
                    .replace("0影响机场，期间本场及五边将出现", "0影响机场，期间机场将出现")
                    .replace("0影响机场，期间机场及五边将出现", "0影响机场，期间机场将出现")
                    .replace("0影响机场，期间机场及北五边将出现", "0影响机场，期间机场将出现")
                    .replace("0影响机场及五边，期间机场还将出现", "0影响机场，期间机场将出现")
                    .replace("0影响机场及五边，期间机场及五边将出现", "0影响机场，期间机场将出现")
                    .replace("0影响虹桥机场及五边，虹桥机场及五边将出现", "0影响机场，期间机场将出现")
                    .replace("0影响机场及五边区域，期间机场及五边将出现", "0影响机场，期间机场将出现")

                    .replace("0本场或五边短时出现", "0影响机场，期间机场将出现短时")
                    .replace("0影响机场，期间机场短时有", "0影响机场，期间机场将出现短时")
                    .replace("0影响机场及五边，期间机场维持", "0影响机场，期间机场将出现短时")
                    .replace("0机场及五边多分散性对流发展，期间机场短时", "0影响机场，期间机场将出现短时")
                    .replace("0影响机场及五边，期间机场及五边还将短时出现", "0影响机场，期间机场将出现短时")
            ;
//            String[] split = textContent.split("伴");
//            if (split.length>2) {
//                // 若存在多个"伴"，删除后面那个
//                textContent = split[0] + "伴" + split[1] + split[2];
//            }else if(split.length>1){
//                // 若"伴"后面有"期间机场还将出现"关键词，则删除这些关键字。
//                textContent = split[0] + "伴" + split[1].replace("期间机场还将出现", "");
//            }
        }
        // 无"伴"字
        if (!textContent.contains("伴")) {
            if (textContent.contains("机场将出现") && textContent.contains("还将出现")
                    || textContent.contains("机场及五边有") && textContent.contains("还将出现")
                    || textContent.contains("维持") && textContent.contains("还将出现")
                    || textContent.contains("虹桥机场东南") && textContent.contains("期间")
                    || textContent.contains("虹桥机场偏南") && textContent.contains("期间")) {
                // 有"伴"意，替换"伴"关键字
                textContent = textContent
                        .replace("期间机场进近区域还将出现", "伴")
                        .replace("期间机场还将出现", "伴")
                        .replace("期间跑道还将出现", "伴")
                        .replace("期间还将出现", "伴")
                        .replace("期间进近区域", "伴")
                        .replace("期间虹桥机场及五边短时有", "伴短时")
                ;
                // 有"伴"意，替换"其它"关键字
                textContent = textContent
                        .replace("0维持", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场", "0影响机场，期间机场将出现")
                        .replace("0本场仍有", "0影响机场，期间机场将出现")
                        .replace("0机场将出现", "0影响机场，期间机场将出现")
                        .replace("0机场将维持", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场将出现", "0影响机场，期间机场将出现")
                        .replace("0本场及五边维持", "0影响机场，期间机场将出现")
                        .replace("0，虹桥机场及五边有", "0影响机场，期间机场将出现")
                ;
            }else{
                // 无"伴"意，替换其它关键字
                textContent = textContent
                        .replace("0以后转", "0-00:00影响机场，期间机场将出现")
                        .replace("0本场仍有", "0影响机场，期间机场将出现")
                        .replace("0仍将出现", "0影响机场，期间机场将出现")
                        .replace("0机场五边", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场有", "0影响机场，期间机场将出现")
                        .replace("0，机场出现", "0影响机场，期间机场将出现")
                        .replace("0机场将出现", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场将出现", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场仍将出现", "0影响机场，期间机场将出现")
                        .replace("0，虹桥机场及五边有", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场及五边仍有", "0影响机场，期间机场将出现")
                        .replace("0机场进近区域将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场，机场还将出现", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场跑道进近区域将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场，期间机场还将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场及五边，期间将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场及五边，期间还将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场，期间本场及五边将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场，期间机场及五边将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场，期间机场及五边还将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场北五边，期间机场还将出现", "0影响机场，期间机场将出现")
                        .replace("0影响虹桥机场及五边，期间机场将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场及五边，期间机场及五边将出现", "0影响机场，期间机场将出现")
                        .replace("0影响机场北五边，期间机场北五边还将出现", "0影响机场，期间机场将出现")
                        .replace("0本场及五边维持雷暴天气，期间机场还将出现", "0影响机场，期间机场将出现")
                        .replace("0影响虹桥机场及五边，期间机场及五边将出现", "0影响机场，期间机场将出现")

                        .replace("0机场进近区域将短时出现", "0影响机场，期间机场将出现短时")

                        .replace("0以后RVR", "0-00:00影响机场，期间机场将出现RVR")
                        .replace("0能见度", "0影响机场，期间机场将出现能见度")
                        .replace("0，虹桥机场能见度", "0影响机场，期间机场将出现能见度")

                        .replace("0云底高", "0影响机场，期间机场将出现云底高")
                        .replace("0机场云底高", "0影响机场，期间机场将出现云底高")
                        .replace("0虹桥机场云底高", "0影响机场，期间机场将出现云底高")

                        .replace("0将转为", "0影响机场，期间机场将出现")
                        .replace("0将再次转为", "0影响机场，期间机场将出现")

                        .replace("0虹桥机场维持", "0影响机场，期间机场将出现")
                        .replace("0虹桥机场及五边将出现", "0影响机场，期间机场将出现")

                        .replace("0，虹桥机场小", "0影响机场，期间机场将出现小")
                ;
            }
        }
        return textContent;
    }

    // 机场重要天气
    public String importantWeatherForAirport(String textContent){
        return textContent
                .replace("0影响机场及五边，期间机场及五边还将短时出现", "0重要截取短时")
                .replace("0影响虹桥机场及五边，虹桥机场及五边将出现", "0重要截取")
                .replace("0影响机场及五边区域，期间机场及五边将出现", "0重要截取")
                .replace("0影响虹桥机场及五边，期间机场及五边将出现", "0重要截取")
                .replace("0影响机场北五边，期间机场北五边还将出现", "0重要截取")
                .replace("0影响机场及五边，期间机场及五边将出现", "0重要截取")
                .replace("0影响机场五边，期间机场及五边将出现", "0重要截取")
                .replace("0影响虹桥机场及五边，期间机场将出现", "0重要截取")
                .replace("0影响机场北部地区，期间机场将出现", "0重要截取")
                .replace("0影响机场及五边，期间机场还将出现", "0重要截取")
                .replace("0影响机场北五边，期间机场还将出现", "0重要截取")
                .replace("0影响机场，期间机场及北五边将出现", "0重要截取")
                .replace("0影响机场，期间机场及五边还将出现", "0重要截取")
                .replace("0影响机场，期间机场及五边将出现", "0重要截取")
                .replace("0影响机场，期间本场及五边将出现", "0重要截取")
                .replace("0影响机场及五边，期间机场将出现", "0重要截取")
                .replace("0影响机场或五边，期间机场将出现", "0重要截取")
                .replace("0影响虹桥机场，期间机场将出现", "0重要截取")
                .replace("0影响机场及五边，期间机场维持", "0重要截取")
                .replace("0影响机场及五边，期间还将出现", "0重要截取")
                .replace("雨带影响机场及五边，期间机场有", "重要截取")
                .replace("0影响机场，期间机场短时有", "0重要截取短时")
                .replace("0影响机场及五边，期间机场有", "0重要截取")
                .replace("0影响机场及五边，期间将出现", "0重要截取")
                .replace("0影响机场，期间机场还将出现", "0重要截取")
                .replace("0影响机场，期间机场将出现", "0重要截取")
                .replace("0影响机场，届时机场出现", "0重要截取")
                .replace("0影响机场，机场还将出现", "0重要截取")
                .replace("0影响机场，期间机场维持", "0重要截取")
                .replace("0影响虹桥机场，期间机场", "0重要截取")

                .replace("0机场及五边多分散性对流发展，期间机场短时", "0重要截取短时")
                .replace("0机场五边多分散性对流发展", "0重要截取对流")
                .replace("0机场进近区域将短时出现", "0重要截取短时")
                .replace("0机场进近区域将出现", "0重要截取")
                .replace("0机场将出现", "0重要截取")
                .replace("0机场将维持", "0重要截取")
                .replace("0机场五边", "0重要截取")
                .replace("0机场", "0重要截取")

                .replace("0本场及五边维持雷暴天气，期间机场还将出现", "0重要截取")
                .replace("0本场或五边短时出现", "0重要截取短时")
                .replace("0本场及五边维持", "0重要截取")
                .replace("0本场仍有", "0重要截取")
                .replace("0本场", "0重要截取")

                .replace("0虹桥机场跑道进近区域将出现", "0重要截取")
                .replace("0虹桥机场及五边将出现", "0重要截取")
                .replace("0虹桥机场及五边仍有", "0重要截取")
                .replace("0虹桥机场仍将出现", "0重要截取")
                .replace("0虹桥机场将出现", "0重要截取")
                .replace("0虹桥机场维持", "0重要截取")
                .replace("0虹桥机场有", "0重要截取")
                .replace("0虹桥机场", "0重要截取")

                .replace("0，虹桥机场及五边有", "0重要截取")
                .replace("0，虹桥机场有", "0重要截取")
                .replace("0，虹桥机场", "0重要截取")
                .replace("0，机场出现", "0重要截取")

                .replace("0 短时出现", "0重要截取短时")

                .replace("0能见度", "0重要截取能见度")
                .replace("0云底高", "0重要截取云底高")

                .replace("0短时", "0重要截取短时")

                .replace("0将再次转为", "0重要截取")
                .replace("0仍将出现", "0重要截取")
                .replace("0将转为", "0重要截取")
                .replace("0维持", "0重要截取")
                .replace("0出现", "0重要截取")

                .replace("0以后转", "0-00:00重要截取")
                .replace("0以后", "0-00:00重要截取")

                .replace("0。", "0重要截取")
                .replace("0，", "0重要截取")
                .replace("0 ", "0重要截取")

                ;
    }

    // 终端重要天气
    public String importantWeatherForTerminal(String textContent){
        if (!textContent.substring(textContent.length()-1).contains("。")) {
            textContent = textContent.concat("。");
        }
        String[] split = textContent.split("预计北京时间");
        String one = split[0];
        String two = split[1];
        Matcher matcher = compile(EXPRESSION).matcher(two);
        if (matcher.matches()) {
            String str = "重要截取";
            String group = matcher.group(9);
            if (group.contains(str)) {
                return textContent;
            }else{
                return one + "预计北京时间" + two.replace(group, str + group);
            }
        }
        return textContent;
    }

    // 区域重要天气
    public String importantWeatherForArea(String textContent){
        if (!textContent.substring(textContent.length()-1).contains("。")) {
            textContent = textContent.concat("。");
        }
        String spl = "预计北京时间";
        String[] split = textContent.split(spl);
        String one = split[0];
        String two = split[1];
        Matcher matcher = compile(EXPRESSION).matcher(two);
        if (matcher.matches()) {
            String str = "重要截取";
            String group = matcher.group(9);
            if (group.contains(str)) {
                return textContent;
            }else{
                return one + spl + two.replace(group, str + group);
            }
        }
        return textContent;
    }

    // 重要天气
    public String importantWeather(String textContent){
        String alertTypeStr = alertType.get();
        if("1".equals(alertTypeStr)){
            return importantWeatherForAirport(textContent);
        }else if("2".equals(alertTypeStr)){
            return importantWeatherForTerminal(textContent);
        }else if("3".equals(alertTypeStr)){
            return importantWeatherForArea(textContent);
        }
        return textContent;
    }

    // 伴随
    public String accompanying(String textContent){
        textContent = textContent
                .replace("期间虹桥机场及五边短时有", "伴短时")
                .replace("届时虹桥机场及五边还将出现", "伴")
                .replace("期间机场进近区域还将出现", "伴")
                .replace("期间机场还将出现", "伴")
                .replace("期间跑道还将出现", "伴")
                .replace("期间还将出现", "伴")
                .replace("期间进近区域", "伴")
                .replace("期间还将伴", "伴")
                .replace("短时伴", "伴")
                .replace("或伴有", "伴")
                .replace("并伴随", "伴")
                .replace("并伴有", "伴")
                .replace("并伴", "伴")
                .replace("伴随", "伴")
                .replace("伴有", "伴")
                .replace("，伴", "伴")
        ;
        return textContent;
    }

    // "有"字转换
    public String have(String textContent){
        // 1有、存在、已出现等关键字转换（只能放在"伴"后面）
        textContent = textContent
                .replace("机场有短时有", "机场短时")
                .replace("已经出现", "有")
                .replace("已出现", "有")
                .replace("，出现", "有")
                .replace("本场有", "本场")
                .replace("短时有", "短时")
                .replace("有持续", "持续")
                .replace("目前有", "有")
                .replace("仍有", "有")
                .replace("内有", "有")
        ;
        // 2有、存在、已出现等关键字（只能放在"伴"后面）
        if (!textContent.contains("有")) {
            if(textContent.contains("存在")){
                textContent = textContent.replace("存在", "有");
            }else{
                if (!textContent.contains("已出现")) {
                    if (textContent.contains("在虹桥机场")) {
                        textContent = textContent.replace("在虹桥机场", "在虹桥机场有");
                    }
                }
            }
        }
        // 3有，天气转换2（只能放在"伴"后面）
        if (textContent.contains("有")) {
            if (!textContent.contains("顶高")) {
                textContent = textContent.replace("预计北京时间", "顶高，预计北京时间");
            }
        }
        return textContent;
    }

    /**
     * 获取具有可读性的文本内容
     **/
    public String getReadableTextContent(String textContent){
        // 目前，...
        textContent = currently(textContent);
        // 受...影响，...
        textContent = influences(textContent);
        // "在"字转换
        textContent = in(textContent);
        // 重要天气
        textContent = importantWeather(textContent);
        // 跟随天气
        textContent = accompanying(textContent);
        // "有"字转换
        textContent = have(textContent);


        // 尾部替换
        textContent = textContent
                .replace("天气。", "。")
                .replace("天气，短时", "，短时")
                .replace("，届时解除。", "。")
                .replace("。届时解除该份机场警报。", "。")
        ;
        // 天气转换1（只能放在"伴"后面）
        if (textContent.contains("已出现")) {
            if (!textContent.contains("天气")) {
                textContent = textContent
                        .replace("已出现大雨，", "已出现大雨天气，")
                        .replace("已出现雷雨，", "已出现雷雨天气，")
                        .replace("已出现雷暴，", "已出现雷暴天气，")
                        .replace("已出现阵雨，", "已出现阵雨天气，")
                        .replace("已出现中阵雨，", "已出现中阵雨天气，")
                        .replace("已出现大阵雨，", "已出现大阵雨天气，")
                        .replace("已出现较强雷阵雨，", "已出现较强雷阵雨天气，")

                        .replace("已出现风切变，", "已出现风切变天气，")
                        .replace("已出现中度风切变。", "已出现中度风切变天气，")

                        .replace("已出现低云，", "已出现低云天气，")
                        .replace("已出现轻度的霜，", "已出现轻度的霜天气，")
                ;
            }
        }
        // 天气转换3（只能放在"伴"后面）
        textContent = textContent
                .replace("云团顶高", "顶高")
                .replace("东移，预计", "东移，云团，预计")
                .replace("天气结束", "结束")
                .replace("天气，并", "，并")
                .replace("伴CB天气", "伴CB")
        ;
        // 截取关键字"以后"之前的数据
        if(textContent.contains("以后") && textContent.contains("届时")){
            String[] laters = textContent.split("以后");
            String later = laters[0];
            textContent = later.substring(0, later.length()-5);
            if ("，".equals(textContent.substring(textContent.length() - 1))) {
                textContent = textContent.replace("，", "。");
            }
        }
        // 标点符号转换
        textContent = textContent
                .replace("日:", "日")
                .replace("日日", "日")
                .replace("-北京时间", "-")
                .replace("--", "-")
                .replace("；", "。")
                .replace("：", ":")
        ;
        // 判断最后一位是否是"。"，不是则添加，然后取 "，影响机场" 和 "。" 之间的字符串。
        if (!textContent.substring(textContent.length()-1).contains("。")) {
            textContent = textContent.concat("。");
        }
        return textContent;
    }

    /**
     * 低能低见处理
     **/
    public String lowVisibilityHandle(String textContent){
        return textContent
                .replace("云底高维持在60-90米，短时30-60米", "云底高60-90~30-60")
                .replace("云底高维持在60-90米，短时45米", "云底高60-90~45")
                .replace("云底高上升至90米以上", "云底高90-10000")
                .replace("云底高维持在30-90米", "云底高30-90")

                .replace("能见度维持在800米以上", "能见度800-3000")
                .replace("能见度上升至800米以上", "能见度800-3000")
                .replace("能见度下降至600-800米", "能见度600-800")

                .replace("RVR短时在350米-550米波动", "RVR350-550")
                .replace("RVR上升至550米以上", "RVR550-2000")
                .replace("RVR稳定在550米以上", "RVR550-3000")
                .replace("RVR下降至350-550米", "RVR350-550")
                ;
    }

    /**
     * 更新内容
     **/
    public void initCancel(List<AlertContentResolveDO> alertContents, AlertOriginalTextDO airportAlert){
        String textContent = airportAlert.getTextContent();
        // 警报内容
        AlertContentResolveDO alertContent = new AlertContentResolveDO();
        alertContent.setWarningType(airportAlert.getAlertType());
        alertContent.setAffectedArea(WarningTypeEnum.AIRPORT_WARNING.getWarningType());
//        alertContent.setWarningNature(super.getWarningNature(textContent));

        // 预警性质
        alertContent.setWarningNature(WarningNatureEnum.UPDATE.getMessage());
        // 强度
        alertContent.setStrength("无");
        // 范围
        alertContent.setPredictRange("机场");
        // 字眼1 霜、云底高、能见度、风
        List<String> word1 = Lists.newArrayList();
        word1.add("霜");
        word1.add("云底高");
        word1.add("能见度");
        word1.add("风");
        word1.add("雪");
        word1.add("对流云团");
        // 取出伴随天气字符串
        String weatherStr = "";
        String[] weathers = null;
        if (textContent.contains("取消")) {
            weathers = textContent.split("取消");
        }
        if (textContent.contains("解除")) {
            weathers = textContent.split("解除");
        }
        if (textContent.contains("已结束")) {
            weathers = textContent.split("已结束");
        }
        String weather0 = weathers[0];
        for (String word : word1) {
            if (weather0.contains(word)) {
                String[] words = weather0.split(word);
                weatherStr = word + words[1];
                break;
            }
        }
        // 将伴随天气封装成list
        List<ImportantWeatherDO> weatherList = Lists.newArrayList();
        if (weather0.contains("，")) {
            initWeatherList(weatherList, weatherStr, "，");
        }else{
            weatherList.add(AirportUtils.getImportantWeather(weatherStr));
        }
        // 若取消后面存在括号()，则取出括号中的内容
        if (weathers.length>1) {
            String weather1 = weathers[1];
            if (weather1.contains("（")) {
                String replace = weather1.split("（")[1]
                        .replace("）", "")
                        .replace(")", "")
                        .replace("。", "")
                        .replace("，", "")
                        ;
                weatherList.add(AirportUtils.getImportantWeather(replace));
            }
        }
//        // 预报开始时间
//        airportAlertContentDO.setPredictStartTime(new Date());
//        // 预报结束时间
//        airportAlertContentDO.setPredictEndTime(new Date());
//        alertContent.setImportantWeatherDOS(weatherList);
        alertContents.add(alertContent);
    }

    public void initWeatherList(List<ImportantWeatherDO> weatherList, String weather, String keyword){
        int i=1;
        String[] names = weather.split(keyword);
        List<String> keys = Lists.newArrayList();
        keys.add("云底高");
        keys.add("能见度");
        keys.add("RVR");
        keys.add("霜");
        keys.add("风切变");
        keys.add("小雪");
        keys.add("雪");
        keys.add("对流云团");
        String name1 = names[0];
        for (String key : keys) {
            if (name1.contains(key)) {
                i = 0;
                break;
            }
        }
        // 将伴随天气封装成list
        for(; i<names.length; i++){
            String weatherStr = names[i];
            // 将伴随天气封装成list
            if (StringUtils.isNotBlank(weatherStr)) {
                if (!weatherStr.contains(",") && !weatherStr.contains("，")) {
                    weatherStr = weatherStr.concat(",");
                }
                if (weatherStr.contains("，")) {
                    weatherStr = weatherStr.replace("，", ",");
                }
                if (weatherStr.contains("。")) {
                    weatherStr = weatherStr.replace("。", ",");
                }
                String[] split = weatherStr.split(",");
                for (String str : split) {
                    if (StringUtils.isNotBlank(str)
                            && !str.contains("期间")
                            && !str.contains("高")
                            && !str.contains("公里")
                            && !str.contains("强度不变")
                            && !str.contains("范围")
                    ) {
                        String replace = str.replace("，", "")
                                .replace("。", "")
                                .replace("（", "")
                                .replace("）", "")
                                ;
                        weatherList.add(AirportUtils.getImportantWeather(replace));
                    }
                }
            }
        }
    }

}
