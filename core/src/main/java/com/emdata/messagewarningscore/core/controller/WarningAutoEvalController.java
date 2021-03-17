package com.emdata.messagewarningscore.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.common.common.utils.ThreadLocalUtils;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.core.controller.vo.WarningAutoEvalQueryParam;
import com.emdata.messagewarningscore.core.service.IWarningAutoEvalService;
import com.emdata.messagewarningscore.core.service.bo.WarningAutoEvalBO;
import com.emdata.messagewarningscore.core.service.bo.WarningAutoEvalTableBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 预警自动评估相关接口
 * @date: 2020/12/14
 * @author: sunming
 */
@RequestMapping("evaluate")
@RestController
@Api(tags = "机场预警自动评估相关接口", description = "机场预警自动评估相关接口")
public class WarningAutoEvalController {

    @Autowired
    private IWarningAutoEvalService iWarningAutoEvalService;


    @ApiOperation(value = "机场预警自动评估查询")
    @GetMapping("page")
    public ResultData<List<WarningAutoEvalBO>> warningAutoEvalPage(WarningAutoEvalQueryParam warningAutoEvalQueryParam) {
        // 机场代码从token中取
        String airportCode = ThreadLocalUtils.getAirportCode();
        List<WarningAutoEvalBO> warningAutoEvalDOS = iWarningAutoEvalService
                .warningAutoEvalPage(airportCode, warningAutoEvalQueryParam);
        return ResultData.success(warningAutoEvalDOS);
    }
}
