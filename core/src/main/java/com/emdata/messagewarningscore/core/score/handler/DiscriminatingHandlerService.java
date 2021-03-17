package com.emdata.messagewarningscore.core.score.handler;/**
 * Created by zhangshaohu on 2021/1/13.
 */

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.warning.entity.HitData;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/13
 * @description:
 */
public interface DiscriminatingHandlerService {
    /**
     * 漏报数据
     *
     * @param warningTypeEnum 报告类型
     * @param seriousDO       重要天气
     * @return
     */
    List<SeriousDO> runLeak(WarningTypeEnum warningTypeEnum, List<SeriousDO> seriousDO, NatureEnum nature);

    void runSaveLeakScore(WarningTypeEnum warningTypeEnum, List<SeriousDO> seriousDO, NatureEnum nature);

    /**
     * 空报处理器 根据不同WarningTypeEnum 决定使用什么处理器
     *
     * @param forcastData     预测数据
     * @param warningTypeEnum 处理器类型
     */
    void runSaveEmptyScore(ForcastData forcastData, WarningTypeEnum warningTypeEnum);

    /**
     * 命中处理器 根据不同 WarningTypeEnum
     *
     * @param hitDatas        命中数据
     * @param warningTypeEnum 类型
     */
    void runSaveHitScore(List<HitData> hitDatas, WarningTypeEnum warningTypeEnum);

    /**
     * 漏报处理器
     *
     * @param seriousDOS      实况数据
     * @param warningTypeEnum 预警类型
     * @param natureEnum      命中或者不评分
     */
    void runSaveLeakScore(List<SeriousDO> seriousDOS, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum);

    /**
     * 命中处理器
     *
     * @param alertContentResolveDOS
     * @param warningTypeEnum
     * @param nature
     */
    void runSaveHitOrEmptyScore(List<AlertContentResolveDO> alertContentResolveDOS, WarningTypeEnum warningTypeEnum, NatureEnum nature);
}