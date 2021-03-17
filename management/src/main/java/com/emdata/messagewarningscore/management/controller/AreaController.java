package com.emdata.messagewarningscore.management.controller;

import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.AreaService;
import com.emdata.messagewarningscore.management.service.bo.AreaBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
@RestController
@Api(tags = "地区联级查询接口", description = "地区相关接口")
@RequestMapping("area")
public class AreaController {

    @Autowired
    private AreaService areaService;


    @ApiOperation(value = "查询下级地点", notes = "查询省市区地区、自定义地点的下级地点，code为空则，则查询所有省级")
    @GetMapping
    public ResultData<List<AreaBO>> findAreas(@ApiParam(value = "地区编码") @RequestParam(value = "code", required = false) String code) {
        List<AreaBO> areas = areaService.findAreas(code);
        return ResultData.success(areas);
    }
}
