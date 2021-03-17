package com.emdata.messagewarningscore.core.score.accuracy;/**
 * Created by zhangshaohu on 2020/12/21.
 */

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description:
 */
public interface Accuracy {
    /**
     * 提前量
     *
     * @return
     */
    Double leadTime();

    /**
     * 偏差量
     *
     * @return
     */
    Double deviation();

    /**
     * 重合率
     *
     * @return
     */
    Double coincidenceRate();

    /**
     * 提前量得分
     *
     * @return
     */
    Double getLs();

    /**
     * 提前量的权重系数
     *
     * @return
     */
    Double getWl();

    /**
     * 偏差量得分
     *
     * @return
     */
    Double getDs();

    /**
     * 提前量与偏差量综合质量
     *
     * @return
     */
    Double getLdq();

    /**
     * 预报成功质量得分
     *
     * @return
     */
    Double getForeSuccessQualityScore(Double foreScore);

}