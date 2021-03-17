package com.emdata.messagewarningscore.core.controller;

import com.emdata.messagewarningscore.common.common.utils.ThreadLocalUtils;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.core.controller.vo.MdrsAutoEvalQueryParam;
import com.emdata.messagewarningscore.core.service.IMdrsAutoEvalService;
import com.emdata.messagewarningscore.core.service.bo.MdrsAutoEvalAllBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: mdrs自动评估相关接口
 * @date: 2020/12/16
 * @author: sunming
 */
@RequestMapping("mdrs")
@RestController
@Api(tags = "mdrs自动评估相关接口")
public class MdrsAutoEvalController {

    @Autowired
    private IMdrsAutoEvalService iMdrsAutoEvalService;

    @GetMapping("page")
    @ApiOperation(value = "mdrs自动评估查询")
    public ResultData<List<MdrsAutoEvalAllBO>> mdrsAutoEvalPage(MdrsAutoEvalQueryParam mdrsAutoEvalQueryParam) {
        String airportCode = ThreadLocalUtils.getAirportCode();
        List<MdrsAutoEvalAllBO> mdrsAutoEvalDOPage = iMdrsAutoEvalService
                .mdrsAutoEvalPage(airportCode, mdrsAutoEvalQueryParam);
        return ResultData.success(mdrsAutoEvalDOPage);
    }
}
