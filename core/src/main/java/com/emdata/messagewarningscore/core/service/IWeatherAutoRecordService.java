package com.emdata.messagewarningscore.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.WeatherAutoRecordDO;
import com.emdata.messagewarningscore.core.controller.vo.WeatherAutoRecordQuery;
import com.emdata.messagewarningscore.core.service.bo.WeatherAutoRecordBO;
import com.emdata.messagewarningscore.core.service.bo.WeatherAutoRecordTableBO;

import java.util.List;

/**
 * @description: 重要天气自动记录服务类
 * @date: 2020/12/14
 * @author: sunming
 */
public interface IWeatherAutoRecordService extends IService<WeatherAutoRecordDO> {

    /**
     * 重要天气自动记录分页查询
     *
     * @param airportCode            机场代码
     * @param weatherAutoRecordQuery 查询参数
     * @return 查询结果
     */
    List<WeatherAutoRecordBO> weatherAutoRecordPage(String airportCode, WeatherAutoRecordQuery weatherAutoRecordQuery);

}
