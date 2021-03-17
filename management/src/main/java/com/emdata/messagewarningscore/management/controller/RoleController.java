package com.emdata.messagewarningscore.management.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.RoleAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleDeleteParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleUpdateParam;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.RoleDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author changfeng
 * @description 
 * @date 2019/12/16
 */
@RestController
@Api(tags = "角色API", description = "角色API")
@RequestMapping("roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @LogAnnotation(type = LogTypeEnum.INSERT, prefix = {"角色名称"}, field = {"name"})
    @ApiOperation(value = "新增角色")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<RoleDO> addRole(@RequestBody @Valid RoleAddParam param) {
        return ResultData.success(roleService.addRole(param));
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE, prefix = {"角色id"}, field = {"id"})
    @ApiOperation(value = "修改角色信息")
    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<RoleDO> updateRole(@RequestBody @Valid RoleUpdateParam param) {
        return ResultData.success(roleService.updateRole(param));
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = {"角色id"}, field = {"id"})
    @ApiOperation(value = "删除角色信息")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData deleteRole(@RequestBody @Valid RoleDeleteParam param) {
        roleService.deleteRole(param);
        return ResultData.success();
    }

    @ApiOperation(value = "查询角色信息")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<RoleDO>> queryRole(RoleQueryPageParam param) {
        return ResultData.success(roleService.queryRole(param));
    }

    @ApiOperation(value = "分页查询角色信息")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<RoleDO>> queryRolePage(RoleQueryPageParam param) {
        Page<RoleDO> page = roleService.queryRolePage(param);
        return ResultData.success(page);
    }
}
