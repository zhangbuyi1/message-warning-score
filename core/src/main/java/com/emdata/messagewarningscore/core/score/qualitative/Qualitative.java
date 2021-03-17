package com.emdata.messagewarningscore.core.score.qualitative;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.warning.entity.HitData;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description:
 */
public interface Qualitative {
    /**
     * 评定漏报 SeriousDO
     * 没有判断雷暴数据是否
     *
     * @return
     */
    NatureEnum runLeak(SeriousDO seriousDOS);


    /**
     * 判断空报
     *
     * @return
     */
    HitData runHitOrEmpty(ForcastData forcastData);

    /**
     * 判断部分准确
     *
     * @return
     */
    HitData runPartHit(ForcastData forcastData);

    HitData runHitOrEmpty(AlertContentResolveDO alertContentResolveDO);


}