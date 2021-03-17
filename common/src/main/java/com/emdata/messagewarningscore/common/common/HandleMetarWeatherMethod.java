package com.emdata.messagewarningscore.common.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emdata.messagewarningscore.common.accuracy.config.RaConfig;
import com.emdata.messagewarningscore.common.accuracy.config.RaSnConfig;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.MetarSourceMapper;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.metar.entity.ChildCloud;
import com.emdata.messagewarningscore.common.metar.entity.Rvr;
import com.emdata.messagewarningscore.common.metar.entity.Vis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 处理metar天气拼接方法类
 * @date: 2021/1/5
 * @author: sunming
 */
@Component
public class HandleMetarWeatherMethod {

    @Autowired
    private MetarSourceMapper metarSourceMapper;

    public String handleWeather(String airportCode, Date startTime, Date endTime, EvaluationWeather evaluationWeather) {
        String weather = "";
        // 查询metarSource
        LambdaQueryWrapper<MetarSourceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MetarSourceDO::getAirportCode, airportCode);
        lqw.ge(MetarSourceDO::getMetarTime, startTime);
        lqw.le(MetarSourceDO::getMetarTime, endTime);
        List<MetarSourceDO> metarSourceDOS = metarSourceMapper.selectList(lqw);
        if (!CollectionUtils.isEmpty(metarSourceDOS)) {
            List<Metar> metars = metarSourceDOS.stream().map(a -> {
                return new Metar(a.getMetarSource(), a.getMetarTime());
            }).collect(Collectors.toList());
            // 雷暴
            if (evaluationWeather.getCode() == 1) {

            }
            // 低云低能见度
            else if (evaluationWeather.getCode() == 2) {
                weather = handleVis(metars);
            }
            // 降雪，冻降水
            else if (evaluationWeather.getCode() == 3) {
                weather = handleSn(metars);
            }
            // 中度以上降水
            else if (evaluationWeather.getCode() == 4) {
                weather = handleRa(metars);
            }
        }
        return weather;
    }


    /**
     * 处理低云低能见度
     *
     * @param metars
     * @return
     */
    public static String handleVis(List<Metar> metars) {
        StringBuilder builder = new StringBuilder();
        // 得到能见度
        List<Vis> vis = metars.stream().map(Metar::getVis).filter(Objects::nonNull).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(vis)) {
            Optional<Vis> visOptional = vis.stream().min(Comparator.comparing(Vis::getVis));
            if (visOptional.isPresent()) {
                Vis visMin = visOptional.get();
                builder.append("能见度为：").append(visMin).append("；");
            }
        }
        // 得到rvr
        List<Rvr> rvrs = metars.stream().map(Metar::getRvr).filter(Objects::nonNull).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(rvrs)) {
            Optional<Rvr> rvrOptional = rvrs.stream().min(Comparator.comparing(Rvr::getMinRvrVis));
            if (rvrOptional.isPresent()) {
                Rvr rvr = rvrOptional.get();
                builder.append("RVR为：").append(rvr).append("；");
            }
        }
        // 得到云底高
        List<ChildCloud> cloudHeights = metars.stream().map(a -> {
            return a.getCloud().getBKNAndOVClowCloud(true);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(cloudHeights)) {
            Optional<ChildCloud> childCloud = cloudHeights.stream().min(Comparator.comparing(ChildCloud::getCloudHeight));
            if (childCloud.isPresent()) {
                Integer minCloudHeight = childCloud.get().getCloudHeight();
                builder.append("云底高为：").append(minCloudHeight).append("；");
            }
        }
        return builder.toString();
    }

    /**
     * 处理降雪，冻降水
     *
     * @param
     * @return
     */
    public static String handleSn(List<Metar> metars) {
        StringBuilder builder = new StringBuilder();
        List<String> weathers = new ArrayList<>();
        RaSnConfig raSnConfig = new RaSnConfig();
        List<String> rasns = raSnConfig.getRasns();
        for (Metar metar : metars) {
            String weather = metar.getWeatherDO().getWeather();
            String[] split = weather.split(" ");
            String w = contains(rasns, split);
            if (w != null && !weathers.contains(w)) {
                weathers.add(w);
            }
        }
        // 拼接天气
        if (!CollectionUtils.isEmpty(weathers)) {
            for (String weather : weathers) {
                builder.append(weather).append(" ");
            }
        }
        return builder.toString().trim();
    }

    /**
     * 处理中度以上降水
     *
     * @param
     * @return
     */
    public static String handleRa(List<Metar> metars) {
        StringBuilder builder = new StringBuilder();
        List<String> weathers = new ArrayList<>();
        RaConfig raConfig = new RaConfig();
        List<String> ra = raConfig.getRa();
        for (Metar metar : metars) {
            String weather = metar.getWeatherDO().getWeather();
            String[] split = weather.split(" ");
            String w = contains(ra, split);
            if (w != null && !weathers.contains(w)) {
                weathers.add(w);
            }
        }
        // 拼接天气
        if (!CollectionUtils.isEmpty(weathers)) {
            for (String weather : weathers) {
                builder.append(weather).append(" ");
            }
        }
        return builder.toString().trim();
    }


    public static String contains(List<String> all, String[] split) {
        Optional<String> first = Arrays.stream(split).filter(s -> {
            return all.contains(s);
        }).findFirst();
        return first.orElse(null);
    }
}
