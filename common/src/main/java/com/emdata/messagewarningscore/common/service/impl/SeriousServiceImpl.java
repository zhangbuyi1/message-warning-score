package com.emdata.messagewarningscore.common.service.impl;/**
 * Created by zhangshaohu on 2020/12/31.
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.accuracy.config.LowVisibilityConfig;
import com.emdata.messagewarningscore.common.accuracy.config.RaConfig;
import com.emdata.messagewarningscore.common.accuracy.config.RaSnConfig;
import com.emdata.messagewarningscore.common.accuracy.config.ThresholdConfig;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.common.HandleMetarWeatherMethod;
import com.emdata.messagewarningscore.common.common.utils.JudgeUtil;
import com.emdata.messagewarningscore.common.common.utils.PicUtil;
import com.emdata.messagewarningscore.common.dao.SeriousMapper;
import com.emdata.messagewarningscore.common.dao.entity.*;
import com.emdata.messagewarningscore.common.enums.RangeTypeEnum;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.metar.entity.*;
import com.emdata.messagewarningscore.common.radar.RadarParse;
import com.emdata.messagewarningscore.common.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author: zhangshaohu
 * @date: 2020/12/31
 * @description:
 */
@Service
public class SeriousServiceImpl extends ServiceImpl<SeriousMapper, SeriousDO> implements ISeriousService {
    @Autowired
    private HandleMetarWeatherMethod handleMetarWeatherMethod;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IRadarInfoService iRadarInfoService;

    @Autowired
    private ILocationInfoService iLocationInfoService;

    @Autowired
    private ILocationInfoPointService iLocationInfoPointService;

    @Autowired
    private IAirPointService iAirPointService;

    @Autowired
    private RadarParse radarParse;


