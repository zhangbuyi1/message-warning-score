package com.emdata.messagewarningscore.data.parse.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommonAdaptee2;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommonAdapter;
import com.emdata.messagewarningscore.data.parse.interfase.ParseTextService;
import com.emdata.messagewarningscore.data.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * @description: 解析区域警报文本实现类
 * @date: 2020/12/10
 * @author: lihongjiang
 */
@Slf4j
@Service("areaShImpl")
public class ParseAreaTextImpl extends AlertCommonAdaptee2 implements ParseTextService {

    @Autowired
    private AlertCommonAdapter alertCommonAdapter;

    @Override
    public ResultData<List<AlertContentResolveDO>> getImportantParam(AlertOriginalTextDO airportAlert) {
        // 警报类型 1机场警报 2终端区警报 3区域警报
        alertType.set("3");
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
        // 封装警报内容解析数据
        List<AlertContentResolveDO> alertContents = new ArrayList<>();
        int type = 1;
        // 包含取消，则是更新；否则是首份。
        if (original.contains("取消") || original.contains("解除") || original.contains("已结束")) {
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
        // 去掉"强度维持"
        String original = deleteOtherKeys(originalContent);
        // 0、替换预计
        original = super.expectedArea(original);
        // 1、添加在
        String substring = original.substring(0, 3);
        if (substring.contains("19日")) {
            original = "在，预计北京时间" + original;
        }
        // 2、未来时间（24小时/12小时）
        original = timeArea(original);
        // 3、时间格式 07：30
        original = original.replace("07：30", "07:30");
        String[] expected = original.split("预计北京时间");
        String splitOne = "";
        String splitTwo = "";
        // 第1次拼接（替换并拆分所有内容）
        List<String> beenSplitList = new LinkedList<>();
        for (int i=1; i<expected.length; i++) {
            splitOne = expected[0];
            splitTwo = expected[i];
            // 1、替换多余
            splitTwo = deleteArea(splitTwo);
            // 2、替换区域
            splitTwo = areaArea(splitTwo);
            // 3、替换符号
            splitTwo = delimiterArea(splitTwo);
//            // 4、替换其它
//            splitTwo = terminalOther(splitTwo);
            beenSplitList.add(splitTwo);
        }
        // 获取拼接后的文本内容
        List<String> allContents = multipleSplitText(beenSplitList, splitOne, airportAlert);
        for (String allContent : allContents) {
            // 0、从内容中取出区域，并删除。
            List<String> collect1 = areas.stream().filter(allContent::contains).collect(Collectors.toList());
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
    public List<String> multipleSplitText(List<String> beenSplitList, String splitOne, AlertOriginalTextDO airportAlert){
        // 第0次拼接（按符号分割）
        List<String> zeroList = new LinkedList<>();
        for (String beenSplit : beenSplitList) {
            if (beenSplit.contains("，")) {
                String[] splits = beenSplit.split("，");
                zeroList.addAll(Arrays.asList(splits));
            }else{
                zeroList.add(beenSplit);
            }
        }
        // 第1次拼接（删除多余的内容）
        List<String> oneList = new LinkedList<>();
        for (String zero : zeroList) {
            String string = StringUtil.deleteBetweenByKey(zero, "概率", "%");
            string = string
                    .replace("（概率", "")
                    .replace("）", "");
            oneList.add(string);
        }
        // 第2次拼接（时间，替换目前）
        List<String> twoList = new LinkedList<>();
        for (String one : oneList) {
            if (one.contains("目前")) {
                Date date = Java8DateUtils.parseDateTimeStr(ymdhmsThreadLocal.get(), Java8DateUtils.DATE_TIME);
                String format = Java8DateUtils.format(date, Java8DateUtils.TIME_WITHOUT_SECOND);
                twoList.add(one.replace("目前", format));
            }else {
                twoList.add(one);
            }
        }
        // 第3次拼接（时间，从上取）
        List<String> thirdList = new LinkedList<>();
        for (String two : twoList) {
            if (two.contains(":") && !two.contains("-")) {
                if (two.contains("0能见度")) {
                    thirdList.add(two.replace("0能见度", "0-00:00能见度"));
                }else if (two.contains("0RVR")) {
                    thirdList.add(two.replace("0RVR", "0-00:00RVR"));
                }else if (two.contains("0云底高")) {
                    thirdList.add(two.replace("0云底高", "0-00:00云底高"));
                }else if (two.contains("0云高")) {
                    thirdList.add(two.replace("0云高", "0-00:00云高"));
                }else if (two.contains("0浦东机场")) {
                    thirdList.add(two.replace("0浦东机场", "0-00:00浦东机场"));
                }else if(two.contains("14日08:00两场云底高稳定抬升")){
                    thirdList.add(two.replace("14日08:00两场云底高稳定抬升", "14日08:00-00:00两场云底高稳定抬升"));
                }else if(two.contains("云底高12日05:00上升")){
                    thirdList.add(two.replace("云底高12日05:00上升", "云底高12日05:00-00:00上升"));
                }
            }else {
                thirdList.add(two);
            }
        }
        // 第4次拼接（时间，拼接-）
        List<String> fourList = new LinkedList<>();
        for (String third : thirdList) {
            if (third.contains(":") && third.contains("-")) {
                // 存在时间，即添加
                fourList.add(third);
            }else{
                // 不存在时间，则判断是否有区域
                List<String> collect = areas.stream().filter(third::contains).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    // 无区域，则添加
                    fourList.add(third);
                }else{
                    // 有区域，则从上一个取出时间
                    for(int i= fourList.size()-1; i>= 0; i--){
                        String lastContent = fourList.get(i);
//                        // 向上取日期和时间
//                        List<String> collect1 = areas.stream().filter(lastContent::contains).collect(Collectors.toList());
//                        if (!CollectionUtils.isEmpty(collect1)) {
                        if (lastContent.contains(":") && lastContent.contains("-")) {
                            Matcher matcher = compile(EXPRESSION).matcher(lastContent);
                            if (matcher.matches()) {
                                String time = lastContent.replace(matcher.group(1), "").replace(matcher.group(9), "");
                                fourList.add(time + third);
                                break;
                            }
                        }
//                        }
                    }
                }
            }
        }
        // 第5次拼接（区域，从上取）
        List<String> fiveList = new LinkedList<>();
        for (String four : fourList) {
            List<String> collect = areas.stream().filter(four::contains).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                if (!"航路".equals(four) && !"航站".equals(four)) {
                    fiveList.add(four);
                }
            } else {
                if (four.contains("上述机场") || four.contains("上述航站") || four.contains("上述航路")) {
                    fiveList.add(four);
                }else{
                    int fiveSize = fiveList.size();
                    if (!four.contains(":")) {
                        fiveList.add(four);
                    }else{
                        if (fiveSize > 0) {
                            // 没有区域，则从上取。
                            for (int j = fiveSize - 1; j >= 0; j--) {
                                String five = fiveList.get(j);
                                if (four.contains("短时") && five.contains("RVR")) {
                                    fiveList.add(four);
                                    break;
                                }else{
                                    List<String> collect1 = areas.stream().filter(five::contains).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(collect1)) {
                                        fiveList.add(collect1.get(0).concat(four));
                                        break;
                                    }
                                }
                            }
                        }else{
                            // 上面没有，则从原始数据的前面取。
                            List<String> collect1 = areas.stream().filter(splitOne::contains).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(collect1)) {
                                fiveList.add(collect1.get(0).concat(four));
                            }
                        }
                    }
                }
            }
        }
        // 第6次拼接（天气，向上设置）
        List<String> sixList = new LinkedList<>();
        for (String five : fiveList) {
            List<String> collect = areas.stream().filter(five::contains).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                sixList.add(five);
            } else {
                if (five.contains("上述机场") || five.contains("上述航站") || five.contains("上述航点")) {
                    sixList.add(five);
                }else {
                    int finalIndex = sixList.size();
                    if (finalIndex > 0) {
                        for (int j = finalIndex- 1; j >= 0; j--) {
                            String content = sixList.get(j);
                            sixList.set(j, content.concat("，").concat(five));
                            break;
                        }
                    }
                }
            }
        }
        // 第7次拼接（天气，从上取）
        List<String> sevenList = new LinkedList<>();
        for (int i=0; i<sixList.size(); i++) {
            String six = sixList.get(i);
            List<String> collect13 = areas.stream().filter(six::contains).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect13)) {
                sevenList.add(six);
            }else{
                List<String> collect = allWeather.stream().filter(six::contains).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)) {
                    sevenList.add(six);
                }else {
                    int num =1;
                    // 判断后面有没有上述关键字，有则直接添加，无则执行以下逻辑。
                    for (int j=i+1; j<sixList.size(); j++) {
                        String sixJ = sixList.get(j);
                        if (sixJ.contains("上述机场") || sixJ.contains("上述航站") || sixJ.contains("上述航点")) {
                            num = 0;
                            break;
                        }
                    }
                    if (0 == num) {
                        sevenList.add(six);
                    }else{
                        String str = "短时";
                        if (six.contains(str)) {
                            String str1 = six.split(str)[1];
                            sevenList.add(str + str1);
                        }else{
                            StringBuilder stringBuilder = new StringBuilder(six);
                            // sixList最后一条的区域与当前区域匹配时，则取sixList；否则取splitOne；
                            int sevenSize = sevenList.size();
                            if (sevenSize > 0) {
                                String area1 = "";
                                String area2 = "";
                                for (int j=sevenSize-1; j>=0; j--) {
                                    String lastIndex = sevenList.get(j);
                                    List<String> collect1 = areas.stream().filter(six::contains).collect(Collectors.toList());
                                    List<String> collect2 = areas.stream().filter(lastIndex::contains).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(collect1)) {
                                        area1 = collect1.get(0);
                                    }
                                    if (!CollectionUtils.isEmpty(collect2)) {
                                        area2 = collect2.get(0);
                                    }
                                    String weatherStr = splitOne;
                                    // 区域匹配且sixList有天气则取sixList天气，否则取splitOne天气。
                                    if (area1.equals(area2)) {
                                        // 有天气则取sixList天气
                                        List<String> collect11 = allWeather.stream().filter(lastIndex::contains).collect(Collectors.toList());
                                        if (!CollectionUtils.isEmpty(collect11)) {
                                            weatherStr = lastIndex;
                                        }
                                    }
                                    List<String> collect12 = allWeather.stream().filter(weatherStr::contains).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(collect12)) {
                                        for (String col : collect12) {
                                            stringBuilder.append("，").append(col);
                                        }
                                        sevenList.add(stringBuilder.toString());
                                        break;
                                    }
                                }
                            }else{
                                List<String> collect1 = allWeather.stream().filter(splitOne::contains).collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(collect1)) {
                                    for (String col : collect1) {
                                        stringBuilder.append("，").append(col);
                                    }
                                    sevenList.add(stringBuilder.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        // 第8次拼接（复制并替换）
        List<String> eightList = new LinkedList<>();
        for (String seven : sevenList) {
            if(seven.contains("虹浦杭波昌福厦")) {
                eightList.add(seven.replace("虹浦杭波昌福厦", "虹桥机场"));
                eightList.add(seven.replace("虹浦杭波昌福厦", "浦东机场"));
                eightList.add(seven.replace("虹浦杭波昌福厦", "杭州机场"));
                eightList.add(seven.replace("虹浦杭波昌福厦", "宁波机场"));
                eightList.add(seven.replace("虹浦杭波昌福厦", "南昌机场"));
                eightList.add(seven.replace("虹浦杭波昌福厦", "福州机场"));
                eightList.add(seven.replace("虹浦杭波昌福厦", "厦门机场"));
            }else if(seven.contains("肥京杭波温福厦")) {
                eightList.add(seven.replace("肥京杭波温福厦", "合肥机场"));
                eightList.add(seven.replace("肥京杭波温福厦", "南京机场"));
                eightList.add(seven.replace("肥京杭波温福厦", "杭州机场"));
                eightList.add(seven.replace("肥京杭波温福厦", "宁波机场"));
                eightList.add(seven.replace("肥京杭波温福厦", "温州机场"));
                eightList.add(seven.replace("肥京杭波温福厦", "福州机场"));
                eightList.add(seven.replace("肥京杭波温福厦", "厦门机场"));
            }else if(seven.contains("虹浦杭波温福厦")) {
                eightList.add(seven.replace("虹浦杭波温福厦", "虹桥机场"));
                eightList.add(seven.replace("虹浦杭波温福厦", "浦东机场"));
                eightList.add(seven.replace("虹浦杭波温福厦", "杭州机场"));
                eightList.add(seven.replace("虹浦杭波温福厦", "宁波机场"));
                eightList.add(seven.replace("虹浦杭波温福厦", "温州机场"));
                eightList.add(seven.replace("虹浦杭波温福厦", "福州机场"));
                eightList.add(seven.replace("虹浦杭波温福厦", "厦门机场"));
            }else if(seven.contains("肥波杭青浦京昌")) {
                eightList.add(seven.replace("肥波杭青浦京昌", "合肥机场"));
                eightList.add(seven.replace("肥波杭青浦京昌", "宁波机场"));
                eightList.add(seven.replace("肥波杭青浦京昌", "杭州机场"));
                eightList.add(seven.replace("肥波杭青浦京昌", "青岛机场"));
                eightList.add(seven.replace("肥波杭青浦京昌", "浦东机场"));
                eightList.add(seven.replace("肥波杭青浦京昌", "南京机场"));
                eightList.add(seven.replace("肥波杭青浦京昌", "南昌机场"));
            }else if(seven.contains("济青肥京虹浦")) {
                eightList.add(seven.replace("济青肥京虹浦", "济南机场"));
                eightList.add(seven.replace("济青肥京虹浦", "青岛机场"));
                eightList.add(seven.replace("济青肥京虹浦", "合肥机场"));
                eightList.add(seven.replace("济青肥京虹浦", "南京机场"));
                eightList.add(seven.replace("济青肥京虹浦", "虹桥机场"));
                eightList.add(seven.replace("济青肥京虹浦", "浦东机场"));
            }else if(seven.contains("昌杭温波虹浦")) {
                eightList.add(seven.replace("昌杭温波虹浦", "南昌机场"));
                eightList.add(seven.replace("昌杭温波虹浦", "杭州机场"));
                eightList.add(seven.replace("昌杭温波虹浦", "温州机场"));
                eightList.add(seven.replace("昌杭温波虹浦", "宁波机场"));
                eightList.add(seven.replace("昌杭温波虹浦", "虹桥机场"));
                eightList.add(seven.replace("昌杭温波虹浦", "浦东机场"));
            }else if(seven.contains("虹浦肥京杭波")) {
                eightList.add(seven.replace("虹浦肥京杭波", "虹桥机场"));
                eightList.add(seven.replace("虹浦肥京杭波", "浦东机场"));
                eightList.add(seven.replace("虹浦肥京杭波", "合肥机场"));
                eightList.add(seven.replace("虹浦肥京杭波", "南京机场"));
                eightList.add(seven.replace("虹浦肥京杭波", "杭州机场"));
                eightList.add(seven.replace("虹浦肥京杭波", "宁波机场"));
            }else if(seven.contains("青肥京虹浦")) {
                eightList.add(seven.replace("青肥京虹浦", "青岛机场"));
                eightList.add(seven.replace("青肥京虹浦", "合肥机场"));
                eightList.add(seven.replace("青肥京虹浦", "南京机场"));
                eightList.add(seven.replace("青肥京虹浦", "虹桥机场"));
                eightList.add(seven.replace("青肥京虹浦", "浦东机场"));
            }else if(seven.contains("肥京杭虹浦")) {
                eightList.add(seven.replace("肥京杭虹浦", "合肥机场"));
                eightList.add(seven.replace("肥京杭虹浦", "南京机场"));
                eightList.add(seven.replace("肥京杭虹浦", "杭州机场"));
                eightList.add(seven.replace("肥京杭虹浦", "虹桥机场"));
                eightList.add(seven.replace("肥京杭虹浦", "浦东机场"));
            }else if(seven.contains("昌杭虹浦波")) {
                eightList.add(seven.replace("昌杭虹浦波", "南昌机场"));
                eightList.add(seven.replace("昌杭虹浦波", "杭州机场"));
                eightList.add(seven.replace("昌杭虹浦波", "虹桥机场"));
                eightList.add(seven.replace("昌杭虹浦波", "浦东机场"));
                eightList.add(seven.replace("昌杭虹浦波", "宁波机场"));
            }else if(seven.contains("京杭温昌")) {
                eightList.add(seven.replace("京杭温昌", "南京机场"));
                eightList.add(seven.replace("京杭温昌", "杭州机场"));
                eightList.add(seven.replace("京杭温昌", "温州机场"));
                eightList.add(seven.replace("京杭温昌", "南昌机场"));
            }else if(seven.contains("杭波虹浦")) {
                eightList.add(seven.replace("杭波虹浦", "杭州机场"));
                eightList.add(seven.replace("杭波虹浦", "宁波机场"));
                eightList.add(seven.replace("杭波虹浦", "虹桥机场"));
                eightList.add(seven.replace("杭波虹浦", "浦东机场"));
            }else if(seven.contains("肥京虹浦")) {
                eightList.add(seven.replace("肥京虹浦", "合肥机场"));
                eightList.add(seven.replace("肥京虹浦", "南京机场"));
                eightList.add(seven.replace("肥京虹浦", "虹桥机场"));
                eightList.add(seven.replace("肥京虹浦", "浦东机场"));
            }else if(seven.contains("昌杭虹浦")) {
                eightList.add(seven.replace("昌杭虹浦", "南昌机场"));
                eightList.add(seven.replace("昌杭虹浦", "杭州机场"));
                eightList.add(seven.replace("昌杭虹浦", "虹桥机场"));
                eightList.add(seven.replace("昌杭虹浦", "浦东机场"));
            }else if(seven.contains("杭波温")) {
                eightList.add(seven.replace("杭波温", "杭州机场"));
                eightList.add(seven.replace("杭波温", "宁波机场"));
                eightList.add(seven.replace("杭波温", "温州机场"));
            }else if(seven.contains("肥京青")) {
                eightList.add(seven.replace("肥京青", "合肥机场"));
                eightList.add(seven.replace("肥京青", "南京机场"));
                eightList.add(seven.replace("肥京青", "青岛机场"));
            }else if(seven.contains("杭虹浦")) {
                eightList.add(seven.replace("杭虹浦", "杭州机场"));
                eightList.add(seven.replace("杭虹浦", "虹桥机场"));
                eightList.add(seven.replace("杭虹浦", "浦东机场"));
            }else if(seven.contains("虹浦京")) {
                eightList.add(seven.replace("虹浦京", "虹桥机场"));
                eightList.add(seven.replace("虹浦京", "浦东机场"));
                eightList.add(seven.replace("虹浦京", "南京机场"));
            }else if(seven.contains("杭宁")) {
                eightList.add(seven.replace("杭宁", "杭州机场"));
                eightList.add(seven.replace("杭宁", "宁波机场"));
            }else if(seven.contains("肥京")) {
                eightList.add(seven.replace("肥京", "合肥机场"));
                eightList.add(seven.replace("肥京", "南京机场"));
            }else if(seven.contains("厦福")) {
                eightList.add(seven.replace("厦福", "厦门机场"));
                eightList.add(seven.replace("厦福", "福州机场"));
            }else if(seven.contains("昌温")) {
                eightList.add(seven.replace("昌温", "南昌机场"));
                eightList.add(seven.replace("昌温", "温州机场"));
            }else if(seven.contains("两场")) {
                eightList.add(seven.replace("两场", "虹桥机场"));
                eightList.add(seven.replace("两场", "浦东机场"));
            }else if (seven.contains("两航B")) {
                eightList.add(seven.replace("两航B", "SASAN"));
                eightList.add(seven.replace("两航B", "PIKAS"));
            }else{
                eightList.add(seven);
            }
        }
        // 第9次拼接（特殊处理：上述机场、已出现、已经出现、"）
        List<String> nineList = new LinkedList<>();
        for (String eight : eightList) {
            if(!eight.contains("已经出现") && !eight.contains("已出现") && !eight.contains("稳定抬升至标准以上") && !eight.contains("上升至标准以上")) {
                if (!eight.contains("上述机场") && !eight.contains("上述航站") && !eight.contains("上述航点")) {
                    nineList.add(eight);
                }else{
                    for (int j=0; j<nineList.size(); j++) {
                        String ten = nineList.get(j);
                        nineList.set(j, ten.concat(eight)
                                .replace("上述机场", "，")
                                .replace("上述航站", "，")
                                .replace("上述航点", "，")
                        );
                    }
                }
            }
        }
        // 第10次拼接（特殊处理：能见度、RVR、短时、）  低能低见度   云底高维持在60-90米，短时45米
        List<String> tenList = new LinkedList<>();
        for (String nine : nineList) {
            lowVisibility(nine, tenList);
        }
        // 返回数据（删除"#"号）  东北风到偏南风14-23/秒阵风24-30/秒，中到大阵雨
        return tenList.stream().map(thirdContent ->
                thirdContent
                        .replace("!", "")
                        .replace("$", "")
                        .replace("#", "")
                        .replace("m/s", "，")
                        .replace("/秒", "，")
                        .replace("0转雨夹雪", "0雨夹雪")
        ).collect(Collectors.toCollection(LinkedList::new));
    }

    public void lowVisibility(String nine, List<String> tenList){
        nine = nine
                // 云底高、能见度、RVR
                .replace("能见度达到800以上，短时600-800，RVR550以上，短时350-550，云底高30-60", "能见度800-3000~600-800，RVR550-2000~350-550，云底高30-60")
                .replace("能见度抬升至1000以上，RVR抬升至550以上，云底高抬升至80以上", "能见度1000-3000，RVR550-2000，云底高80-10000")
                .replace("能见度抬升至800以上，RVR抬升至550以上，云底高抬升至80以上", "能见度800-3000，RVR550-2000，云底高80-10000")
                .replace("能见度抬升至500-800，RVR抬升至350-550，云底高抬升至60-80", "能见度500-800，RVR350-550，云底高60-80")
                // 云底高，RVR
                .replace("云底高60-80，短时30-50，RVR短时350-500", "云底高60-80~30-50，RVR~350-500")
                .replace("云底高上升至100以上，RVR抬升至550以上", "云底高100-10000，RVR550-2000")
                .replace("云高抬高至80以上，RVR抬升至550以上", "云高80-2000，RVR550-2000")
                .replace("云底高下降至30-50，RVR下降至150-550", "云底高30-50，RVR150-550")
                .replace("云底高60-80，二、四跑道云底高度短时30-50", "云底高60-80~30-50")
                .replace("低云天气，云底高60-80，短时30-50", "低云，云底高60-80~30-50")
                // 云底高
                .replace("云底高下降至60-80，短时在30-50波动", "云底高60-80~30-50")
                .replace("云底高下降至60-80，短时云底高30-50", "云底高60-80~30-50")
                .replace("云底高30-90、RVR短时350-550", "云底高30-90，RVR350-550")
                .replace("云底高60-80之间，短时30-50波动", "云底高60-80~30-50")
                .replace("云底高下降至60-80，短时30-50", "云底高60-80~30-50")
                .replace("云底高60-80，短时30-50", "云底高60-80~30-50")
                .replace("云底高60-80，短时45", "云底高60-80~45")
                .replace("云底高上升至100以上", "云底高100-10000")
                .replace("云底高上升至80及以上", "云底高80-10000")
                .replace("云底高上升至80以上", "云底高80-10000")
                .replace("云底高60-80，短时50", "云底高60-80~50")
                .replace("云底高60-78短时波动", "云底高60-78")
                .replace("云底高60-90，短时30-60", "云底高60-90~30-60")
                .replace("云底高60-90~30-60波动", "云底高60-90~30-60")
                .replace("云底高12日05:00-00:00上升至100以上", "12日05:00-00:00云底高100-10000")
                .replace("云底高60，短时30-50", "云底高60~30-50")
                .replace("云底高稳定抬升至80以上", "云底高80-10000")
                .replace("云底高稳定抬升至90以上", "云底高90-10000")
                .replace("云底高为90及以上", "云底高90-10000")
                .replace("云底高下降至30-50", "云底高30-50")
                .replace("云底高抬升至80以上", "云底高80-10000")
                // 云高
                .replace("RVR上升至600-800，短时400-500，云高上升至60，短时30-50", "RVR600-800~400-500，云高60~30-50")
                .replace("RVR1000-2000，云高上升至90以上", "RVR1000-2000，云高90-10000")
                .replace("RVR下降至400-500，云高下降至30-50", "RVR400-500，云高30-50")
                .replace("云高60-80，短时30-50", "云高60-80~30-50")
                .replace("云高上升至90以上", "云高90-10000")
                .replace("云高将下降至30-50", "云高30-50")
                .replace("云高60-80，短时40", "云高60-80~40")
                .replace("云高60-90，短时30-60", "云高60-90~30-60")
                .replace("云高抬升至60及以上", "云高60-10000")
                .replace("云高抬升至60以上", "云高60-10000")
                .replace("云高60-80，短时30-60", "云高60-80~30-60")
                // 能见度、RVR
                .replace("能见度下降至800以下，RVR下降至550以下", "能见度0-800，RVR0-550")
                .replace("能见度下降至200-600，RVR在300-700波动", "能见度200-600，RVR300-700")
                .replace("部分雾，能见度最低1000-2000，部分跑道RVR短时低于350", "雾，能见度1000-2000，RVR~0-350")
                .replace("能见度短时800-1000，部分跑道RVR短时300-500", "能见度~800-1000，RVR~300-500")
                .replace("能见度600-800，短时300，RVR350-550，短时200", "能见度600-800~300，RVR350-550~200")
                .replace("出现RVR350-550，短时350以下，能见度400", "RVR350-550~0-350，能见度400")
                .replace("RVR上升至550以上，能见度上升至800以上", "RVR550-2000，能见度800-3000")
                .replace("能见度下降至600-800，RVR短时350-550", "能见度600-800，RVR~350-550")
                .replace("能见度700，短时400，RVR400，短时200", "能见度700~400，RVR400~200")
                .replace("能见度短时500-800、RVR短时350-550", "能见度~500-800，RVR~350-550")
                .replace("能见度最低300-500（RVR最低150-300", "能见度300-500，RVR150-300")
                .replace("能见度低于800（RVR最低300-550", "能见度0-800，RVR300-550")
                // 能见度和RVR
                .replace("能见度和RVR均上升至1000以上", "能见度1000-3000，RVR1000-2000")
                .replace("能见度和RVR抬升至800以上", "能见度800-3000，RVR800-2000")
                .replace("能见度和RVR上升至800以上", "能见度800-3000，RVR800-2000")
                .replace("能见度和RVR转到800以上", "能见度800-3000，RVR800-2000")
                .replace("能见度和RVR转至800以上", "能见度800-3000，RVR800-2000")
                .replace("能见度和RVR为800以上", "能见度800-3000，RVR800-2000")
                .replace("能见度和RVR为700以上", "能见度700-3000，RVR700-2000")
                .replace("能见度和RVR转至600以上", "能见度600-3000，RVR600-2000")
                .replace("能见度和RVR转至550以上", "能见度550-3000，RVR550-2000")
                .replace("能见度和RVR短时300-550", "能见度~300-550，RVR~300-550")
                .replace("能见度和RVR短时200-300", "能见度~200-300，RVR~200-300")
                .replace("能见度和RVR500-700", "能见度500-700，RVR500-700")
                .replace("能见度和RVR200-500", "能见度200-500，RVR200-500")
                // 能见度
                .replace("能见度5km以上", "能见度5000-10000")
                .replace("能见度上升至1500以上", "能见度1500-3000")
                .replace("能见度上升到1000以上", "能见度1000-3000")
                .replace("能见度上升至1000以上", "能见度1000-3000")
                .replace("能见度转至1000以上", "能见度1000-3000")
                .replace("能见度1000以上", "能见度1000-2000")
                .replace("能见度上升至800以上", "能见度800-3000")
                .replace("能见度转至800以上", "能见度800-3000")
                .replace("能见度转至600以上", "能见度600-3000")
                .replace("期间能见度500-800", "能见度500-800")

                .replace("能见度最低下降至400-600", "能见度400-600")
                .replace("能见度低于800", "能见度0-800")

                // RVR
                .replace("RVR21:48-25日03:00为300-550", "21:48-25日03:00RVR300-550")
                .replace("RVR300-550，短时低于300", "RVR300-550~0-300")
                .replace("RVR上升至1000以上", "RVR1000-2000")
                .replace("RVR最低下降至350-550", "RVR350-550")
                .replace("RVR上升至600以上", "RVR600-2000")
                .replace("RVR上升至550以上", "RVR550-2000")
                .replace("RVR抬升至550以上", "RVR550-2000")
                .replace("RVR转至550以上", "RVR550-2000")
                .replace("RVR550，二、四跑道RVR短时150-350", "RVR550~150-350")
                .replace("RVR均转至1000以上", "RVR1000-2000")
                .replace("RVR短时出现350-550", "RVR~350-550")
                .replace("RVR将下降至350-550", "RVR350-550")
                .replace("RVR下降至350-550", "RVR350-550")
                .replace("RVR短时550-800", "RVR~550-800")
                .replace("RVR短时350-550", "RVR~350-550")

        ;
        tenList.add(nine);
    }


}
