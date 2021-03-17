package com.emdata.messagewarningscore.core.score.accuracy;

import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;

public interface MdrsAccuracy {

    /**
     * mdrs开始偏差量
     *
     * @return
     */
    Double mdrsStartDeviation();

    /**
     * mdrs结束偏差量
     *
     * @return
     */
    Double mdrsEndDeviation();

    /**
     * 命中得分
     *
     * @return
     */
    Double mdrsHitScore();

    /**
     * mdrs主观得分
     */
    Double mdrsSubjectScore();

    /**
     * mdrs客观得分
     */
    Double mdrsObjectiveScore();

    /**
     * mdrs概率权重
     */
    Double mdrsProWeight();

    /**
     * mdrs综合得分
     */
    Double mdrsAllScore();
}