    @Override
    public void consumer(MetarSourceDO metarSourceDO, ThresholdConfig thresholdConfig) {
        LambdaQueryWrapper<SeriousDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SeriousDO::getAirportCode, metarSourceDO.getAirportCode());
        // 查询正在进行中的
        lqw.eq(SeriousDO::getStatus, 0);
        List<SeriousDO> historyScoreThresholds = this.list(lqw);
        /**
         * 判断当前需要评分的重要天气
         */
        LocationDO one = locationService.getOne(new LambdaQueryWrapper<LocationDO>().eq(LocationDO::getLocationCode, metarSourceDO.getAirportCode()).eq(LocationDO::getAirportCode, metarSourceDO.getAirportCode()));
        List<SeriousDO> socreThreshold = isSocreThreshold(metarSourceDO, thresholdConfig, one);
        // 判断低云低能见度是否ok
        SeriousDO lowVisLowCloud = threshold(historyScoreThresholds, metarSourceDO, EvaluationWeather.LOW_VIS_LOWCLOUD, socreThreshold);
        // 中到大降水
        SeriousDO rA = threshold(historyScoreThresholds, metarSourceDO, EvaluationWeather.RA, socreThreshold);
        // 降雪 冻降水
        SeriousDO rnFzra = threshold(historyScoreThresholds, metarSourceDO, EvaluationWeather.RN_FZRA, socreThreshold);
        // 插入
        insartSerious(historyScoreThresholds, lowVisLowCloud, rA, rnFzra);
    }

    @Override
    public void consumerRadar(CrPictureDO crPictureDO, ThresholdConfig thresholdConfig) {
        String radarUuid = crPictureDO.getRadarUuid();
        String picPath = crPictureDO.getPicPath();
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(picPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 查出雷达信息
        RadarInfoDO radarInfoDO = iRadarInfoService.getById(radarUuid);
        PicUtil.LatLng startLatLng = new PicUtil.LatLng(radarInfoDO.getStartLongitude(), radarInfoDO.getStartLatitude());
        PicUtil.LatLng endLatLng = new PicUtil.LatLng(radarInfoDO.getEndLongitude(), radarInfoDO.getEndLatitude());
        // 根据雷达uuid查出地点信息
        List<LocationInfoDO> locationInfoDOS = iLocationInfoService.findByRadarUuid(radarUuid);
        if (CollectionUtils.isEmpty(locationInfoDOS)) {
            return;
        }
        // 根据地点查询出点的信息
        for (LocationInfoDO locationInfoDO : locationInfoDOS) {
            String type = locationInfoDO.getRangeType();
            Integer locationDOId = locationInfoDO.getId();
            List<LocationInfoPointDO> locationInfoPointDOS = iLocationInfoPointService.findByLocationId(locationDOId);
            if (CollectionUtils.isEmpty(locationInfoPointDOS)) {
                List<String> pointUuids = locationInfoPointDOS.stream().map(LocationInfoPointDO::getPointUuid).collect(Collectors.toList());
                List<AirPointDO> airPointDOS = iAirPointService.listByIds(pointUuids);
                if (!CollectionUtils.isEmpty(airPointDOS)) {
                    List<PicUtil.LatLng> latLngs = convertAirPointTo(airPointDOS);
                    // 雷达反射率集合
                    List<Double> reflectRates = new ArrayList<>();
                    // 终端区，机场
                    if (type.equals(RangeTypeEnum.SURFACE.getCode())) {
                        reflectRates.addAll(radarParse.parseRange(image, latLngs, startLatLng, endLatLng));
                    }
                    // 关键点
                    else if (type.equals(RangeTypeEnum.POINT.getCode())) {
                        BufferedImage finalImage = image;
                        latLngs.stream().map(s -> {
                            List<Double> doubles = radarParse.parsePoint(finalImage, s, startLatLng, endLatLng, 1.0);
                            reflectRates.addAll(doubles);
                            return s;
                        }).collect(Collectors.toList());
                    }
                    // 航路
                    else if (type.equals(RangeTypeEnum.LINE.getCode())) {
                        List<Double> doubles = radarParse.parseRoute(image, latLngs, startLatLng, endLatLng, 0.0, locationInfoDO.getWidth());
                        reflectRates.addAll(doubles);
                    }

                }
            }

        }

    }

    @Override
    public List<SeriousDO> selectAlertByTime(Date startTime, Date endTime, Integer locationInfoId, Integer field, Integer num) {
        LocationInfoDO one = iLocationInfoService.getOne(new LambdaQueryWrapper<LocationInfoDO>().eq(LocationInfoDO::getId, locationInfoId));
        startTime = JudgeUtil.add(startTime, field, num * -1);
        endTime = JudgeUtil.add(endTime, field, num * 1);
        LambdaQueryWrapper<SeriousDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SeriousDO::getLocationId, one.getLocationId());
        lqw.ge(SeriousDO::getStartTime, startTime);
        lqw.le(SeriousDO::getEndTime, endTime);
        lqw.eq(SeriousDO::getSeriousWeather, endTime);
        List<SeriousDO> list = this.list(lqw);
        return list;
    }


    @Override
    public List<SeriousDO> selectIsScore(Integer field, Integer num) {
        Date date = new Date();
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.setTime(date);
        instance.add(field, num);
        LambdaQueryWrapper<SeriousDO> lqw = new LambdaQueryWrapper<>();
        lqw.le(SeriousDO::getEndTime, instance.getTime());
        List<SeriousDO> list = this.list(lqw);
        return list;
    }

    /**
     * 将AirPointDOs转为LatLng集合
     *
     * @param airPointDOS 点集合
     * @return 经纬度集合
     */
    private List<PicUtil.LatLng> convertAirPointTo(List<AirPointDO> airPointDOS) {
        List<PicUtil.LatLng> latLngs = airPointDOS.stream().map(a -> {
            return new PicUtil.LatLng(a.getLongitude(), a.getLatitude());
        }).collect(Collectors.toList());
        return latLngs;
    }

    /**
     * 插入数据  如果当前存在数据则修改不存在则插入
     *
     * @param seriousDO
     */
    private void insartSerious(List<SeriousDO> historyScoreThresholds, SeriousDO... seriousDO) {
        for (SeriousDO element : seriousDO) {
            if (element != null) {
                Optional<SeriousDO> first = historyScoreThresholds.stream().filter(s -> {
                    return s.getId().equals(element.getId());
                }).findFirst();
                if (first.isPresent()) {
                    // 修改
                    this.updateById(first.get());
                } else {
                    // 插入
                    this.save(element);
                }
            }
        }
    }

    /**
     * @param historyScoreThresholds 历史数据
     * @param evaluationWeather      天气类型
     * @param thisScoreThreshold     当前实况重要天气
     */
    private SeriousDO threshold(List<SeriousDO> historyScoreThresholds, MetarSourceDO metarSourceDO, EvaluationWeather evaluationWeather, List<SeriousDO> thisScoreThreshold) {
        // 历史达到阈值的天气现象
        Optional<SeriousDO> historySerious = historyScoreThresholds.stream().filter(s -> {
            return s.getSeriousWeather().equals(evaluationWeather);
        }).findFirst();
        // 当前满足阈值的实况天气
        Optional<SeriousDO> thisSerious = thisScoreThreshold.stream().filter(s -> {
            return s.getSeriousWeather().equals(evaluationWeather);
        }).findFirst();
        /**
         * 历史存在
         */
        if (historySerious.isPresent()) {
            // 当前不存在 说明当前重要天气结束 将当前时间设置为结束时间
            if (!thisSerious.isPresent()) {
                SeriousDO seriousDO = historySerious.get();
                //重要天气消失一小时 则认为是重要天气的消散
                if (seriousDO.getEndTime().getTime() - metarSourceDO.getMetarTime().getTime() > 1000 * 60 * 60) {
                    seriousDO.setStatus(1);
                    // 拼接天气
                    String weather = handleMetarWeatherMethod.handleWeather(seriousDO.getAirportCode()
                            , seriousDO.getStartTime(), seriousDO.getEndTime(), evaluationWeather);
                    if (StringUtils.isNotBlank(weather)) {
                        seriousDO.setWeather(weather);
                    }
                    return seriousDO;
                }

            } else {
                Date startTime = thisSerious.get().getStartTime();
                SeriousDO seriousDO = historySerious.get();
                // 当前天气持续中
                seriousDO.setEndTime(startTime);
                return seriousDO;
            }
        } else {
            // 历史不存在 当前存在
            if (thisSerious.isPresent()) {
                SeriousDO seriousDO = thisSerious.get();
                // 说明当前重要天气开始 将重要天气加入 插入重要天气
                return seriousDO;
            }
        }
        return null;
    }

    /**
     * 低云高或垂直能见度＜60 米且云量在
     * BKN 及以上或（和）低能见度＜800 米。
     * 当预报能见度＜800 米，而实况能见度≥800 米，但 RVR
     * ＜550 米时，用使用端跑道的 RVR 数值代替能见度数值进行
     * 评定。
     *
     * @return
     */
    private boolean lowCloudLowVis(Metar metar, LowVisibilityConfig lowVisibilityConfig) {
        Cloud cloud = metar.getCloud();
        Vis vis = metar.getVis();
        ChildCloud bknAndOVClowCloud = cloud.getBKNAndOVClowCloud(true);
        if (bknAndOVClowCloud.getCloudHeight() < lowVisibilityConfig.getLowBknHeight()) {
            return true;
        }
        if (vis.getVis() < lowVisibilityConfig.getLowVis()) {
            return true;
        }
        Rvr rvr = metar.getRvr();
        /**
         * 当预报能见度＜800 米，而实况能见度≥800 米，但 RVR
         * ＜550 米时，用使用端跑道的 RVR 数值代替能见度数值进行
         * 评定。
         */
        if (rvr.getMinRvrVis() < lowVisibilityConfig.getLowVis()) {
            return true;
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
    private boolean snRa(Metar metar, RaSnConfig raSnConfig) {
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
    private boolean ra(Metar metar, RaConfig raConfig) {
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
    private boolean contains(List<String> all, String[] split) {
        Optional<String> first = Arrays.stream(split).filter(s -> {
            return all.contains(s);
        }).findFirst();
        return first.isPresent();
    }

    /**
     * 判断当前metar报文产生的重要天气
     *
     * @param metarSourceDO   metar报文
     * @param thresholdConfig 配置文件
     * @return
     */
    private List<SeriousDO> isSocreThreshold(MetarSourceDO metarSourceDO, ThresholdConfig thresholdConfig, LocationDO locationDO) {
        List<SeriousDO> seriousDOS = new ArrayList<>();
        Metar metar = new Metar(metarSourceDO.getMetarSource());

        LowVisibilityConfig lowVisibilityConfig = new LowVisibilityConfig();
        // 低云低能见度
        if (lowCloudLowVis(metar, lowVisibilityConfig)) {
            //(Date startTime, Date endTime, String airportCode, EvaluationWeather seriousWeather, String weather
            // 判断当前天气现象是否添加
            seriousDOS.add(new SeriousDO(metarSourceDO.getMetarTime(), metarSourceDO.getMetarTime(), metarSourceDO.getAirportCode(), EvaluationWeather.LOW_VIS_LOWCLOUD, "", locationDO.getId()));
        }
        // 降雪 冻降水
        RaSnConfig raSnConfig = new RaSnConfig();
        if (snRa(metar, raSnConfig)) {
            seriousDOS.add(new SeriousDO(metarSourceDO.getMetarTime(), metarSourceDO.getMetarTime(), metarSourceDO.getAirportCode(), EvaluationWeather.RN_FZRA, "", locationDO.getId()));
        }
        // 中到大阵雨
        RaConfig raConfig = new RaConfig();
        if (ra(metar, raConfig)) {
            seriousDOS.add(new SeriousDO(metarSourceDO.getMetarTime(), metarSourceDO.getMetarTime(), metarSourceDO.getAirportCode(), EvaluationWeather.RA, "", locationDO.getId()));
        }
        return seriousDOS;
    }
}