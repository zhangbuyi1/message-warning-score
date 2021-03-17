package com.emdata.messagewarningscore.core.score.handler.data.airport.impl;

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.accuracy.enums.WeatherType;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.*;
import com.emdata.messagewarningscore.common.enums.AffectedAreaEnum;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.service.IAlertContentService;
import com.emdata.messagewarningscore.common.service.ILocationService;
import com.emdata.messagewarningscore.common.warning.entity.HitData;
import com.emdata.messagewarningscore.core.score.accuracy.tmp.AccuracyTmp;
import com.emdata.messagewarningscore.core.score.handler.SaveHandlerService;
import com.emdata.messagewarningscore.core.score.handler.data.CommonMethed;
import com.emdata.messagewarningscore.core.score.handler.data.airport.AirportDataHandlerService;
import com.emdata.messagewarningscore.core.service.bo.WarningAutoEvalBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description: 数据处理器
 */
@Service("AirportDataHandlerServiceImpl")
public class AirportDataHandlerServiceImpl implements AirportDataHandlerService {

    @Autowired
    private ILocationService iLocationService;

    @Autowired
    private SaveHandlerService saveHandlerService;

    @Autowired
    private IAlertContentService iAlertContentService;

    /**
     * 漏报处理数据调用
     *
     * @param seriousDOS
     * @param warningTypeEnum
     * @param natureEnum
     */
    @Override
    public void runLeakDataProcess(List<SeriousDO> seriousDOS, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum) {
        List<WarningAutoEvalBO> warningAutoEvalBOS = leakDataHandler(seriousDOS, warningTypeEnum, natureEnum);
        if (CollectionUtils.isEmpty(warningAutoEvalBOS)) {
            return;
        }
        warningAutoEvalBOS.forEach(this::splitBoIntoTable);
        // 当前代码有问题 注释掉了 重要天气没有alert_content_resolve_id
//        seriousDOS.forEach(a -> {
//            updateAlertContentStatus(a.getAlertContentResolveId());
//        });
    }

    /**
     * 空报数据处理调用
     *
     * @param forcastData
     */
    @Override
    public void runEmptyDataProcess(ForcastData forcastData) {
        WarningAutoEvalBO warningAutoEvalBO = emptyDataHandler(forcastData);
        splitBoIntoTable(warningAutoEvalBO);
        updateAlertContentStatus(forcastData.getAlertContentResolveId());
    }

    @Override
    public void runHitDataProcess(HitData hitData) {
        WarningAutoEvalBO warningAutoEvalBO = hitDataHandler(hitData);
        splitBoIntoTable(warningAutoEvalBO);
        updateAlertContentStatus(hitData.getAlertContentResolveId());
    }

    /**
     * 更新已评分
     *
     * @param id
     */
    public void updateAlertContentStatus(Integer id) {
        AlertContentResolveDO resolveDO = iAlertContentService.getById(id);
        resolveDO.setStatus(1);
        iAlertContentService.updateById(resolveDO);
    }

    /**
     * 拆分bo并分别入库
     *
     * @param warningAutoEvalBO
     */
    public void splitBoIntoTable(WarningAutoEvalBO warningAutoEvalBO) {
        WarningAutoEvalDO warningAutoEvalDO = obtainAutoEvalDO(warningAutoEvalBO);
        WarningAutoEvalInfosDO warningAutoEvalInfosDO = obtainAutoEvalInfosDO(warningAutoEvalBO);
        warningAutoEvalInfosDO.setEvalId(warningAutoEvalDO.getId());
        saveHandlerService.insart(WarningAutoEvalDO.class, warningAutoEvalDO);
        saveHandlerService.insart(WarningAutoEvalInfosDO.class, warningAutoEvalInfosDO);
    }

