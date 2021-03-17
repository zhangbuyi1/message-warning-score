package com.emdata.messagewarningscore.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.RoleAuthorityAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleAuthorityQueryPageParam;
import com.emdata.messagewarningscore.management.entity.RoleAuthorityDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.RoleAuthorityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author changfeng
 * @description 角色权限控制类
 * @date 2019/12/16
 */
@RestController
@Api(tags = "角色权限API", description = "角色权限API")
@RequestMapping("roles_authoritys")
public class RoleAuthorityController {

    @Autowired
    private RoleAuthorityService roleAuthorityService;

    @ApiOperation(value = "新增、修改、删除角色权限关系")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData addRoleAuthority(@RequestBody @Valid RoleAuthorityAddParam param) {
        roleAuthorityService.addRoleAuthority(param);
        return ResultData.success();
    }

//    @ApiOperation(value = "修改角色权限关系")
//    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultData<RoleAuthorityDO> updateRole(@RequestBody @Valid RoleUpdateParam param) {
//        return ResultData.success(roleAuthorityService.updateRole(param));
//    }

//    @ApiOperation(value = "删除角色权限关系")
//    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultData deleteRole(@RequestParam(value = "roleAuthorityId", required = true) Integer roleId) {
//        roleAuthorityService.deleteRole(roleId);
//        return ResultData.success();
//    }

    @ApiOperation(value = "查询角色权限关系")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<RoleAuthorityDO>> queryRoleAuthority(RoleAuthorityQueryPageParam param) {
        return ResultData.success(roleAuthorityService.queryRoleAuthority(param));
    }

    @ApiOperation(value = "分页查询角色权限关系")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<RoleAuthorityDO>> queryRoleAuthorityPage(RoleAuthorityQueryPageParam param) {
        return ResultData.success(roleAuthorityService.queryRoleAuthorityPage(param));
    }
}
