package com.emdata.messagewarningscore.core.score.handler.data.mdrs;

import com.emdata.messagewarningscore.common.accuracy.entity.AccuracyEntity;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.accuracy.enums.WeatherType;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.MdrsAutoEvalDO;
import com.emdata.messagewarningscore.common.dao.entity.MdrsAutoEvalInfosDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.service.IAlertContentService;
import com.emdata.messagewarningscore.common.warning.entity.HitData;
import com.emdata.messagewarningscore.core.score.accuracy.tmp.MdrsAccuracyTmp;
import com.emdata.messagewarningscore.core.score.handler.SaveHandlerService;
import com.emdata.messagewarningscore.core.score.handler.data.CommonMethed;
import com.emdata.messagewarningscore.core.service.bo.MdrsAutoEvalAllBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: mdrs数据处理
 * @date: 2021/1/13
 * @author: sunming
 */
@Service("MdrsDataHandlerServiceImpl")
public class MdrsDataHandlerServiceImpl implements MdrsDataHandlerService {

    @Autowired
    private SaveHandlerService saveHandlerService;
    @Autowired
    private IAlertContentService iAlertContentService;

    /**
     * 漏报对象处理器
     *
     * @param seriousDO 实况对象
     */
    @Override
    public MdrsAutoEvalAllBO leakDataHandler(SeriousDO seriousDO, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum) {
        MdrsAutoEvalAllBO mdrsAutoEvalAllBO = new MdrsAutoEvalAllBO();
        mdrsAutoEvalAllBO.setAirportCode(seriousDO.getAirportCode());
        mdrsAutoEvalAllBO.setReleaseTime(null);
        mdrsAutoEvalAllBO.setImportantWeather(seriousDO.getSeriousWeather().getShortCode());
        mdrsAutoEvalAllBO.setWeather(seriousDO.getWeather());
        mdrsAutoEvalAllBO.setWithWeather("");
        mdrsAutoEvalAllBO.setPredictProbility("");
        mdrsAutoEvalAllBO.setHit(natureEnum.getMessage());
        mdrsAutoEvalAllBO.setStartDeviation(0.0);
        mdrsAutoEvalAllBO.setEndDeviation(0.0);
        mdrsAutoEvalAllBO.setHitScore(0.0);
        mdrsAutoEvalAllBO.setProWeight(0.0);
        mdrsAutoEvalAllBO.setSubjectScore(0.0);
        mdrsAutoEvalAllBO.setObjectiveScore(0.0);
        mdrsAutoEvalAllBO.setAllScore(0.0);
        mdrsAutoEvalAllBO.setMetarStartTime(seriousDO.getStartTime());
        mdrsAutoEvalAllBO.setMetarEndTime(seriousDO.getEndTime());
        mdrsAutoEvalAllBO.setPreStartTime(null);
        mdrsAutoEvalAllBO.setPreEndTime(null);
        // 如果是低云低能见度
        CommonMethed.setMetarVisCbaseRvr(seriousDO, mdrsAutoEvalAllBO);
        return mdrsAutoEvalAllBO;
    }

    @Override
    public List<MdrsAutoEvalAllBO> leakDataHandler(List<SeriousDO> seriousDO, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum) {
        List<MdrsAutoEvalAllBO> mdrsAutoEvalAllBOS = seriousDO.stream()
                .map(a -> leakDataHandler(a, warningTypeEnum, natureEnum)).collect(Collectors.toList());
        return mdrsAutoEvalAllBOS;
    }

