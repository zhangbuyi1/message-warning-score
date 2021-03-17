package com.emdata.messagewarningscore.common.common.utils;/**
 * Created by zhangshaohu on 2021/1/14.
 */

import com.emdata.messagewarningscore.common.accuracy.config.*;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.dao.entity.RadarDataDO;
import com.emdata.messagewarningscore.common.dao.entity.RadarEchoDO;
import com.emdata.messagewarningscore.common.enums.ThresholdEnum;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.metar.entity.ChildCloud;
import com.emdata.messagewarningscore.common.metar.entity.Cloud;
import com.emdata.messagewarningscore.common.metar.entity.WeatherDO;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/14
 * @description:
 */
public class SeriousUtils {
    public static SeriousBO isScoreThreshold(RadarDataDO radarDataDO, String airportCode, boolean ifFolating) {
        TSConfig tsConfig = ThresholdConfig.getTsConfig(airportCode);
        List<RadarEchoDO> data = radarDataDO.getData();
        Optional<RadarEchoDO> first = null;
        if (ifFolating) {
            first = data.stream().filter(s -> {
                return s.getRadarPercentage() > tsConfig.getRadarCoverage() - (tsConfig.getFloatingRange() * 1.0 / tsConfig.getRadarCoverage());
            }).findFirst();
        } else {
            first = data.stream().filter(s -> {
                return s.getRadarPercentage() > tsConfig.getRadarCoverage();
            }).findFirst();
        }
        if (first.isPresent()) {
            if (ifFolating) {
                return new SeriousBO(radarDataDO.getPicTime(), radarDataDO.getPicTime(), airportCode, EvaluationWeather.TS, "", radarDataDO.getLocationId(), ThresholdEnum.NEAR);

            }
            return new SeriousBO(radarDataDO.getPicTime(), radarDataDO.getPicTime(), airportCode, EvaluationWeather.TS, "", radarDataDO.getLocationId(), ThresholdEnum.SATISFY);
        } else {
            return null;
        }

    }


    /**
     * 判断当前metar报文产生的重要天气
     *
     * @param metarSourceDO metar报文
     * @return
     */
    public static List<SeriousBO> isSocreThreshold(MetarSourceDO metarSourceDO, Integer locationId, Boolean ifFloating) {
        List<SeriousBO> seriousDOS = new ArrayList<>();
        // 配置文件
        LowVisibilityConfig lowVisibilityConfig = ThresholdConfig.getLowVisibilityConfig(metarSourceDO.getAirportCode());

        Metar metar = new Metar(metarSourceDO.getMetarSource());
        if (!ifFloating) {
            // 低云低能见度
            // 满足阈值
            boolean lowVisNot = lowVis(metar.getVis().getVis(), lowVisibilityConfig.getLowVis(), 0);
            boolean lowCloudNot = lowCloud(metar.getCloud(), lowVisibilityConfig.getLowBknHeight(), 0);
            boolean lowRvrNot = lowRvr(metar.getRvr().getMinRvrVis(), lowVisibilityConfig.getLowRvr(), 0);
            if (lowVisNot || lowCloudNot || lowRvrNot) {
                seriousDOS.add(new SeriousBO(metarSourceDO.getMetarTime(), metarSourceDO.getMetarTime(), metarSourceDO.getAirportCode(), EvaluationWeather.LOW_VIS_LOWCLOUD, "", locationId, ThresholdEnum.SATISFY));
            }
        } else {
            // 部分满足
            boolean lowVis = lowVis(metar.getVis().getVis(), lowVisibilityConfig.getLowVis(), lowVisibilityConfig.getNearThreshold());
            boolean lowCloud = lowCloud(metar.getCloud(), lowVisibilityConfig.getLowBknHeight(), lowVisibilityConfig.getNearThreshold());
            boolean lowRvr = lowRvr(metar.getRvr().getMinRvrVis(), lowVisibilityConfig.getLowRvr(), lowVisibilityConfig.getNearThreshold());
            if (lowVis || lowCloud || lowRvr) {
                seriousDOS.add(new SeriousBO(metarSourceDO.getMetarTime(), metarSourceDO.getMetarTime(), metarSourceDO.getAirportCode(), EvaluationWeather.LOW_VIS_LOWCLOUD, "", locationId, ThresholdEnum.SATISFY));
            }
        }

        // 降雪 冻降水
        RaSnConfig raSnConfig = new RaSnConfig();
        if (snRa(metar, raSnConfig)) {
            seriousDOS.add(new SeriousBO(metarSourceDO.getMetarTime(), metarSourceDO.getMetarTime(), metarSourceDO.getAirportCode(), EvaluationWeather.RN_FZRA, "", locationId, ThresholdEnum.SATISFY));
        }
        // 中到大阵雨
        RaConfig raConfig = new RaConfig();
        if (ra(metar, raConfig)) {
            seriousDOS.add(new SeriousBO(metarSourceDO.getMetarTime(), metarSourceDO.getMetarTime(), metarSourceDO.getAirportCode(), EvaluationWeather.RA, "", locationId, ThresholdEnum.SATISFY));
        }
        return seriousDOS;
    }

