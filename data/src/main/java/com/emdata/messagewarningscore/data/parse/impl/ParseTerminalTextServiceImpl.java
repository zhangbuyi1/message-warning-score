package com.emdata.messagewarningscore.data.parse.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommonAdaptee2;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommonAdapter;
import com.emdata.messagewarningscore.data.parse.interfase.ParseTextService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * @description: 终端区
 * @date: 2020/12/10
 * @author: lihongjiang
 */
@Slf4j
@Service("terminalShImpl")
public class ParseTerminalTextServiceImpl extends AlertCommonAdaptee2 implements ParseTextService {

    @Autowired
    private AlertCommonAdapter alertCommonAdapter;



    @Override
    public ResultData<List<AlertContentResolveDO>> getImportantParam(AlertOriginalTextDO airportAlert) {
        // 警报类型 1机场警报 2终端区警报 3区域警报
        alertType.set("2");
        // 发布时间
        Date releaseTime = airportAlert.getReleaseTime();
        // 年月
        String yearMonth = Java8DateUtils.format(releaseTime, Java8DateUtils.MONTH_PATTERN_2);
        ymThreadLocal.set(yearMonth);
        // 年月日时分秒
        String ymdhms = Java8DateUtils.format(releaseTime, Java8DateUtils.DATE_TIME);
        ymdhmsThreadLocal.set(ymdhms);
        // 获取原始内容
        String original = airportAlert.getTextContent();
        // 设置原始文本
        originalThreadLocal.set(original);
        // 封装机场警报数据
        List<AlertContentResolveDO> alertContents = new ArrayList<>();
        int type = 1;
        // 包含取消，则是更新；否则是首份。
        if (original.contains("取消") || original.contains("解除")) {
            if (!original.contains("届时")) {
                type = 0;
            }else {
                if(original.contains("届时取消")){
                    type = 0;
                }
            }
        }
        // 0为取消，1为首次/更正
        if(0 == type){
            super.initCancel(alertContents, airportAlert);
        }else{
            initWeatherContent(alertContents, airportAlert);
        }
        alertType.remove();
        ymThreadLocal.remove();
        contentObject.remove();
        numThreadLocal.remove();
        ymdhmsThreadLocal.remove();
        start1ThreadLocal.remove();
        contentThreadLocal.remove();
        originalThreadLocal.remove();
        return ResultData.success(alertContents);
    }

    /**
     * 终端用！，两场用#，单个用？
     * 1、将数据替换为两场
     * 2、按逗号分割
     * 3、拼接日期
     * 4、拼接时间
     * 5、拼接区域
     * 6、拼接天气
     * 7、复制并替换成机场
     **/
    public void initWeatherContent(List<AlertContentResolveDO> alertContents, AlertOriginalTextDO airportAlert){
        // 原始内容
        String originalContent = airportAlert.getTextContent();
        log.info("原始数据：{}", originalContent);
        originalContent = originalContent.replace("上海终端区将受中等强度的对流云团影响", "在终端区有中等强度的对流云团");
        // 去掉"强度维持"
        String original = deleteOtherKeys(originalContent);
        // 转换"预计"文本
        original = super.expectedTerminal(original);
        String[] expected = original.split("预计北京时间");
        String splitOne = "";
        String splitTwo = "";
        // 第1-2次拼接（拆分所有内容）
        List<String> oneTowList = new LinkedList<>();
        for (int i=1; i<expected.length; i++) {
            splitOne = expected[0];
            splitTwo = expected[i];
            // 1、删除多余的内容
            splitTwo = terminalDelete(splitTwo);
            // 2、将数据替换成两场
            splitTwo = terminalArea(splitTwo);
            // 3、按逗号分割
            splitTwo = terminalDelimiter(splitTwo);
            // 4、其它替换
            splitTwo = terminalOther(splitTwo);
            // 拆分虹桥和浦东
            String[] strings = splitTwo.split("，");
            for (String string : strings) {
                if (string.contains("虹桥") && string.contains("浦东")) {
                    String replace = string
                            .replace("虹桥", "，#虹桥")
                            .replace("浦东", "，#浦东");
                    oneTowList.addAll(Arrays.asList(replace.split("，")));
                }else{
                    oneTowList.add(string);
                }
            }
        }
        // 获取拼接后的文本内容
        List<String> allContents = multipleSplitText(oneTowList, splitTwo, airportAlert);
        // 封装数据
        for (String allContent : allContents) {
            // 0、从内容中取出区域，并删除。
            List<String> collect1 = terminals.stream().filter(allContent::contains).collect(Collectors.toList());
            String areaName = collect1.get(0);
            String content = allContent.replace(areaName, "");
            // 1、拼接头部内容
            String originalText = splitOne.replace("云顶高度：", "云顶高度") + "预计北京时间" + content;
            // 2、让原始文本具有可读性
            String textContent = super.getReadableTextContent(originalText);
            // 3、设置可读性文本
            contentThreadLocal.set(textContent);
//            // 4、设置预计个数（当前是第几个，默认为0）
//            numThreadLocal.set(i);
            // 天气类型 1雷暴 2能见度 3低云 4降雪 5风
            alertCommonAdapter.adapterInterfaceImpl(alertContents, airportAlert, areaName);
        }
    }

