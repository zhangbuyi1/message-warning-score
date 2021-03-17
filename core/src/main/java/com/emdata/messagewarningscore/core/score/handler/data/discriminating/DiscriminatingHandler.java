package com.emdata.messagewarningscore.core.score.handler.data.discriminating;/**
 * Created by zhangshaohu on 2021/1/13.
 */

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.common.utils.SpringUtil;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.warning.entity.HitData;
import com.emdata.messagewarningscore.core.score.handler.BaseDataHandlerService;
import com.emdata.messagewarningscore.core.score.handler.DiscriminatingHandlerService;
import com.emdata.messagewarningscore.core.score.handler.HitHandlerService;
import com.emdata.messagewarningscore.core.score.handler.hit.HitHandler;
import com.emdata.messagewarningscore.core.score.qualitative.Qualitative;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/13
 * @description:
 */
@Service("DiscriminatingHandler")
public class DiscriminatingHandler extends HitHandler implements DiscriminatingHandlerService, HitHandlerService {
    /**
     * 处理漏报数据
     */
    private static Map<WarningTypeEnum, String> objectStringMap = new HashMap<WarningTypeEnum, String>() {{
        put(WarningTypeEnum.AIRPORT_WARNING, "AirportQualitativeImpl");
        put(WarningTypeEnum.MDRS_WARNING, "MdrsQualitativeImpl");
        put(WarningTypeEnum.TERMINAL_WARNING, "TerminalQualitativeImpl");
    }};
    /**
     * 空报漏报命中 入库处理
     */
    private static Map<WarningTypeEnum, String> dataSourceHandlerMap = new HashMap<WarningTypeEnum, String>() {{
        put(WarningTypeEnum.AIRPORT_WARNING, "AirportDataHandlerServiceImpl");
        put(WarningTypeEnum.MDRS_WARNING, "MdrsDataHandlerServiceImpl");
        put(WarningTypeEnum.TERMINAL_WARNING, "AirportDataHandlerServiceImpl");
    }};

    @Override
    public List<SeriousDO> runLeak(WarningTypeEnum warningTypeEnum, List<SeriousDO> seriousDOS, NatureEnum nature) {
        Qualitative qualitative = (Qualitative) SpringUtil.getBean(objectStringMap.get(warningTypeEnum));
        List<SeriousDO> collect = seriousDOS.stream().filter(s -> {
            NatureEnum natureEnum = qualitative.runLeak(s);
            return natureEnum.equals(nature);
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void runSaveLeakScore(WarningTypeEnum warningTypeEnum, List<SeriousDO> seriousDO, NatureEnum nature) {
        List<SeriousDO> seriousDOS = runLeak(warningTypeEnum, seriousDO, nature);
        runSaveLeakScore(seriousDOS, warningTypeEnum, nature);
    }

    @Override
    public void runSaveEmptyScore(ForcastData forcastData, WarningTypeEnum warningTypeEnum) {
        BaseDataHandlerService baseDataHandlerService = (BaseDataHandlerService) SpringUtil.getBean(dataSourceHandlerMap.get(warningTypeEnum));
        baseDataHandlerService.runEmptyDataProcess(forcastData);
    }

    @Override
    public void runSaveHitScore(List<HitData> hitDatas, WarningTypeEnum warningTypeEnum) {
        BaseDataHandlerService baseDataHandlerService = (BaseDataHandlerService) SpringUtil.getBean(dataSourceHandlerMap.get(warningTypeEnum));
        hitDatas.stream().peek(s -> {
            baseDataHandlerService.runHitDataProcess(s);
        }).collect(Collectors.toList());
    }

    @Override
    public void runSaveLeakScore(List<SeriousDO> seriousDOS, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum) {
        BaseDataHandlerService baseDataHandlerService = (BaseDataHandlerService) SpringUtil.getBean(dataSourceHandlerMap.get(warningTypeEnum));
        baseDataHandlerService.runLeakDataProcess(seriousDOS, warningTypeEnum, natureEnum);
    }

    @Override
    public void runSaveHitOrEmptyScore(List<AlertContentResolveDO> alertContentResolveDOS, WarningTypeEnum warningTypeEnum, NatureEnum nature) {
        List<HitData> run = run(alertContentResolveDOS, warningTypeEnum, nature);
        if (nature.equals(NatureEnum.EMPTY)) {
            run.stream().peek(s -> {
                runSaveEmptyScore(s.getForcastData(), warningTypeEnum);
            }).collect(Collectors.toList());
        }
        if (nature.equals(NatureEnum.HIT) || nature.equals(NatureEnum.PART_TRUE)) {
            runSaveHitScore(run, warningTypeEnum);
        }
    }

}