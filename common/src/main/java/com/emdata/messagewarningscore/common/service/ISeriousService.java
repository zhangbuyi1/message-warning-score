package com.emdata.messagewarningscore.common.service;/**
 * Created by zhangshaohu on 2020/12/31.
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.accuracy.config.ThresholdConfig;
import com.emdata.messagewarningscore.common.dao.entity.CrPictureDO;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/31
 * @description:
 */
public interface ISeriousService extends IService<SeriousDO> {
    /**
     * 消费一条metar报文
     *
     * @param metarSourceDO
     * @param thresholdConfig
     */
    void consumer(MetarSourceDO metarSourceDO, ThresholdConfig thresholdConfig);

    /**
     * 消费一条雷达数据
     *
     * @param crPictureDO
     * @param thresholdConfig
     */
    void consumerRadar(CrPictureDO crPictureDO, ThresholdConfig thresholdConfig);


    List<SeriousDO> selectAlertByTime(Date startTime, Date endTime, Integer locationInfoId, Integer field, Integer num);

    /**
     * 查询到可以评分的数据
     *
     * @param field
     * @param num
     * @return
     */
    List<SeriousDO> selectIsScore(Integer field, Integer num);
}