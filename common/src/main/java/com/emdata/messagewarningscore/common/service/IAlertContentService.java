package com.emdata.messagewarningscore.common.service;/**
 * Created by zhangshaohu on 2021/1/6.
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/6
 * @description:
 */
public interface IAlertContentService extends IService<AlertContentResolveDO> {
    /**
     * 根据时间查询当前时间段是否出现了重要天气现象
     *
     * @return
     */
    List<AlertContentResolveDO> selectAlertByTime(Date startTime, Date endTime, WarningTypeEnum warningTypeEnum, Integer locationId, Integer field, Integer num);

    /**
     * 查询可以评分的预测数据
     *
     * @param field 时间单位
     * @param num   时间
     * @return
     */
    List<AlertContentResolveDO> selectIsScoreAlertContent(WarningTypeEnum warningTypeEnum, Integer field, Integer num);

}