package com.emdata.messagewarningscore.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.AlertOriginalContentMapper;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.data.service.AlertOriginalContentService;
import com.emdata.messagewarningscore.data.util.ReadFromMhtUtil;
import com.emdata.messagewarningscore.data.util.StringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Desc AlertOriginalContentServiceImpl
 * @Author lihongjiang
 * @Date 2021/1/15 13:34
 **/
@Slf4j
@Service
public class AlertOriginalContentServiceImpl extends ServiceImpl<AlertOriginalContentMapper, AlertOriginalTextDO> implements AlertOriginalContentService {

    /**
     * 解析警报原始文本
     * @param folder    文件夹
     * @param fileName  文件名 不传时，解析文件夹下所有文件。
     **/
    @Override
    public ResultData<List<AlertOriginalTextDO>> parseOriginalText(String folder, String fileName) {
        List<File> files = Lists.newArrayList();
        if (StringUtils.isNotBlank(fileName)) {
            files.add(new File(folder.concat("\\").concat(fileName).concat(".doc")));
        }else{
            File fileObj = new File(folder);
            File[] fileArr = fileObj.listFiles();
            if (null != fileArr && fileArr.length>0) {
                files.addAll(Arrays.asList(fileArr));
            }
        }
        if (files.size()<1) {
            return ResultData.error("文件不能为空!");
        }
        // 原始文本对象
        List<AlertOriginalTextDO> alertOriginalTexts = Lists.newArrayList();
        for (File file : files) {
            String name = file.getName();
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 查询该文件名是否存在，存在则结束当前循环，不存在则继续执行。
            LambdaQueryWrapper<AlertOriginalTextDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.like(true, AlertOriginalTextDO::getAlertFileName, name);
            AlertOriginalTextDO one = this.getOne(lambdaQueryWrapper);
            if (null != one) {
                continue;
            }
            // 获取文件名中的警报类型
            String type = name.split("\\.")[1];
            if ("jcjb".equals(type)) {
                alertOriginalTexts.add(parseAirportOriginalText(in, name));
            }else if ("zdqtqyj".equals(type)) {
                alertOriginalTexts.add(parseTerminalOriginalText(in, name));
            }else if ("zytqkb".equals(type)) {
                alertOriginalTexts.add(parseAreaOriginalText(in, name));
            }
        }
        // 批量保存警报的原始文本
        if (alertOriginalTexts.size() > 0) {
            try {
                this.saveBatch(alertOriginalTexts);
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return ResultData.success(alertOriginalTexts);
    }

    /**
     * 解析机场的警报原始文本
     *
     * 机场警报评定的重要天气包含
     * 雷暴（对流）、低云（云高或垂直能见度＜60 米且云量在 BKN 及以上的云组）、
     * 低能见度（＜800 米）、降雪和冻降水。
     *
     * @param in 输入流
     */
    public AlertOriginalTextDO parseAirportOriginalText(InputStream in, String name) {
        // 读取mht文件内容
        String readFromMht = ReadFromMhtUtil.readFromMht(in);
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
        String releaseTitle = "";
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
                releaseTitle = textTitle;
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
        airportAlert.setReleaseTitle(releaseTitle);
        airportAlert.setAlertFileName(name);
        airportAlert.setTextContent(textContent.toString());
        return airportAlert;
    }

    /**
     * 解析终端区的警报原始文本
     */
    public AlertOriginalTextDO parseTerminalOriginalText(InputStream in, String name) {
        // 读取mht文件内容
        String readFromMht = ReadFromMhtUtil.readFromMht(in);
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
        StringBuilder stringBuilder = new StringBuilder();
        for (String row : rows) {
            stringBuilder.append(row);
        }
        String releaseTitle = "【终端区天气预警】";
        String contents = stringBuilder.toString();
        if (contents.contains(releaseTitle)) {
            contents = releaseTitle + contents.split(releaseTitle)[1];
        }
        // 内容
        String textContent = StringUtil.interceptByKey(contents, releaseTitle, "。");
        // 发布时间
        String[] split1 = contents.split("。");
        String dataStr = split1[split1.length - 1];
        String str = dataStr.substring(dataStr.length()-11).replace("月", "-").replace("日", " ");
        String ymdhm = name.substring(0, 4) + "-" + str;
        Date releaseTime = Java8DateUtils.parseDateTimeStr(ymdhm, Java8DateUtils.DATE_TIME_WITHOUT_SECONDS);
        String airportName = "";
        String airportCode = "";
        String weatherStationName = "";
        String releaseSequenceNum = "";
        String releaseTimeZone = "北京时";
        // 封装参数
        AlertOriginalTextDO airportAlert = new AlertOriginalTextDO();
        airportAlert.setAlertType(2);
        airportAlert.setAirportName(airportName);
        airportAlert.setAirportCode(airportCode);
        airportAlert.setReleaseTime(releaseTime);
        airportAlert.setReleaseTimeZone(releaseTimeZone);
        airportAlert.setWeatherStationName(weatherStationName);
        airportAlert.setReleaseSequenceNum(releaseSequenceNum);
        airportAlert.setAlertFileName(name);
        airportAlert.setReleaseTitle(releaseTitle);
        airportAlert.setTextContent(textContent.replace(" ", ""));
        return airportAlert;
    }

    /**
     * 解析区域的警报原始文本（文件夹名称：重要天气预警文本）
     */
    public AlertOriginalTextDO parseAreaOriginalText(InputStream in, String name) {
        // 读取mht文件内容
        String readFromMht = ReadFromMhtUtil.readFromMht(in);
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
        StringBuilder stringBuilder = new StringBuilder();
        for (String row : rows) {
            stringBuilder.append(row);
        }
        String releaseTitle = "【重要天气快报】";
        String contents = stringBuilder.toString();
        if (contents.contains(releaseTitle)) {
            contents = releaseTitle + contents.split(releaseTitle)[1];
        }
        // 内容
        String textContent = StringUtil.interceptByKey(contents, releaseTitle, "。");
        // 发布时间
        String[] split1 = contents.split("。");
        String dataStr = split1[split1.length - 1];
        String str = dataStr.substring(dataStr.length()-11).replace("月", "-").replace("日", " ");
        String ymdhm = name.substring(0, 4) + "-" + str;
        Date releaseTime = Java8DateUtils.parseDateTimeStr(ymdhm, Java8DateUtils.DATE_TIME_WITHOUT_SECONDS);
        String airportName = "";
        String airportCode = "";
        String weatherStationName = "";
        String releaseSequenceNum = "";
        String releaseTimeZone = "北京时";
        // 封装参数
        AlertOriginalTextDO airportAlert = new AlertOriginalTextDO();
        airportAlert.setAlertType(3);
        airportAlert.setAirportName(airportName);
        airportAlert.setAirportCode(airportCode);
        airportAlert.setReleaseTime(releaseTime);
        airportAlert.setReleaseTimeZone(releaseTimeZone);
        airportAlert.setWeatherStationName(weatherStationName);
        airportAlert.setReleaseSequenceNum(releaseSequenceNum);
        airportAlert.setAlertFileName(name);
        airportAlert.setReleaseTitle(releaseTitle);
        airportAlert.setTextContent(textContent.replace(" ", ""));
        return airportAlert;
    }

}
