package com.emdata.messagewarningscore.core.score.handler;/**
 * Created by zhangshaohu on 2021/1/5.
 */

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/5
 * @description: 后处理预警数据  需要判断当前主要天气为雷暴 还是降雨
 */
@Service
public interface AlertcontentMainWeatherHandler {

    /**
     * @param alertContentResolveDOS
     * @return 处理数据 并且确定当前为降雨还是雷暴
     */
    List<ForcastData> resetAlertContent(List<AlertContentResolveDO> alertContentResolveDOS);

    /**
     * 处理数据
     *
     * @param alertContentResolveDO
     * @return
     */
    ForcastData resetAlertContent(AlertContentResolveDO alertContentResolveDO);


}