package com.emdata.messagewarningscore.core.score.handler.hit;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.emdata.messagewarningscore.common.accuracy.entity.AccuracyEntity;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.common.utils.JudgeUtil;
import com.emdata.messagewarningscore.common.common.utils.SpringUtil;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.warning.entity.HitData;
import com.emdata.messagewarningscore.core.score.handler.HitHandlerService;
import com.emdata.messagewarningscore.core.score.handler.extras.ExtrasSocore;
import com.emdata.messagewarningscore.core.score.qualitative.Qualitative;
import com.emdata.messagewarningscore.data.service.IMetarSourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description:
 */
@Service("HitHandler")
public class HitHandler implements HitHandlerService {
    private static Map<WarningTypeEnum, String> objectStringMap = new HashMap<WarningTypeEnum, String>() {{
        put(WarningTypeEnum.AIRPORT_WARNING, "AirportQualitativeImpl");
        put(WarningTypeEnum.MDRS_WARNING, "MdrsQualitativeImpl");
        put(WarningTypeEnum.TERMINAL_WARNING, "TerminalQualitativeImpl");
    }};

    @Resource(name = "ExtrasSocore")
    private ExtrasSocore extrasSocore;
    @Resource
    private IMetarSourceService metarSourceService;

    @Override
    public HitData run(HitData hitData) {
        // 获得一个全新的变量
        HitData newHitData = hitData.clone();
        /**
         * 预测数据
         */
        ForcastData forcastData = hitData.getForcastData();

        List<SeriousDO> seriousDOS = hitData.getSeriousDOS();

        Optional<SeriousDO> liveStartTime = seriousDOS.stream().min(Comparator.comparing(s -> {
            return s.getStartTime();
        }));
        Optional<SeriousDO> liveEndTime = seriousDOS.stream().max(Comparator.comparing(s -> {
            return s.getEndTime();
        }));
        /**
         * 若预报的伴随天气在预报时段外前后 2h 内均未出现，
         则该份预报成功质量得分扣 2 分
         */
        Date forecastStartTime = forcastData.getStartTime();

        Date forecastEndTime = forcastData.getEndTime();
        List<Metar> metarSourceByTime = metarSourceService.getMetarSourceByTime(forcastData.getAirportCode(), JudgeUtil.add(forecastStartTime, Calendar.HOUR_OF_DAY, -2), JudgeUtil.add(forecastEndTime, Calendar.HOUR_OF_DAY, 2));
        /**
         * 获得附加分
         */
        newHitData.setForeScore(extrasSocore.run(forcastData, metarSourceByTime));
        /**
         * 计算提前量数据
         */
        AccuracyEntity accuracyEntity = new AccuracyEntity(forecastStartTime, forecastEndTime, liveStartTime.get().getStartTime(), liveEndTime.get().getEndTime(), forcastData.getSendTime(), forcastData.getNature().getValue());
        newHitData.setAccuracyEntity(accuracyEntity);
        return newHitData;
    }

    @Override
    public List<HitData> run(List<AlertContentResolveDO> alertContentResolveDOS, WarningTypeEnum warningTypeEnum, NatureEnum nature) {
        Qualitative qualitative = (Qualitative) SpringUtil.getBean(objectStringMap.get(warningTypeEnum));
        List<HitData> collect = alertContentResolveDOS.stream().map(s -> {
            return qualitative.runHitOrEmpty(s);
        }).collect(Collectors.toList());
        return collect.stream().map(s -> {
            if (s.getNatureEnum().equals(NatureEnum.HIT) || s.getNatureEnum().equals(NatureEnum.PART_TRUE)) {
                return run(s);
            }
            return s;
        }).collect(Collectors.toList());
    }
}