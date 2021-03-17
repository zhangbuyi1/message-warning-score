package com.emdata.messagewarningscore.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.LogQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.LogWriteParam;
import com.emdata.messagewarningscore.management.entity.LogDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author changfeng
 * @description 日志相关接口
 * @date 2020/2/26
 */
@RestController
@Api(tags = "日志相关接口", description = "日志相关接口")
@RequestMapping("log")
public class LogController {

    @Autowired
    private LogService logService;

    @ApiOperation(value = "新增日志", notes = "新增日志")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData addLog(@RequestBody LogWriteParam param) {

        logService.addLog(param);
        return ResultData.success();
    }


    @ApiOperation(value = "分页查询日志信息")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<LogDO>> queryLogPage(LogQueryPageParam param) {
        return ResultData.success(logService.queryLogPage(param));
    }

}
