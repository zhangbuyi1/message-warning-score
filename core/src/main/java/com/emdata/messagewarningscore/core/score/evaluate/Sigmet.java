package com.emdata.messagewarningscore.core.score.evaluate;/**
 * Created by zhangshaohu on 2020/12/21.
 */

import java.util.Map;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description:
 */
public interface Sigmet {


    /**
     * 附属得分
     *
     * @return
     */
    Double attachScore();

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
     * 是否满足终端区评分阈值
     *
     * @return
     */
    boolean startingTerminalReportThreshold();

    /**
     * 是否满足机场警报预警和mdrf
     *
     * @return
     */
    boolean startingAlertReportThreshold();


}