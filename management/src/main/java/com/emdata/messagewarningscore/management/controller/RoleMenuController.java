package com.emdata.messagewarningscore.management.controller;


import com.emdata.messagewarningscore.management.controller.vo.RoleMenuQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuVo;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.RoleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author changfeng
 * @description 角色菜单控制类
 * @date 2019/12/16
 */
@RestController
@Api(tags = "角色菜单API", description = "角色菜单API")
@RequestMapping("roles_menus")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

//    @ApiOperation(value = "新增、修改、删除角色权限关系")
//    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultData addRoleAuthority(@RequestBody @Valid RoleAuthorityAddParam param) {
//        roleAuthorityService.addRoleAuthority(param);
//        return ResultData.success();
//    }

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

    @ApiOperation(value = "查询角色菜单关系")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<RoleMenuVo>> queryRoleAuthority(RoleMenuQueryPageParam param) {
        return ResultData.success(roleMenuService.queryRoleMenu(param));
    }

//    @ApiOperation(value = "分页查询角色权限关系")
//    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultData<Page<RoleAuthorityDO>> queryRoleAuthorityPage(RoleAuthorityQueryPageParam param) {
//        return ResultData.success(roleAuthorityService.queryRoleAuthorityPage(param));
//    }
}
