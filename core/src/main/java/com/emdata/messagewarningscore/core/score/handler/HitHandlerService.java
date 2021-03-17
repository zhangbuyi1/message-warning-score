package com.emdata.messagewarningscore.core.score.handler;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.warning.entity.HitData;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description: 处理命中数据
 */
public interface HitHandlerService {
    /**
     * 处理命中数据
     *
     * @param hitData
     * @return
     */
    HitData run(HitData hitData);

    List<HitData> run(List<AlertContentResolveDO> alertContentResolveDOS, WarningTypeEnum warningTypeEnum, NatureEnum nature);

}