    /**
     * 多次分割文本
     **/
    public List<String> multipleSplitText(List<String> oneTowList, String splitTwo, AlertOriginalTextDO airportAlert){
        // 第3次拼接（日期，从上取）
        List<String> thirdList = new LinkedList<>(oneTowList);
        // 第4次拼接（时间，从上取）
        List<String> fourList = new LinkedList<>();
        for (String third : thirdList) {
            if (third.contains(":") && third.contains("-")) {
                // 存在时间，即添加
                fourList.add(third);
            }else{
                // 不存在时间，则判断是否有区域
                List<String> collect = terminals.stream().filter(third::contains).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    // 无区域，则添加
                    fourList.add(third);
                }else{
                    if (third.contains("两场大风天气")) {
                        fourList.add("两场大风天气");
                    }else{
                        // 有区域，则从上一个取出时间
                        for(int i= fourList.size()-1; i>= 0; i--){
                            String lastContent = fourList.get(i);
                            // 向上取日期和时间
                            List<String> collect1 = terminals.stream().filter(lastContent::contains).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(collect1)) {
                                if (lastContent.contains(":") && lastContent.contains("-")) {
                                    Matcher matcher = compile(EXPRESSION).matcher(lastContent);
                                    if (matcher.matches()) {
                                        String time = lastContent.replace(matcher.group(1), "").replace(matcher.group(9), "");
                                        fourList.add(time + third);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // 第5次拼接（区域，从上取）
        List<String> fiveList = new LinkedList<>();
        for (int i=0; i<fourList.size(); i++) {
            String four = fourList.get(i);
            List<String> collect = terminals.stream().filter(four::contains).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                fiveList.add(four);
            } else {
                if(0 == i){
                    // 第一个没有区域时，从第二个取。
                    String four1 = fourList.get(i+1);
                    List<String> collect1 = terminals.stream().filter(four1::contains).collect(Collectors.toList());
                    fiveList.add(collect1.get(0) + four);
                }else{
                    int num = 0;
                    int flag = 0;
                    Matcher matcher1 = compile(EXPRESSION).matcher(four);
                    Matcher matcher2 = compile("([0-3]?[0-9]日)?([0-2]?[0-9]):([0-6]?[0-9])之后(.*?)").matcher(four);
                    if (matcher1.matches() || matcher2.matches()) {
                        // 判断fourContents中，是否存在#号。
                        int jv = fiveList.size() - 1;
                        for (int j = jv; j >= 0; j--) {
                            String content = fiveList.get(j);
                            if (content.contains("#")) {
                                flag = 1;
                                break;
                            }
                        }
                        // 若存在#，则从上面取出区域，并拼接，然后添加。
                        if (1 == flag) {
                            for (int j = jv; j >= 0; j--) {
                                String content = fiveList.get(j);
                                if (content.contains("#")) {
                                    // 将原有的"#"号替换成"$"号
                                    fiveList.set(j, content.replace("#", "$"));
                                    // 含“#”，说明上面有两个区域，依次添加即可。
                                    List<String> collect1 = terminals.stream().filter(content::contains).collect(Collectors.toList());
                                    fiveList.add("#" + collect1.get(0).concat(four));
                                    num = 1;
                                }
                            }
                        }
                        // 若不存在#号，则直接添加内容。
                        if (0 == flag) {
                            for (int j = jv; j >= 0; j--) {
                                // 不含“#”，说明上面只有一个区域
                                String content = fiveList.get(j);
                                List<String> collect1 = terminals.stream().filter(content::contains).collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(collect1)) {
                                    fiveList.add(collect1.get(0).concat(four));
                                    num = 1;
                                    break;
                                }
                            }
                        }
                    }
                    if (0 == num) {
                        // 不存在时间，则直接添加。
                        fiveList.add(four);
                    }
                }
            }
        }
        int i=0;
        int fiveSize = fiveList.size();
        // 第6次拼接（将两个相邻，且同区，且无天气的内容，前面同时添加#号）
        List<String> sixList = new LinkedList<>();
        for (; i<fiveSize; i++) {
            String one = fiveList.get(i);
            int i1 = i + 1;
            if(i1 < fiveSize){
                String two = fiveList.get(i1);
                List<String> oneTerminal = terminals.stream().filter(one::contains).collect(Collectors.toList());
                List<String> twoTerminal = terminals.stream().filter(two::contains).collect(Collectors.toList());
                List<String> oneWeather = allWeather.stream().filter(one::contains).collect(Collectors.toList());
                List<String> twoWeather = allWeather.stream().filter(two::contains).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(oneTerminal) && !CollectionUtils.isEmpty(twoTerminal) && CollectionUtils.isEmpty(oneWeather) && CollectionUtils.isEmpty(twoWeather)) {
                    String oneStr = oneTerminal.get(0);
                    String twoStr = twoTerminal.get(0);
                    if (oneStr.equals(twoStr)
                            || oneStr.contains("浦东") && twoStr.contains("虹桥")
                            || oneStr.contains("虹桥") && twoStr.contains("浦东")) {
                        if (one.contains("终端")) {
                            sixList.add("!" + one);
                            sixList.add("!" + two);
                            i = i1;
                        }else if (one.contains("虹桥") || one.contains("浦东")) {
                            sixList.add("#" + one);
                            sixList.add("#" + two);
                            i = i1;
                        }else{
                            sixList.add(one);
                        }
                    }else{
                        sixList.add(one);
                    }
                }else{
                    sixList.add(one);
                }
            }else{
                sixList.add(one);
            }
        }
        int num = 0;
        int flag = 0;
        // 第7次拼接（天气，向上设置）
        List<String> sevenList = new LinkedList<>();
        for (String six : sixList) {
            // 有"终端、浦东、虹桥"，则代表有区域
            List<String> collect = terminals.stream().filter(six::contains).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                // num=1，且sixList>0，说明之前的区域已经添加了天气，则将"#"删除。
                if (1 == num && sevenList.size() > 0) {
                    sevenList = sevenList.stream().map(seven -> seven
                            .replace("#", "")
                            .replace("$", "")
                            .replace("!", "")
                    ).collect(Collectors.toList());
                }
                sevenList.add(six);
                num = 0;
                flag = 0;
            } else {
                if (sevenList.size() > 0) {
                    // 确认 sevenList 中，最后一个有没有"#"或"!"。有则是复制，无则是不复制。
                    // 将天气添加至有"#"或"!"号的内容中，并将flag设置为1。
                    int finalIndex = sevenList.size() - 1;
                    for (int j = finalIndex; j >= 0; j--) {
                        String content = sevenList.get(j);
                        if (content.contains("#") || content.contains("$") || content.contains("!")) {
                            flag = 1;
                            break;
                        }
                    }
                    // 有#或$或!号，则都添加该天气。
                    if (1 == flag) {
                        num = 1;
                        String symbol = "#";
                        String seven = sevenList.get(sevenList.size() - 1);
                        if (seven.contains("$")) {
                            symbol = "$";
                        }else if(seven.contains("!")){
                            symbol = "!";
                        }
                        for (int k = 0; k < sevenList.size(); k++) {
                            String fiveContent = sevenList.get(k);
                            if (fiveContent.contains(symbol)) {
                                sevenList.set(k, fiveContent.concat("，").concat(six));
                            }
                        }
                    }
                    // 无#或$或!号，则只添加最后一条数据。
                    if (0 == flag) {
                        sevenList.set(finalIndex, sevenList.get(finalIndex).concat("，").concat(six));
                    }
                }
            }
        }
        // 第8次拼接（复制并替换）
        List<String> eightList = new LinkedList<>();
        for (String seven : sevenList) {
            String content = seven
                    .replace("和", "，")
                    .replace("：", ":")
                    ;
            // 同时存在终端和三航B时，删除终端。
            if (content.contains("终端") && content.contains("三航B")) {
                content = content.replace("终端", "");
            }
            List<String> addContents = new LinkedList<>();
            // 判断有几个匹配的区域，有几个就添加几个相同的数据
            List<String> newTerminals = terminals.stream().filter(content::contains).collect(Collectors.toList());
            for (int k = 0; k < newTerminals.size(); k++) {
                addContents.add(content);
            }
            int size = addContents.size();
            if (size > 1) {
                for (int k = 0; k < addContents.size(); k++) {
                    String addContent = addContents.get(k);
                    // 依次删除多余的终端区域
                    for (int z = 0; z < newTerminals.size(); z++) {
                        if (z != k) {
                            addContent = "#" + addContent.replace(newTerminals.get(z), "");
                        }
                    }
                    // 添加删除后的内容
                    eightList.add(addContent);
                }
            }
            if (size == 1) {
                String addContent = addContents.get(0);
                if(content.contains("终加两")){
                    if (content.contains("-") && content.contains(":")) {
                        eightList.add(addContent.replace("终加两", "#终端"));
                        eightList.add(addContent.replace("终加两", "#虹桥"));
                        eightList.add(addContent.replace("终加两", "#浦东"));
                    }
                }else if (content.contains("两场")) {
                    if (content.contains("-") && content.contains(":")) {
                        // 把"#"作为标记，表示该条记录是被复制的，用于后面添加天气。
                        eightList.add(addContent.replace("两场", "#虹桥"));
                        eightList.add(addContent.replace("两场", "#浦东"));
                    }else{
                        // 以下判断，是一些特殊处理。
                        int eightSize = eightList.size();
                        int index1 = eightSize - 1;
                        int index2 = eightSize - 2;
                        if (index1 >= 0) {
                            String indexOne = eightList.get(index1);
                            if (index2 >= 0) {
                                String indexTwo = eightList.get(index2);
                                // 以上两个下标，若包含"浦东"和"虹桥"，则将该天气分别添加至浦东和虹桥中。
                                if (indexOne.contains("虹桥") && indexTwo.contains("浦东")
                                        || indexOne.contains("浦东") && indexTwo.contains("虹桥")) {
                                    Matcher matcherOne = compile(EXPRESSION).matcher(indexOne);
                                    if (matcherOne.matches()) {
                                        List<String> one = terminals.stream().filter(indexOne::contains).collect(Collectors.toList());
                                        String time = indexOne.replace(matcherOne.group(1), "").replace(matcherOne.group(9), "");
                                        eightList.add(one.get(0) + time + seven.replace("两场", ""));
                                    }
                                    Matcher matcherTow = compile(EXPRESSION).matcher(indexTwo);
                                    if (matcherTow.matches()) {
                                        List<String> two = terminals.stream().filter(indexTwo::contains).collect(Collectors.toList());
                                        String time = indexTwo.replace(matcherTow.group(1), "").replace(matcherTow.group(9), "");
                                        eightList.add(two.get(0) + time + seven.replace("两场", ""));
                                    }
                                }
                            }
                        }
                    }
                }else if(content.contains("三航A")){
                    eightList.add(addContent.replace("三航A", "#SASAN"));
                    eightList.add(addContent.replace("三航A", "#NXD"));
                    eightList.add(addContent.replace("三航A", "#AND"));
                }else if(content.contains("三航B")){
                    eightList.add(addContent.replace("三航B", "#SASAN"));
                    eightList.add(addContent.replace("三航B", "#NXD"));
                    eightList.add(addContent.replace("三航B", "#PIKAS"));
                }else if(content.contains("两航A")){
                    eightList.add(addContent.replace("两航A", "#AND"));
                    eightList.add(addContent.replace("两航A", "#NXD"));
                }else if(content.contains("两航B")){
                    eightList.add(addContent.replace("两航B", "#SASAN"));
                    eightList.add(addContent.replace("两航B", "#PIKAS"));
                }else {
                    eightList.add(addContent);
                }
            }
            if (size < 1) {
                if (content.contains("其中") && splitTwo.contains("两场")) {
                    eightList.add(content.replace("其中", "#虹桥"));
                    eightList.add(content.replace("其中", "#浦东"));
                } else {
                    eightList.add(content);
                }
            }
        }
        // 第9次拼接（添加对流云团天气"）
        List<String> nineList = new LinkedList<>();
        for (String eight : eightList) {
            List<String> collect = allWeather.stream().filter(eight::contains).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                nineList.add(eight);
            }else{
                nineList.add(eight + "。");
            }
        }
        // 第10次拼接（特殊处理）
        List<String> tenList = new LinkedList<>();
        for (String nine : nineList) {
            String newNine = nine;
            // 将目前替换成发布时间
            if(newNine.contains("目前")){
                String format = Java8DateUtils.format(airportAlert.getReleaseTime(), Java8DateUtils.DATE_TIME_WITHOUT_SECONDS);
                String substring = format.substring(format.length() - 5);
                newNine = newNine.replace("目前", substring);
            }
            // 若天气在时间前面，则将天气放到时间后面去。
            String substring = "对流";
            if (substring.equals(newNine.substring(0, 2))) {
                newNine = newNine.replace("对流", "").concat("对流");
            }
            // 此时航道没有天气时，则从上一个有天气的终端获取。
            if (newNine.contains("SASAN") || newNine.contains("PIKAS") || newNine.contains("NXD") || newNine.contains("AND")) {
                List<String> collect = allWeather.stream().filter(newNine::contains).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    // 遍历tenList，直到找到上一个有天气的终端。
                    List<String> weathers = Lists.newArrayList();
                    int tenIndex = tenList.size() - 1;
                    for (int j=tenIndex; j>=0; j--) {
                        String lastContent = tenList.get(j);
                        List<String> collect1 = allWeather.stream().filter(lastContent::contains).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(collect1) && lastContent.contains("终端")) {
                            weathers.addAll(collect1);
                            break;
                        }
                    }
                    // 若存在天气，则遍历添加天气，否则不添加。
                    if (weathers.size()>0) {
                        StringBuilder stringBuilder = new StringBuilder(newNine);
                        for (String weather : weathers) {
                            stringBuilder.append("，").append(weather);
                        }
                        tenList.add(stringBuilder.toString());
                    }else {
                        tenList.add(newNine);
                    }
                }
            } else {
                tenList.add(newNine);
            }
        }
        // 返回数据（删除"#"号）
        return tenList.stream().map(thirdContent ->
                thirdContent
                        .replace("!", "")
                        .replace("$", "")
                        .replace("#", "")
        ).collect(Collectors.toCollection(LinkedList::new));
    }



}
