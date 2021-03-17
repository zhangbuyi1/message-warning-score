package com.emdata.messagewarningscore.data.parse.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;
import com.emdata.messagewarningscore.common.service.IAirportService;
import com.emdata.messagewarningscore.common.service.ILocationInfoService;
import com.emdata.messagewarningscore.common.service.ILocationService;
import com.emdata.messagewarningscore.data.dao.entity.Weather;
import com.emdata.messagewarningscore.data.parse.bo.AreaAndTimeBO;
import com.emdata.messagewarningscore.data.parse.bo.MdrsTextBO;
import com.emdata.messagewarningscore.data.parse.impl.lhj.common.AlertCommon;
import com.emdata.messagewarningscore.data.parse.interfase.ParseMdrsInterface;
import com.emdata.messagewarningscore.data.service.AlertOriginalContentService;
import com.emdata.messagewarningscore.data.util.ParseTextTimeUtil;
import com.emdata.messagewarningscore.data.util.ReadFromRtfUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 解析mdrs文本实现类
 * @date: 2020/12/10
 * @author: sunming
 */
@Service("mdrsShImpl")
public class ParseMdrsTextImpl implements ParseMdrsInterface {

    @Autowired
    private ILocationService iLocationService;

    @Autowired
    private ILocationInfoService iLocationInfoService;

    @Autowired
    private IAirportService iAirportService;

    @Autowired
    private AlertOriginalContentService alertOriginalContentService;

    /**
     * 解析mdrs文本
     *
     * @param in 输入流
     * @return 解析后的do实体类集合
     */
    @Override
    public List<AlertContentResolveDO> parseMdrsText(InputStream in) {
        // 1、获取用流读出来的rtf内容代码
        String rtf = readFromInputStream(in);
        // 2、获取表格上边的解析后的纯文本内容
        String rtfHeaderText = ReadFromRtfUtil.readTitleFromRtf(rtf);
        // 3、封装地区和时间bo
        AreaAndTimeBO areaAndTimeBO = toAreaAndTimeBO(rtfHeaderText);
        // 4、获取表格内的纯文本内容
        List<String> textFromRtf = ReadFromRtfUtil.getTextFromRtf(rtf);
        // 存储原始文本
        saveOriginalText(rtfHeaderText, textFromRtf, areaAndTimeBO);
        // 5、封装解析后的实体类
        List<MdrsTextBO> mdrsTextBOS = convertToBO(textFromRtf, areaAndTimeBO);
        // 6、转换成入库的DO
        List<AlertContentResolveDO> alertContentResolveDOS = convertToAlertContentDO(mdrsTextBOS);
        return alertContentResolveDOS;
    }

    /**
     * 存储原始文本
     *
     * @param rtfHeaderText
     * @param textFromRtf
     * @param areaAndTimeBO
     */
    public void saveOriginalText(String rtfHeaderText, List<String> textFromRtf, AreaAndTimeBO areaAndTimeBO) {
        StringBuilder builder = new StringBuilder();
        builder.append(rtfHeaderText).append("；");
        for (String s : textFromRtf) {
            builder.append(s).append("；");
        }
        String content = builder.toString();
        AlertOriginalTextDO originalTextDO = new AlertOriginalTextDO();
        originalTextDO.setAlertType(4);
        originalTextDO.setReleaseTitle(areaAndTimeBO.getTitle());
        originalTextDO.setReleaseTime(ParseTextTimeUtil.obtainReleaseTime(areaAndTimeBO.getReleaseTime()));
        originalTextDO.setTextContent(content);
        if (StringUtils.isNotBlank(rtfHeaderText) && CollectionUtils.isNotEmpty(textFromRtf)) {
            originalTextDO.setStatus(1);
        } else {
            originalTextDO.setStatus(2);
        }
        alertOriginalContentService.save(originalTextDO);
    }

