package com.emdata.messagewarningscore.core.score.handler;

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.warning.entity.HitData;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description:
 */
public interface BaseDataHandlerService<T, M, R> {
    /**
     * 漏报对象处理器
     *
     * @param seriousDO 实况对象
     */
    T leakDataHandler(SeriousDO seriousDO, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum);


    List<T> leakDataHandler(List<SeriousDO> seriousDO, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum);

    /**
     * 命中数据处理
     *
     * @param hitData
     */
    T hitDataHandler(HitData hitData);

    /**
     * 空报数据处理
     *
     * @param forcastData
     */
    T emptyDataHandler(ForcastData forcastData);

    /**
     * 漏报数据处理调用
     *
     * @param seriousDOS
     * @param warningTypeEnum
     * @param natureEnum
     */
    void runLeakDataProcess(List<SeriousDO> seriousDOS, WarningTypeEnum warningTypeEnum, NatureEnum natureEnum);

    /**
     * 空报数据处理调用
     *
     * @param forcastData
     */
    void runEmptyDataProcess(ForcastData forcastData);

    /**
     * 命中数据处理调用
     *
     * @param hitData
     */
    void runHitDataProcess(HitData hitData);

}