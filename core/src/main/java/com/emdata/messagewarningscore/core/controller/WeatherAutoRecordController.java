package com.emdata.messagewarningscore.core.controller;

import com.emdata.messagewarningscore.common.common.utils.ThreadLocalUtils;
import com.emdata.messagewarningscore.common.dao.entity.WeatherAutoRecordDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.core.controller.vo.WeatherAutoRecordQuery;
import com.emdata.messagewarningscore.core.service.IWeatherAutoRecordService;
import com.emdata.messagewarningscore.core.service.bo.WeatherAutoRecordBO;
import com.emdata.messagewarningscore.core.service.bo.WeatherAutoRecordTableBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 重要天气自动记录相关接口
 * @date: 2020/12/14
 * @author: sunming
 */
@RequestMapping("record")
@RestController
@Api(tags = "重要天气自动记录相关接口")
public class WeatherAutoRecordController {

    @Autowired
    private IWeatherAutoRecordService iWeatherAutoRecordService;

    @GetMapping("page")
    @ApiOperation(value = "重要天气自动记录查询")
    public ResultData<List<WeatherAutoRecordBO>> weatherAutoRecordPage(WeatherAutoRecordQuery weatherAutoRecordQuery) {
        String airportCode = ThreadLocalUtils.getAirportCode();
        List<WeatherAutoRecordBO> weatherAutoRecordDOS = iWeatherAutoRecordService
                .weatherAutoRecordPage(airportCode, weatherAutoRecordQuery);
        return ResultData.success(weatherAutoRecordDOS);
    }
}