    /**
     * 转换为入库的DO
     *
     * @param mdrsTextBOS
     * @return
     */
    public List<AlertContentResolveDO> convertToAlertContentDO(List<MdrsTextBO> mdrsTextBOS) {
        List<AlertContentResolveDO> alertContentResolveDOS = new ArrayList<>();
        for (MdrsTextBO mdrsTextBO : mdrsTextBOS) {
            AlertContentResolveDO alertContentResolveDO = new AlertContentResolveDO();
            String airportCode = mdrsTextBO.getAirportCode();
            // 查询location_info_id
            List<LocationDO> locationDOS = iLocationService.findByAirportCode(airportCode);
            if (CollectionUtils.isNotEmpty(locationDOS)) {
                for (LocationDO locationDO : locationDOS) {
                    // 分终端区和机场
                    if (mdrsTextBO.getAffectedRange().contains("终端区") && locationDO.getLocationName().contains("终端区")) {
                        LocationInfoDO locationInfoDO = iLocationInfoService.findByName(locationDO.getLocationName());
                        Integer locationInfoId = Optional.ofNullable(locationInfoDO).map(LocationInfoDO::getId).orElse(0);
                        alertContentResolveDO.setLocationInfoId(locationInfoId);
                    } else if (mdrsTextBO.getAffectedRange().contains("本场") && locationDO.getLocationName().contains("机场")) {
                        LocationInfoDO locationInfoDO = iLocationInfoService.findByName(locationDO.getLocationName());
                        Integer locationInfoId = Optional.ofNullable(locationInfoDO).map(LocationInfoDO::getId).orElse(0);
                        alertContentResolveDO.setLocationInfoId(locationInfoId);
                    } else {
                        LocationInfoDO locationInfoDO = iLocationInfoService.findByName(locationDO.getLocationName());
                        Integer locationInfoId = Optional.ofNullable(locationInfoDO).map(LocationInfoDO::getId).orElse(0);
                        alertContentResolveDO.setLocationInfoId(locationInfoId);
                    }
                    alertContentResolveDO.setReleaseTitle(mdrsTextBO.getTitle());
                    alertContentResolveDO.setAirportCode(airportCode);
                    alertContentResolveDO.setAirportName(iAirportService.findByAirportCode(airportCode).getAirportName());
                    alertContentResolveDO.setReleaseTime(mdrsTextBO.getReleaseTime());
                    alertContentResolveDO.setPredictStartTime(mdrsTextBO.getPredictStartTime());
                    alertContentResolveDO.setPredictEndTime(mdrsTextBO.getPredictEndTime());
                    alertContentResolveDO.setPredictRange(mdrsTextBO.getAffectedRange());
                    alertContentResolveDO.setWeatherType(mdrsTextBO.getSeriousWeather().getCode());
                    alertContentResolveDO.setWeatherName(mdrsTextBO.getWeather());
                    alertContentResolveDO.setMainWeatherJson(setForecastWeather(mdrsTextBO.getWeather()));
                    alertContentResolveDO.setPredictProbility(mdrsTextBO.getPredictProbility());
                    alertContentResolveDOS.add(alertContentResolveDO);
                }
            }
        }
        return alertContentResolveDOS;
    }

    /**
     * 转换天气为集合
     *
     * @param weather
     * @return
     */
    public List<ForcastWeather> setForecastWeather(String weather) {
        List<ForcastWeather> forcastWeathers = new ArrayList<>();
        String[] weathers = weather.split("，");
        for (String w : weathers) {
            List<Weather> sorts = Lists.newArrayList(AlertCommon.weathers);
            sorts.sort(Comparator.comparing(Weather::getSort));
            for (Weather sort : sorts) {
                Optional<String> first = Arrays.stream(sort.getNames().split(",")).filter(w::contains).findFirst();
                if (first.isPresent()) {
                    String describe = first.get();
                    ForcastWeather forcastWeather = new ForcastWeather();
                    forcastWeather.setDescribe(describe);
                    forcastWeather.setCode(sort.getCode());
                    forcastWeather.setPriority(sort.getPriority());
                    w = w.replace(describe, "");
                    if (StringUtils.isNotBlank(w)) {
                        if (w.contains("-")) {
                            String[] split1 = w.split("-");
                            forcastWeather.setMin(Integer.parseInt(split1[0]));
                            forcastWeather.setMax(Integer.parseInt(split1[1]));
                        }
                    }
                    forcastWeathers.add(forcastWeather);
                }
            }
        }
        return forcastWeathers;
    }

