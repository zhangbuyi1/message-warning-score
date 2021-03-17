package com.emdata.messagewarningscore.data.service2.impl.type;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.data.dao.entity.TextContentTemplateDO;
import com.emdata.messagewarningscore.data.service2.AirportWeatherService;
import com.emdata.messagewarningscore.data.service2.ParseTextContentService;
import com.emdata.messagewarningscore.data.service2.impl.adaptee.ParseAlertTextAdaptee;
import com.emdata.messagewarningscore.data.service2.impl.airport.*;
import com.emdata.messagewarningscore.data.util.ReadFromMhtUtil;
import com.emdata.messagewarningscore.data.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Desc 解析预警文本适配器（类型：机场、区域、终端...）
 * @Author lihongjiang
 * @Date 2020/12/24 10:33
 **/
@Slf4j
@Service
public class IAirportTextAdapterImpl extends ParseAlertTextAdaptee implements ParseTextContentService {

    @Override
    public ResultData<AlertOriginalTextDO> fileFormatConver(FileInputStream fileInput) {
        // 读取mht文件内容
        String readFromMht = ReadFromMhtUtil.readFromMht(fileInput);
        log.debug("读取mht文件，原内容:{}", readFromMht);
        String[] split = readFromMht.split("\r\n|\n");
        // 分类字段
        List<String> rows = Arrays.stream(split).filter(a -> {
            a = a.replaceAll(" ", " ");
            return StringUtils.isNotBlank(a);
        }).collect(Collectors.toList());
        log.debug("读取mht文件，分类后:{}", rows);
        if (rows.size() < 1) {
            return null;
        }
        String airportName = "";
        String airportCode = "";
        String weatherStationName = "";
        String airportAlertFileName = "";
        String releaseSequenceNum = "";
        Date releaseTime = null;
        String releaseTimeZone = "";
        StringBuilder textContent = new StringBuilder();
        for(int i=0; i<rows.size(); i++){
            // 0机场
            if (i == 0) {
                String textTitle = rows.get(i);
                if (textTitle.contains("机场")) {
                    String[] airport = textTitle.split("机场");
                    airportName = airport[0] + "机场";
                    // 通过机场名称匹配机场编号（未编写）
                    airportCode = "";
                }
                airportAlertFileName = textTitle;
            }
            // 1名称
            if (i == 1) {
                weatherStationName = rows.get(i);
            }
            // 2序列号
            if (i == 2) {
                String[] split1 = rows.get(i).split("：");
                releaseSequenceNum = split1[1];
            }
            // 3时间
            if (i == 3) {
                String str = rows.get(3);
                String[] split2 = str.split("：");
                String timeStr = split2[1];
                if (timeStr.contains("（")) {
                    String[] times = timeStr.split("（");
                    String time = times[0];
                    if (time.contains(":")) {
                        releaseTime = Java8DateUtils.parseDateTimeStr(times[0], Java8DateUtils.DATE_TIME_WITHOUT_SECONDS);
                    }
                    String timeZone = times[1];
                    if (timeZone.contains("）")) {
                        releaseTimeZone = timeZone.replace("）", "");
                    }
                }
            }
            // 4及以上为内容
            if (i > 3) {
                textContent.append(rows.get(i));
            }
        }
        // 封装参数
        AlertOriginalTextDO airportAlert = new AlertOriginalTextDO();
        airportAlert.setAlertType(1);
        airportAlert.setAirportName(airportName);
        airportAlert.setAirportCode(airportCode);
        airportAlert.setReleaseTime(releaseTime);
        airportAlert.setReleaseTimeZone(releaseTimeZone);
        airportAlert.setWeatherStationName(weatherStationName);
        airportAlert.setReleaseSequenceNum(releaseSequenceNum);
        airportAlert.setAlertFileName(airportAlertFileName);
        airportAlert.setTextContent(textContent.toString());
        return ResultData.success(airportAlert);
    }

    @Override
    public ResultData<List<AlertContentResolveDO>> parseTextContent(AlertOriginalTextDO airportAlert) {
        // 从配置表中，查询模板
        List<TextContentTemplateDO> textContentTemplates = super.getTextContentTemplate("1");
        if (CollectionUtils.isEmpty(textContentTemplates)) {
            return ResultData.error("未查询到模板!");
        }
        // 文本内容
        String textContent = airportAlert.getTextContent();
        // 匹配模板，获取天气类型
        String weatherType = "";
        for (TextContentTemplateDO textContentTemplate : textContentTemplates) {
            String templateContent = textContentTemplate.getTemplateContent();
            if (templateContent.contains("__")) {
                List<String> strings = Arrays.asList(templateContent.split("__"));
                // 匹配天气模板（1雷暴 2能见度 3低云 4降雪 5风）
                Boolean existByKeyWord = StringUtil.isExistByKeyWord(textContent, strings);
                if (existByKeyWord) {
                    weatherType = textContentTemplate.getWeatherType();
                    break;
                }
            }
        }
        if (StringUtils.isBlank(weatherType)) {
            return ResultData.error("未匹配到模板!");
        }
        // 解析接口 天气类型 1雷暴 2能见度 3低云 4降雪 5风
        AirportWeatherService airportWeatherService = null;
        if ("1".equals(weatherType)) {
            airportWeatherService = new AirportThunderstormImpl();
        }else if("2".equals(weatherType)){
            airportWeatherService = new AirportVisibilityImpl();
        }else if("3".equals(weatherType)){
            airportWeatherService = new AirportLowCloudImpl();
        }else if("4".equals(weatherType)){
            airportWeatherService = new AirportSnowfallImpl();
        }else if("5".equals(weatherType)){
            airportWeatherService = new AirportWindImpl();
        }
        // 接口未成功加载，则不需要执行。
        if (null == airportWeatherService) {
            return ResultData.error("未加载到接口的实现类，请检查类型参数!");
        }
        // 解析文本内容，并返回
        List<AlertContentResolveDO> alertContents = airportWeatherService.parseTextContent(airportAlert);
        return ResultData.success(alertContents);
    }

}
