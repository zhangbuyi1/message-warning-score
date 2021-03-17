package com.emdata.messagewarningscore.management.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.PlaceAddParam;
import com.emdata.messagewarningscore.management.controller.vo.PlaceQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.PlaceUpdateParam;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.AreaCustomPlaceDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.AreaCustomPlaceService;
import com.emdata.messagewarningscore.management.service.bo.AreaCustomPlaceBO;
import com.emdata.messagewarningscore.management.service.bo.AreaCustomPlacePageBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: sunming
 * @date: 2020/1/10
 * @description: 地点信息controller
 */
@RestController
@Api(tags = "自定义地点相关接口", description = "地点相关接口")
@RequestMapping("area_custom_places")
public class AreaCustomPlaceController {

    @Autowired
    private AreaCustomPlaceService areaCustomPlaceService;

    @ApiOperation(value = "根据地点编码查询地点信息", notes = "根据地点编码查询地点信息")
    @GetMapping
    public ResultData<AreaCustomPlaceBO> findByPlaceCode(@RequestParam("placeCode") String placeCode) {
        if (StringUtils.isEmpty(placeCode)) {
            return ResultData.error("地点编码为空");
        }
        return ResultData.success(areaCustomPlaceService.findByPlaceCode(placeCode));
    }

    @LogAnnotation(type = LogTypeEnum.INSERT, field = {"placeName", "placeCode"}, prefix = {"地点名称", "地点编码"})
    @ApiOperation(value = "新增地点信息", notes = "新增地点信息")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<AreaCustomPlaceDO> createPlace(@RequestBody @Valid PlaceAddParam param) {
        if (param.getParentCode().length() == 6 && param.getParentCode().endsWith("00")) {
            return ResultData.error("不能选择省/市级，作为上级地点");
        }
        return ResultData.success(areaCustomPlaceService.createPlace(param));
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = "地点编码")
    @ApiOperation(value = "删除地点信息", notes = "删除地点信息")
    @DeleteMapping
    public ResultData<AreaCustomPlaceDO> deleteByPlaceCode(@ApiParam(value = "地点编码") @RequestParam(value = "placeCode", required = true) String placeCode) {
        return ResultData.success(areaCustomPlaceService.deleteByPlaceCode(placeCode));
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE,  field = {"placeName", "placeCode"}, prefix = {"地点名称", "地点编码"})
    @ApiOperation(value = "修改地点信息", notes = "修改地点信息")
    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<AreaCustomPlaceDO> updataPlace(@RequestBody @Valid PlaceUpdateParam param) {
        if (param.getParentCode().length() == 6 && param.getParentCode().endsWith("00")) {
            return ResultData.error("不能选择省/市级，作为上级地点");
        }
        return ResultData.success(areaCustomPlaceService.updataPlace(param));
    }

    @ApiOperation(value = "分页查询地点信息")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<AreaCustomPlacePageBO>> queryAreaCustomPlacePage(PlaceQueryPageParam param) {
        return ResultData.success(areaCustomPlaceService.queryAreaCustomPlacePage(param));
    }
}