    /**
     * 封装解析mdrs文本后的对象
     *
     * @param texts
     * @return
     */
    public List<MdrsTextBO> convertToBO(List<String> texts, AreaAndTimeBO areaAndTimeBO) {
        List<MdrsTextBO> mdrsTextBOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(texts)) {
            for (int i = 1; i < texts.size(); i++) {
                String text = texts.get(i);
                String[] split = text.split("\n");

                // 获取时间格式的发布时间，预报开始和结束时间
                String existTime = split[2].equals("NA") ? "" : split[2].trim();
                List<AreaAndTimeBO.StartAndEndTimeBO> startAndEndTimeBO = ParseTextTimeUtil.obtainTime(areaAndTimeBO.getReleaseTime(), existTime);
                for (AreaAndTimeBO.StartAndEndTimeBO andEndTimeBO : startAndEndTimeBO) {
                    MdrsTextBO mdrsTextBO = new MdrsTextBO();
                    mdrsTextBO.setTitle(areaAndTimeBO.getTitle());
                    mdrsTextBO.setArea(areaAndTimeBO.getArea());
                    mdrsTextBO.setReleaseTime(andEndTimeBO.getReleaseTime());
                    mdrsTextBO.setPredictStartTime(andEndTimeBO.getStartTime());
                    mdrsTextBO.setPredictEndTime(andEndTimeBO.getEndTime());
                    mdrsTextBO.setAirportCode(split[0].equals("NA") ? "" : split[0].trim());
                    mdrsTextBO.setWeather(split[1].equals("NA") ? "" : split[1].trim());
                    EvaluationWeather evaluationWeather = setImportantWeather(mdrsTextBO.getWeather());
                    mdrsTextBO.setSeriousWeather(evaluationWeather);
                    mdrsTextBO.setAffectedRange(split[3].equals("NA") ? "" : split[3].trim());
                    mdrsTextBO.setPredictProbility(split[4].equals("NA") ? "" : split[4].trim());
                    mdrsTextBO.setRemark(split[5].equals("NA") ? "" : split[5].trim());
                    mdrsTextBOS.add(mdrsTextBO);
                }
            }
        }
        // 如果机场编码没有，则用前一个的
        for (int i = 0; i < mdrsTextBOS.size(); i++) {
            MdrsTextBO bo = mdrsTextBOS.get(i);
            if (StringUtils.isBlank(bo.getAirportCode())) {
                bo.setAirportCode(mdrsTextBOS.get(i - 1).getAirportCode());
            }
        }
        mdrsTextBOS = mdrsTextBOS.stream().filter(a -> !a.getWeather().matches("无重要天气")).collect(Collectors.toList());
        return mdrsTextBOS;
    }

    public EvaluationWeather setImportantWeather(String weather) {
        if (weather.contains("对流") || weather.contains("雷暴")) {
            return EvaluationWeather.TS;
        } else if (weather.contains("雨") || weather.contains("降水")) {
            return EvaluationWeather.RA;
        } else if (weather.contains("降雪") || weather.contains("冻降水")) {
            return EvaluationWeather.RN_FZRA;
        } else if (weather.contains("能见度") || weather.contains("云底高") || weather.contains("RVR") || weather.contains("rvr")) {
            return EvaluationWeather.LOW_VIS_LOWCLOUD;
        }
        return null;
    }

    /**
     * 解析文本中地区和制作时间实体类
     *
     * @param rtfHeaderText
     * @return
     */
    public static AreaAndTimeBO toAreaAndTimeBO(String rtfHeaderText) {
        AreaAndTimeBO areaAndTimeBO = new AreaAndTimeBO();
        String[] split = rtfHeaderText.split("\n");
        for (String s : split) {
            if (s.contains("MDRS")) {
                areaAndTimeBO.setTitle(s);
            }
            if (s.contains("地区")) {
                String[] s1 = s.split(" ");
                List<String> collect = Arrays.stream(s1).filter(a -> {
                    return !a.equals("");
                }).collect(Collectors.toList());
                String area = collect.get(0);
                areaAndTimeBO.setArea(area.substring(3));
                String time = collect.get(1);
                areaAndTimeBO.setReleaseTime(time.substring(5));
            }
        }
        return areaAndTimeBO;
    }

    /**
     * 从输入流中读取rtf内容
     *
     * @param in
     * @return
     */
    public static String readFromInputStream(InputStream in) {
        InputStreamReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            br.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