    /**
     * 处理重要天气
     *
     * @param all
     * @param history
     * @param thismetar
     * @param thisSocreThreshold
     * @param supplier
     * @param IntervalTime
     */
    public static void runHandler(List<SeriousBO> all, List<SeriousBO> history, MetarSourceDO thismetar, List<SeriousBO> thisSocreThreshold, Supplier<EvaluationWeather> supplier, long IntervalTime) {

        // 过滤当前重要天气
        Optional<SeriousBO> thisSeriousBO = thisSocreThreshold.stream().filter(s -> {
            return s.getSeriousWeather().equals(supplier.get()) && s.getStatus().equals(0);
        }).findFirst();
        // 历史重要天气
        Optional<SeriousBO> historySeriousBO = history.stream().filter(s -> {
            return s.getSeriousWeather().equals(supplier.get()) && s.getStatus().equals(0);
        }).findFirst();
        if (thisSeriousBO.isPresent()) {
            if (historySeriousBO.isPresent()) {
                SeriousBO seriousBO = historySeriousBO.get();
                seriousBO.setLastTime(thismetar.getMetarTime());
                seriousBO.setEndTime(thismetar.getMetarTime());
            } else {
                SeriousBO seriousBO = thisSeriousBO.get();
                seriousBO.setLastTime(thismetar.getMetarTime());
                all.add(seriousBO);
            }
        } else {
            if (historySeriousBO.isPresent()) {
                SeriousBO seriousBO = historySeriousBO.get();
                Date lastTime = seriousBO.getLastTime();
                Date thisMetarTime = thismetar.getMetarTime();
                if (thisMetarTime.getTime() - lastTime.getTime() >= IntervalTime) {
                    seriousBO.setEndTime(lastTime);
                    // 重要天气结束
                    seriousBO.setStatus(1);
                }
            }
        }

    }

    /**
     * 判断是否是低云重要天气
     * 低云高或垂直能见度＜60 米且云量在
     * BKN 及以上
     *
     * @param cloud         实况云
     * @param threshold     阈值
     * @param floatingRange 浮动区间百分比
     * @return
     */
    public static boolean lowCloud(Cloud cloud, Integer threshold, Integer floatingRange) {
        // 获得BKN以上的云垂直能见度＜60 米
        ChildCloud bknAndOVClowCloud = cloud.getBKNAndOVClowCloud(true);
        // 判断是否满足阈值
        return lowThreshold(bknAndOVClowCloud.getCloudHeight(), threshold, floatingRange, (live, t1, t2) -> {
            if (bknAndOVClowCloud.getCloudHeight() < t1 + t2) {
                return true;
            }
            return false;
        });
    }

    /**
     * 低能见度＜800 米
     *
     * @param vis
     * @param threshold
     * @param floatingRange
     * @return
     */
    public static boolean lowVis(Integer vis, Integer threshold, Integer floatingRange) {
        return lowThreshold(vis, threshold, floatingRange, (live, t1, t2) -> {
            if (vis < t1 + t2) {
                return true;
            }
            return false;
        });
    }

    /**
     * 判断Rvr是否满足
     *
     * @param rvr
     * @param threshold
     * @param floatingRange
     * @return
     */
    public static boolean lowRvr(Integer rvr, Integer threshold, Integer floatingRange) {
        return lowThreshold(rvr, threshold, floatingRange, (live, t1, t2) -> {
            if (rvr < t1 + t2) {
                return true;
            }
            return false;
        });
    }

    /**
     * 判断雷达回波是否
     *
     * @param radarRange
     * @param threshold
     * @param floatingRange
     * @param radarCoverage
     * @return
     */
    public static boolean radar(List<Double> radarRange, Integer threshold, Integer floatingRange, Integer radarCoverage) {
        List<Double> collect = radarRange.stream().filter(s -> {
            return s > threshold - (floatingRange / 100.0 * threshold);
        }).collect(Collectors.toList());
        if (((collect.size() * 1.0) / radarRange.size() * 100.0) > radarCoverage) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否满足
     *
     * @param live
     * @param threshold
     * @param floatingRange
     * @param isThreshold
     * @return
     */
    private static boolean lowThreshold(Integer live, Integer threshold, Integer floatingRange, BiFunctionP<Integer, Integer, Integer, Boolean> isThreshold) {
        if (isThreshold.apply(live, threshold, 0)) {
            return true;
        }
        if (floatingRange > 0) {
            // 获得区间
            Double floating = floatingRange / 100.0 * threshold;
            if (isThreshold.apply(live, threshold, floating.intValue())) {
                return true;
            }
        }
        return false;

    }


    /**
     * 起评阈值：小雪（各地根据用户服务协议确定小
     * 雪是否评定及起评的标准，上报备案）、中到大雪、冻降水。
     * 米雪、冰粒、雨夹雪等固态降水等同于降雪
     *
     * @param metar
     * @param raSnConfig
     * @return
     */
    private static boolean snRa(Metar metar, RaSnConfig raSnConfig) {
        WeatherDO weatherDO = metar.getWeatherDO();
        String weather = weatherDO.getWeather();
        if (StringUtils.isNotEmpty(weather)) {
            List<String> rasns = raSnConfig.getRasns();
            String[] split = weather.split(" ");
            return contains(rasns, split);
        }
        return false;

    }

    /**
     * 中等以上降水
     *
     * @return
     */
    private static boolean ra(Metar metar, RaConfig raConfig) {
        WeatherDO weatherDO = metar.getWeatherDO();
        String weather = weatherDO.getWeather();
        if (StringUtils.isNotEmpty(weather)) {
            List<String> ra = raConfig.getRa();
            String[] split = weather.split(" ");
            return contains(ra, split);
        }
        return false;
    }

    /**
     * 是否包括
     *
     * @param all
     * @param split
     * @return
     */
    private static boolean contains(List<String> all, String[] split) {
        Optional<String> first = Arrays.stream(split).filter(s -> {
            return all.contains(s);
        }).findFirst();
        return first.isPresent();
    }

}