    /**
     * 命中数据处理
     *
     * @param hitData
     */
    @Override
    public MdrsAutoEvalAllBO hitDataHandler(HitData hitData) {
        ForcastData forcastData = hitData.getForcastData();
        List<SeriousDO> seriousDOS = hitData.getSeriousDOS();
        AccuracyEntity accuracyEntity = hitData.getAccuracyEntity();
        MdrsAccuracyTmp mdrsAccuracyTmp = new MdrsAccuracyTmp();
        mdrsAccuracyTmp.setAccuracyEntity(accuracyEntity);
        MdrsAutoEvalAllBO mdrsAutoEvalAllBO = new MdrsAutoEvalAllBO();
        mdrsAutoEvalAllBO.setAirportCode(forcastData.getAirportCode());
        mdrsAutoEvalAllBO.setReleaseTime(forcastData.getSendTime());
        mdrsAutoEvalAllBO.setImportantWeather(forcastData.getEvaluationWeather().getShortCode());
        // 主要天气
        List<ForcastWeather> mainWeather = forcastData.getMainWeather();
        StringBuilder builder = new StringBuilder();
        mainWeather.forEach(a -> {
            String describe = a.getDescribe();
            builder.append(describe).append("，");
        });
        String weather = builder.toString();
        mdrsAutoEvalAllBO.setWeather(weather.substring(0, weather.length() == 0 ? 0 : weather.length() - 1));
        // 伴随天气
        List<ForcastWeather> accompanyWeathers = forcastData.getAccompanyWeathers();
        StringBuilder sb = new StringBuilder();
        accompanyWeathers.forEach(a -> {
            sb.append(a.getDescribe()).append("，");
        });
        String withWeather = sb.toString();
        mdrsAutoEvalAllBO.setWithWeather(withWeather.substring(0, withWeather.length() == 0 ? 0 : withWeather.length() - 1));
        mdrsAutoEvalAllBO.setPredictProbility("");
        mdrsAutoEvalAllBO.setHit(NatureEnum.HIT.getMessage());
        mdrsAutoEvalAllBO.setStartDeviation(mdrsAccuracyTmp.mdrsStartDeviation());
        mdrsAutoEvalAllBO.setEndDeviation(mdrsAccuracyTmp.mdrsEndDeviation());
        mdrsAutoEvalAllBO.setHitScore(mdrsAccuracyTmp.mdrsHitScore());
        mdrsAutoEvalAllBO.setProWeight(mdrsAccuracyTmp.mdrsProWeight());
        mdrsAutoEvalAllBO.setSubjectScore(mdrsAccuracyTmp.mdrsSubjectScore());
        mdrsAutoEvalAllBO.setObjectiveScore(mdrsAccuracyTmp.mdrsObjectiveScore());
        mdrsAutoEvalAllBO.setAllScore(mdrsAccuracyTmp.mdrsAllScore());
        mdrsAutoEvalAllBO.setPreStartTime(forcastData.getStartTime());
        mdrsAutoEvalAllBO.setPreEndTime(forcastData.getEndTime());
        mdrsAutoEvalAllBO.setMetarStartTime(seriousDOS.get(0).getStartTime());
        mdrsAutoEvalAllBO.setMetarEndTime(seriousDOS.get(0).getEndTime());
        // 如果实况是低云低能见度
        CommonMethed.setMetarVisCbaseRvr(seriousDOS.get(0), mdrsAutoEvalAllBO);
        // 如果预报重要天气为低云低能见度
        mdrsAutoEvalAllBO.setPreVis(CommonMethed.getValue(mainWeather, WeatherType.VIS, WeatherType::getCode));
        mdrsAutoEvalAllBO.setPreCbase(CommonMethed.getValue(mainWeather, WeatherType.CLOUD, WeatherType::getCode));
        mdrsAutoEvalAllBO.setPreRvr(CommonMethed.getValue(mainWeather, WeatherType.RVR, WeatherType::getCode));
        return mdrsAutoEvalAllBO;
    }


