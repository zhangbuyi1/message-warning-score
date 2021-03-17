package com.emdata.messagewarningscore.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.AuthorityDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.AuthorityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author changfeng
 * @description 权限控制类
 * @date 2019/12/14
 */
@RestController
@Api(tags = "权限API", description = "权限API")
@RequestMapping("authoritys")
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;

    @LogAnnotation(type = LogTypeEnum.INSERT, prefix = {"权限名称", "权限地址"}, field = {"name", "url"})
    @ApiOperation(value = "新增权限")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<AuthorityDO> addAuthority(@RequestBody @Valid AuthorityAddParam param) {
        return ResultData.success(authorityService.addAuthority(param));
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE, prefix = {"权限id"}, field = {"id"})
    @ApiOperation(value = "修改权限信息")
    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<AuthorityDO> updateAuthority(@RequestBody @Valid AuthorityUpdateParam param) {
        return ResultData.success(authorityService.updateAuthority(param));
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = {"权限id"}, field = {"id"})
    @ApiOperation(value = "删除权限信息")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<String> deleteAuthority(@RequestBody @Valid AuthorityDeleteParam param) {
        authorityService.deleteAuthority(param);
        return ResultData.success();
    }

    @ApiOperation(value = "查询权限信息")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<AuthorityDO>> queryAuthority(AuthorityQueryPageParam param) {
        return ResultData.success(authorityService.queryAuthority(param));
    }

    @ApiOperation(value = "分页查询权限信息")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<AuthorityDO>> queryAuthorityPage(AuthorityQueryPageParam param) {
        return ResultData.success(authorityService.queryAuthorityPage(param));
    }

    @ApiOperation(value = "查看树级信息")
    @GetMapping(value = "/findTree", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<AuthorityTreeVo>> findTree() {
        return ResultData.success(authorityService.findTree());
    }


    @ApiOperation(value = "根据权限id查看多级权限结构")
    @GetMapping(value = "/findAuthoritysByAuthorityId/{authorityId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<AuthorityRelationVo> findAuthoritysByAuthorityId(@ApiParam(value = "权限id", required = true) @PathVariable(value = "authorityId") Integer authorityId) {
        return ResultData.success(authorityService.findAuthoritysByAuthorityId(authorityId));
    }

    @ApiOperation(value = "根据权限id查询父级list")
    @GetMapping(value = "/findListByAuthorityId/{authorityId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<Integer>> findListByAuthorityId(@ApiParam(value = "权限id", required = true) @PathVariable(value = "authorityId") Integer authorityId) {
        return ResultData.success(authorityService.findListByAuthorityId(authorityId));
    }
}
