package com.emdata.messagewarningscore.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.FtpInfoParam;
import com.emdata.messagewarningscore.management.controller.vo.FtpInfoQueryParam;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.FtpInfoDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.FtpInfoService;
import com.emdata.messagewarningscore.management.service.bo.FtpInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/2
 * @description:
 */
@RestController
@Api(tags = "ftp详细信息相关接口", description = "ftp详细信息相关接口")
@RequestMapping("ftps")
public class FtpInfoController {

    @Autowired
    private FtpInfoService ftpInfoService;

    @LogAnnotation(type = LogTypeEnum.INSERT, field = {"ftpName", "ftpHost"}, prefix = {"ftp名称", "ip地址"})
    @ApiOperation(value = "新增ftp详细信息", notes = "新增ftp详细信息")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<FtpInfoDO> createFtpInfo(@RequestBody FtpInfoParam ftpInfoParam) {

        return ftpInfoService.createFtpInfo(ftpInfoParam);
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE)
    @ApiOperation(value = "编辑ftp详细信息", notes = "编辑ftp详细信息")
    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<FtpInfoDO> updateFtpInfo(@RequestBody FtpInfoParam ftpInfoParam) {

        return ftpInfoService.updateFtpInfo(ftpInfoParam);
    }

    @ApiOperation(value = "获取所有ftp详细信息", notes = "获取所有ftp详细信息")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<FtpInfoBO>> obtainFtpInfo() {

        return ftpInfoService.obtainFtpInfo();
    }

    @ApiOperation(value = "测试ftp连接", notes = "测试ftp连接")
    @GetMapping(value = "check", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> checkFtpInfo(FtpInfoParam ftpInfoParam) {
        return ftpInfoService.checkFtpInfo(ftpInfoParam);
    }

    @ApiOperation(value = "分页查询ftp信息")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<FtpInfoBO>> queryFtpInfoPage(FtpInfoQueryParam param) {
        return ResultData.success(ftpInfoService.queryFtpInfoPage(param));
    }

    @ApiOperation(value = "根据id获取ftp", notes = "根据id获取ftp")
    @GetMapping("{id}")
    public ResultData<FtpInfoBO> getById(@PathVariable("id") Integer id) {
        if (id == null) {
            return ResultData.error("id不能为空");
        }
        FtpInfoDO ftpInfoDO = ftpInfoService.getById(id);
        FtpInfoBO ftpInfoBO = new FtpInfoBO();
        BeanUtils.copyProperties(ftpInfoDO, ftpInfoBO);
        return ResultData.success(ftpInfoBO);
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = "id")
    @ApiOperation(value = "删除ftp详细信息", notes = "删除ftp详细信息")
    @DeleteMapping
    public ResultData<FtpInfoBO> deleteFtpInfo(@RequestParam("id") Integer id) {
        return ftpInfoService.deleteFtpInfo(id);
    }

}