    /**
     * 空报数据处理
     *
     * @param forcastData
     */
    @Override
    public MdrsAutoEvalAllBO emptyDataHandler(ForcastData forcastData) {
        MdrsAutoEvalAllBO mdrsAutoEvalAllBO = new MdrsAutoEvalAllBO();
        mdrsAutoEvalAllBO.setAirportCode(forcastData.getAirportCode());
        mdrsAutoEvalAllBO.setReleaseTime(forcastData.getSendTime());
        mdrsAutoEvalAllBO.setImportantWeather(forcastData.getEvaluationWeather().getShortCode());
        // 主要天气
        List<ForcastWeather> mainWeather = forcastData.getMainWeather();
        StringBuilder builder = new StringBuilder();
        mainWeather.forEach(a -> {
            String describe = a.getDescribe();
            builder.append(describe).append("，");
        });
        String weather = builder.toString();
        mdrsAutoEvalAllBO.setWeather(weather.substring(0, weather.length() == 0 ? 0 : weather.length() - 1));
        // 伴随天气
        List<ForcastWeather> accompanyWeathers = forcastData.getAccompanyWeathers();
        StringBuilder sb = new StringBuilder();
        accompanyWeathers.forEach(a -> {
            sb.append(a.getDescribe()).append("，");
        });
        String withWeather = sb.toString();
        mdrsAutoEvalAllBO.setWithWeather(withWeather.substring(0, withWeather.length() == 0 ? 0 : withWeather.length() - 1));
        mdrsAutoEvalAllBO.setPredictProbility("");
        mdrsAutoEvalAllBO.setHit(NatureEnum.EMPTY.getMessage());
        mdrsAutoEvalAllBO.setStartDeviation(0.0);
        mdrsAutoEvalAllBO.setEndDeviation(0.0);
        mdrsAutoEvalAllBO.setHitScore(0.0);
        mdrsAutoEvalAllBO.setProWeight(0.0);
        mdrsAutoEvalAllBO.setSubjectScore(0.0);
        mdrsAutoEvalAllBO.setObjectiveScore(0.0);
        mdrsAutoEvalAllBO.setAllScore(0.0);
        mdrsAutoEvalAllBO.setMetarStartTime(null);
        mdrsAutoEvalAllBO.setMetarEndTime(null);
        mdrsAutoEvalAllBO.setPreStartTime(forcastData.getStartTime());
        mdrsAutoEvalAllBO.setPreEndTime(forcastData.getEndTime());
        mdrsAutoEvalAllBO.setPreVis(CommonMethed.getValue(mainWeather, WeatherType.VIS, WeatherType::getCode));
        mdrsAutoEvalAllBO.setPreCbase(CommonMethed.getValue(mainWeather, WeatherType.CLOUD, WeatherType::getCode));
        mdrsAutoEvalAllBO.setPreRvr(CommonMethed.getValue(mainWeather, WeatherType.RVR, WeatherType::getCode));
        return mdrsAutoEvalAllBO;
    }

    @Override
    public void runLeakDataProcess(List<SeriousDO> seriousDOS, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum) {
        List<MdrsAutoEvalAllBO> mdrsAutoEvalAllBOS = leakDataHandler(seriousDOS, warningTypeEnum, natureEnum);
        mdrsAutoEvalAllBOS.forEach(this::splitBoIntoTable);
        // 当前代码有问题  注释掉了 重要天气没有对应的alert_content_resolve_id
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
        MdrsAutoEvalAllBO mdrsAutoEvalAllBO = emptyDataHandler(forcastData);
        splitBoIntoTable(mdrsAutoEvalAllBO);
        updateAlertContentStatus(forcastData.getAlertContentResolveId());
    }

    /**
     * 命中数据处理调用
     *
     * @param hitData
     */
    @Override
    public void runHitDataProcess(HitData hitData) {
        MdrsAutoEvalAllBO mdrsAutoEvalAllBO = hitDataHandler(hitData);
        splitBoIntoTable(mdrsAutoEvalAllBO);
        updateAlertContentStatus(hitData.getAlertContentResolveId());
    }


    private MdrsAutoEvalDO obtainAutoEvalDO(MdrsAutoEvalAllBO mdrsAutoEvalAllBO) {
        MdrsAutoEvalDO mdrsAutoEvalDO = new MdrsAutoEvalDO();
        BeanUtils.copyProperties(mdrsAutoEvalAllBO, mdrsAutoEvalDO);
        return mdrsAutoEvalDO;
    }

    private MdrsAutoEvalInfosDO obtainAutoEvalInfosDO(MdrsAutoEvalAllBO mdrsAutoEvalAllBO) {
        MdrsAutoEvalInfosDO mdrsAutoEvalInfosDO = new MdrsAutoEvalInfosDO();
        BeanUtils.copyProperties(mdrsAutoEvalAllBO, mdrsAutoEvalInfosDO);
        return mdrsAutoEvalInfosDO;
    }

    /**
     * 拆分bo并分别入库
     *
     * @param mdrsAutoEvalAllBO
     */
    private void splitBoIntoTable(MdrsAutoEvalAllBO mdrsAutoEvalAllBO) {
        MdrsAutoEvalDO mdrsAutoEvalDO = obtainAutoEvalDO(mdrsAutoEvalAllBO);
        MdrsAutoEvalInfosDO mdrsAutoEvalInfosDO = obtainAutoEvalInfosDO(mdrsAutoEvalAllBO);
        mdrsAutoEvalInfosDO.setEvalId(mdrsAutoEvalDO.getId());
        saveHandlerService.insart(MdrsAutoEvalDO.class, mdrsAutoEvalDO);
        saveHandlerService.insart(MdrsAutoEvalInfosDO.class, mdrsAutoEvalInfosDO);
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
}
