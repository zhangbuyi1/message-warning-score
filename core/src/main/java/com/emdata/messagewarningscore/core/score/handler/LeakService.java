package com.emdata.messagewarningscore.core.score.handler;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description:
 */
public interface LeakService {
    /**
     * 漏报数据
     *
     * @param warningTypeEnum 报告类型
     * @param seriousDO       重要天气
     * @return
     */
    List<SeriousDO> run(WarningTypeEnum warningTypeEnum, List<SeriousDO> seriousDO);

    
}