    /**
     * 漏报 只有实况，没有预测
     *
     * @param seriousDO
     * @param warningTypeEnum
     * @return
     */
    @Override
    public WarningAutoEvalBO leakDataHandler(SeriousDO seriousDO, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum) {
        WarningAutoEvalBO warningAutoEvalBO = new WarningAutoEvalBO();
        warningAutoEvalBO.setAirportCode(seriousDO.getAirportCode());
        warningAutoEvalBO.setWarningType(warningTypeEnum.getMessage());
        LocationDO locationDO = iLocationService.getById(seriousDO.getLocationId());
        String locationName = Optional.ofNullable(locationDO).map(LocationDO::getLocationName).orElse("");
        warningAutoEvalBO.setAffectedArea(AffectedAreaEnum.getValue(warningTypeEnum.getWarningType()));
        warningAutoEvalBO.setWarningNature("");
        warningAutoEvalBO.setReleaseTime(null);
        warningAutoEvalBO.setScoreWeather(seriousDO.getSeriousWeather().getShortCode());
        warningAutoEvalBO.setAffectedRange(locationName);
        warningAutoEvalBO.setWithWeather(seriousDO.getWeather());
        warningAutoEvalBO.setWeather("");
        warningAutoEvalBO.setMdrsStart("橙色");
        warningAutoEvalBO.setEvaluate(natureEnum.getMessage());
        warningAutoEvalBO.setMetarDate(Java8DateUtils.format(seriousDO.getStartTime(), Java8DateUtils.DATE));
        warningAutoEvalBO.setMetarPlaceDate(warningAutoEvalBO.getAffectedArea() + warningAutoEvalBO.getMetarDate());
        long d = seriousDO.getEndTime().getTime() - seriousDO.getStartTime().getTime() / 60 * 60 * 1000;
        warningAutoEvalBO.setMetarDuration((int) d);
        warningAutoEvalBO.setLead(0.0);
        warningAutoEvalBO.setDeviation(0.0);
        warningAutoEvalBO.setCoinRate(0.0);
        warningAutoEvalBO.setMetarStartTime(seriousDO.getStartTime());
        warningAutoEvalBO.setMetarEndTime(seriousDO.getEndTime());
        warningAutoEvalBO.setPreStartTime(null);
        warningAutoEvalBO.setPreEndTime(null);
        // 如果是低云低能见度
        CommonMethed.setMetarVisCbaseRvr(seriousDO, warningAutoEvalBO);
        return warningAutoEvalBO;
    }

    @Override
    public List<WarningAutoEvalBO> leakDataHandler(List<SeriousDO> seriousDO, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum) {
        return seriousDO.stream().map(s -> leakDataHandler(s, warningTypeEnum, natureEnum)).collect(Collectors.toList());

    }


    public WarningAutoEvalDO obtainAutoEvalDO(WarningAutoEvalBO warningAutoEvalBO) {
        WarningAutoEvalDO warningAutoEvalDO = new WarningAutoEvalDO();
        BeanUtils.copyProperties(warningAutoEvalBO, warningAutoEvalDO);
        return warningAutoEvalDO;
    }

    public WarningAutoEvalInfosDO obtainAutoEvalInfosDO(WarningAutoEvalBO warningAutoEvalBO) {
        WarningAutoEvalInfosDO warningAutoEvalInfosDO = new WarningAutoEvalInfosDO();
        BeanUtils.copyProperties(warningAutoEvalBO, warningAutoEvalInfosDO);
        return warningAutoEvalInfosDO;
    }

    /**
     * 命中数据处理
     *
     * @param hitData
     */
    @Override
    public WarningAutoEvalBO hitDataHandler(HitData hitData) {
        ForcastData forcastData = hitData.getForcastData();
        List<SeriousDO> seriousDOS = hitData.getSeriousDOS();
        AccuracyTmp accuracyTmp = new AccuracyTmp(forcastData.getStartTime(), forcastData.getEndTime(), seriousDOS.get(0).getStartTime()
                , seriousDOS.get(0).getEndTime(), forcastData.getSendTime()
                , forcastData.getNature().getValue());
        WarningAutoEvalBO warningAutoEvalBO = new WarningAutoEvalBO();
        warningAutoEvalBO.setAirportCode(hitData.getForcastData().getAirportCode());
        warningAutoEvalBO.setWarningType(forcastData.getWarningTypeEnum().getMessage());
        warningAutoEvalBO.setAffectedArea(forcastData.getRegion());
        warningAutoEvalBO.setWarningNature(forcastData.getNature().getMessage());
        warningAutoEvalBO.setReleaseTime(forcastData.getSendTime());
        warningAutoEvalBO.setScoreWeather(forcastData.getEvaluationWeather().getShortCode());
        warningAutoEvalBO.setAffectedRange(forcastData.getRange());
        // 主要天气
        List<ForcastWeather> mainWeather = forcastData.getMainWeather();
        StringBuilder builder = new StringBuilder();
        mainWeather.forEach(a -> {
            String describe = a.getDescribe();
            builder.append(describe).append("，");
        });
        String weather = builder.toString();
        warningAutoEvalBO.setWeather(weather.substring(0, weather.length() == 0 ? 0 : weather.length() - 1));
        // 伴随天气
        List<ForcastWeather> accompanyWeathers = forcastData.getAccompanyWeathers();
        StringBuilder sb = new StringBuilder();
        accompanyWeathers.forEach(a -> {
            sb.append(a.getDescribe()).append("，");
        });
        String withWeather = sb.toString();
        warningAutoEvalBO.setWithWeather(withWeather.substring(0, withWeather.length() == 0 ? 0 : withWeather.length() - 1));
        warningAutoEvalBO.setMdrsStart("橙色");
        warningAutoEvalBO.setEvaluate(hitData.getNatureEnum().getMessage());
        warningAutoEvalBO.setMetarDate(Java8DateUtils.format(seriousDOS.get(0).getStartTime(), Java8DateUtils.DATE));
        warningAutoEvalBO.setMetarPlaceDate(warningAutoEvalBO.getAffectedArea() + warningAutoEvalBO.getMetarDate());
        long d = seriousDOS.get(0).getEndTime().getTime() - seriousDOS.get(0).getStartTime().getTime() / 60 * 60 * 1000;
        warningAutoEvalBO.setMetarDuration((int) d);
        warningAutoEvalBO.setLead(accuracyTmp.leadTime());
        warningAutoEvalBO.setDeviation(accuracyTmp.deviation());
        warningAutoEvalBO.setCoinRate(accuracyTmp.coincidenceRate());
        warningAutoEvalBO.setMetarStartTime(seriousDOS.get(0).getStartTime());
        warningAutoEvalBO.setMetarEndTime(seriousDOS.get(0).getEndTime());
        warningAutoEvalBO.setPreStartTime(forcastData.getStartTime());
        warningAutoEvalBO.setPreEndTime(forcastData.getEndTime());
        // 如果实况是低云低能见度
        CommonMethed.setMetarVisCbaseRvr(seriousDOS.get(0), warningAutoEvalBO);
        // 如果预报重要天气为低云低能见度
        warningAutoEvalBO.setPreVis(CommonMethed.getValue(mainWeather, WeatherType.VIS, WeatherType::getCode));
        warningAutoEvalBO.setPreCbase(CommonMethed.getValue(mainWeather, WeatherType.CLOUD, WeatherType::getCode));
        warningAutoEvalBO.setPreRvr(CommonMethed.getValue(mainWeather, WeatherType.RVR, WeatherType::getCode));
        return warningAutoEvalBO;
    }


