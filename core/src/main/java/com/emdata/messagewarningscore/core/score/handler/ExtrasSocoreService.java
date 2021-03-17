package com.emdata.messagewarningscore.core.score.handler;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.metar.Metar;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description: 处理伴随天气附加分
 */
public interface ExtrasSocoreService {
    /**
     * 附加得分
     *
     * @param forcastData 预测数据
     * @return
     */
    Double run(ForcastData forcastData, List<Metar> metarSourceByTime);
}