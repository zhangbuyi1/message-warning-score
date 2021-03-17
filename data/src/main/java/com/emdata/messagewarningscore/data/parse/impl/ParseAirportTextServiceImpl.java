package com.emdata.messagewarningscore.data.parse.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.AirportAreaTerminalDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.enums.WarningNatureEnum;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommonAdaptee2;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommonAdapter;
import com.emdata.messagewarningscore.data.parse.interfase.ParseTextService;
import com.emdata.messagewarningscore.data.util.ReadFromMhtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 机场警报实现类
 * @date: 2020/12/10
 * @author: lihongjiang
 */
@Slf4j
@Service("airportShImpl")
public class ParseAirportTextServiceImpl extends AlertCommonAdaptee2 implements ParseTextService {

    @Autowired
    private AlertCommonAdapter alertCommonAdapter;



    /**
     * 伴随天气(关键字)
     *
     * 风向 风速 以后 维持 西北风 阵风
     * 1、若有取消，则用取消分割；
     * 0下标中，若有（霜、云底高、能见度、风）字眼，则获取字眼后面(包含字眼)的所有内容，最终得到【伴随天气】。
     * 2、若无取消，则用预计分割；
     * 1下标中，若有（将出现）字眼，则获取字眼后面(不包含字眼)的所有内容；若有（云底高）字眼，则获取字眼后面(包含字眼)的所有内容，并根据文档情况判断天气；
     * */
    @Override
    public ResultData<List<AlertContentResolveDO>> getImportantParam(AlertOriginalTextDO airportAlert) {
        // 警报类型 1机场警报 2终端区警报 3区域警报
        alertType.set("1");
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
        // 多个预计时，把从第二个开始，后面的预计没有获取到的数据，从第一个获取。
        if (alertContents.size()>1) {
            initOriginalData(alertContents);
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

    public void initWeatherContent(List<AlertContentResolveDO> alertContents, AlertOriginalTextDO airportAlert){
        // 原始内容
        String originalContent = airportAlert.getTextContent();
        // 若存在"更正"，则加上"更正，"
        originalContent = getCorrectedContent(originalContent);
        // 去掉"强度维持"
        originalContent = deleteOtherKeys(originalContent);
        // 转换"预计"文本
        String original = super.expectedAirport(originalContent);
        String[] split = original.split("预计");
        for (int i = 1; i < split.length; i++) {
            // 0、添加文本内容"在"、"有"、"天气"等
            String splitOne = super.addTextContent(split[0], original);
            // 1、原始子文本
            String originalText = splitOne + "预计" + split[i];
            // 2、让原始文本具有可读性
            String textContent = super.getReadableTextContent(originalText);
            // 3、低能低见处理
            textContent = super.lowVisibilityHandle(textContent);
            // 4、设置可读性文本
            contentThreadLocal.set(textContent);
            // 5、设置预计个数（当前是第几个，默认为0）
            numThreadLocal.set(i);
            // 天气类型 1雷暴 2能见度 3低云 4降雪 5风
            alertCommonAdapter.adapterInterfaceImpl(alertContents, airportAlert, "机场");
        }
    }

    public void initOriginalData(List<AlertContentResolveDO> alertContents){
        // 多个预计时，后面为空的值，从第一个取。
        for (int i=1; i<alertContents.size(); i++) {
            AlertContentResolveDO airportAlertContent1 = alertContents.get(0);
            AlertContentResolveDO airportAlertContent2 = alertContents.get(i);
            // 设置范围
            if (StringUtils.isBlank(airportAlertContent2.getPredictRange())) {
                airportAlertContent2.setPredictRange(airportAlertContent1.getPredictRange());
            }
            // 设置子天气类型
            if (StringUtils.isBlank(airportAlertContent2.getSubWeatherType())) {
                airportAlertContent2.setSubWeatherType(airportAlertContent1.getSubWeatherType());
            }
//            // 设置天气名称
//            if (StringUtils.isBlank(airportAlertContent2.getWeatherName()) && StringUtils.isNotBlank(airportAlertContent2.getAdditionalName())) {
//                airportAlertContent2.setWeatherName(airportAlertContent1.getWeatherName());
//                if(StringUtils.isBlank(airportAlertContent2.getWeatherMinNum())
//                        && StringUtils.isBlank(airportAlertContent2.getWeatherMaxNum())){
//                    // 设置最小/最大天气值
//                    airportAlertContent2.setWeatherMinNum(airportAlertContent1.getWeatherMinNum());
//                    airportAlertContent2.setWeatherMaxNum(airportAlertContent1.getWeatherMaxNum());
//                }
//            }
        }
//        // 将主天气和附加天气转成json（先存jsonStr试验）
//        for (AlertContentDO alertContent : alertContents) {
//            Map<String, Object> hashMap = new HashMap<>(4);
//            // 先添加主要天气
//            List<Map<String, Object>> mainWeathers = Lists.newArrayList();
//            hashMap.put("describe", alertContent.getWeatherName());
//            hashMap.put("code", "");
//            hashMap.put("min", alertContent.getWeatherMinNum());
//            hashMap.put("max", alertContent.getWeatherMaxNum());
//            mainWeathers.add(hashMap);
//            // 再添加附加天气
//            List<Map<String, Object>> addWeathers = Lists.newArrayList();
//            hashMap.put("describe", alertContent.getAdditionalName());
//            hashMap.put("code", "");
//            hashMap.put("min", alertContent.getAdditionalMinNum());
//            hashMap.put("max", alertContent.getAdditionalMaxNum());
//            addWeathers.add(hashMap);
//            // 最后添加伴随天气
//            String accompanyWeather = alertContent.getAccompanyWeather();
//            if (StringUtils.isNotBlank(accompanyWeather)) {
//                String[] split = accompanyWeather.split("、");
//                for (String str : split) {
//                    hashMap.put("describe", str);
//                    hashMap.put("code", "");
//                    hashMap.put("min", "");
//                    hashMap.put("max", "");
//                    addWeathers.add(hashMap);
//                }
//            }
//            alertContent.setMainWeatherJson(mainWeathers);
//            alertContent.setAddiWeatherJson(addWeathers);
//        }

    }

    /**
     * 解析机场警报文本（孙铭写的：暂停使用）
     *
     * @param in 输入流
     * @return 1
     */
    public List<AirportAreaTerminalDO> parse(InputStream in) {
        // 读取mht文件内容
        String readFromMht = ReadFromMhtUtil.readFromMht(in);
        System.out.println(readFromMht);
        String[] split = readFromMht.split("\r\n|\n");
        // 分类字段
        List<String> rows = Arrays.stream(split).filter(a -> {
            a = a.replaceAll(" ", " ");
            return StringUtils.isNotBlank(a);
        }).collect(Collectors.toList());
        System.out.println(rows);
        List<AirportAreaTerminalDO> dos = new ArrayList<>();
        AirportAreaTerminalDO airportAreaTerminalDO = new AirportAreaTerminalDO();
        airportAreaTerminalDO.setWarningType(WarningTypeEnum.AIRPORT_WARNING.getWarningType());
        for (int i = 0; i < rows.size(); i++) {
            String row = rows.get(i);
            if (i == 0) {
                if (row.contains("机场")) {
                    String airportName = row.substring(0, row.indexOf("机场") + 2);
                    airportAreaTerminalDO.setAirportName(airportName);
                    continue;
                }
            }
            if (row.contains("发布时间")) {
                // 将中文标点符号转为英文的
                row = row.replace("：", ":").replace("（", "(").replace("）", ")");
                String releaseTime = row.substring(row.indexOf(":") + 1, row.indexOf("("));
                Date releaseDateTime = Java8DateUtils.parseDateTimeStr(releaseTime, Java8DateUtils.DATE_TIME_WITHOUT_SECONDS);
                airportAreaTerminalDO.setReleaseTime(releaseDateTime);
                continue;
            }
            // 最后一个是警报文本描述
            if (i == rows.size() - 1) {
                List<AirportAreaTerminalDO> areaTerminalDOS = parseAirportText(row, airportAreaTerminalDO);
                dos.addAll(areaTerminalDOS);
            }
        }
        return dos;
    }

    /**
     * 解析机场警报文本描述内容
     *
     * @param text                  文本
     * @param airportAreaTerminalDO do实体类
     * @return 赋值后的do实体类
     */
    public static List<AirportAreaTerminalDO> parseAirportText(String text, AirportAreaTerminalDO airportAreaTerminalDO) {
        List<AirportAreaTerminalDO> areaTerminalDOS = new ArrayList<>();
        // 得到发布时间
        Date releaseTime = airportAreaTerminalDO.getReleaseTime();
        // 将发布时间转为yyyy-MM-dd HH:mm:ss字符串
        String releaseTimeStr = Java8DateUtils.format(releaseTime, Java8DateUtils.DATE_TIME);
        // 如果文本中不包含取消
        if (text.contains("取消")) {
            airportAreaTerminalDO.setWarningNature(WarningNatureEnum.UPDATE.getMessage());
        } else {
            airportAreaTerminalDO.setWarningNature(WarningNatureEnum.FIRST.getMessage());
        }
        // 提取文本预报时间段

        // 获取到预报开始时间和结束时间
//        AreaAndTimeBO.StartAndEndTimeBO startAndEndTimeBO = ParseTextTimeUtil.obtainTime(releaseTimeStr, null);
//        airportAreaTerminalDO.setPredictStartTime(startAndEndTimeBO.getStartTime());
//        airportAreaTerminalDO.setPredictEndTime(startAndEndTimeBO.getEndTime());
//        areaTerminalDOS.add(airportAreaTerminalDO);
        return areaTerminalDOS;
    }

}