    /**
     * 空报数据处理
     *
     * @param forcastData
     * @return
     */
    @Override
    public WarningAutoEvalBO emptyDataHandler(ForcastData forcastData) {
        WarningAutoEvalBO warningAutoEvalBO = new WarningAutoEvalBO();
        warningAutoEvalBO.setAirportCode(forcastData.getAirportCode());
        warningAutoEvalBO.setWarningType(forcastData.getWarningTypeEnum().getMessage());
        warningAutoEvalBO.setAffectedArea(forcastData.getRegion());
        warningAutoEvalBO.setWarningNature(forcastData.getNature().getMessage());
        warningAutoEvalBO.setReleaseTime(forcastData.getSendTime());
        warningAutoEvalBO.setScoreWeather(forcastData.getEvaluationWeather().getShortCode());
        warningAutoEvalBO.setAffectedRange(forcastData.getRange());
        // 主要天气
        List<ForcastWeather> mainWeather = forcastData.getMainWeather();
        StringBuilder builder = new StringBuilder();
        mainWeather.forEach(a -> {
            String describe = a.getDescribe();
            builder.append(describe).append("，");
        });
        String weather = builder.toString();
        warningAutoEvalBO.setWeather(weather.substring(0, weather.length() == 0 ? 0 : weather.length() - 1));
        // 伴随天气
        List<ForcastWeather> accompanyWeathers = forcastData.getAccompanyWeathers();
        StringBuilder sb = new StringBuilder();
        accompanyWeathers.forEach(a -> {
            sb.append(a.getDescribe()).append("，");
        });
        String withWeather = sb.toString();
        warningAutoEvalBO.setWithWeather(withWeather.substring(0, withWeather.length() == 0 ? 0 : withWeather.length() - 1));
        warningAutoEvalBO.setMdrsStart("橙色");
        warningAutoEvalBO.setEvaluate(NatureEnum.EMPTY.getMessage());
        warningAutoEvalBO.setMetarPlaceDate("");
        warningAutoEvalBO.setMetarDate("");
        warningAutoEvalBO.setMetarDuration(0);
        warningAutoEvalBO.setLead(0.0);
        warningAutoEvalBO.setDeviation(0.0);
        warningAutoEvalBO.setCoinRate(0.0);
        warningAutoEvalBO.setMetarStartTime(null);
        warningAutoEvalBO.setMetarEndTime(null);
        warningAutoEvalBO.setPreStartTime(forcastData.getStartTime());
        warningAutoEvalBO.setPreEndTime(forcastData.getEndTime());
        // 如果预报重要天气为低云低能见度
        warningAutoEvalBO.setPreVis(CommonMethed.getValue(mainWeather, WeatherType.VIS, WeatherType::getCode));
        warningAutoEvalBO.setPreCbase(CommonMethed.getValue(mainWeather, WeatherType.CLOUD, WeatherType::getCode));
        warningAutoEvalBO.setPreRvr(CommonMethed.getValue(mainWeather, WeatherType.RVR, WeatherType::getCode));
        return warningAutoEvalBO;
    }
}