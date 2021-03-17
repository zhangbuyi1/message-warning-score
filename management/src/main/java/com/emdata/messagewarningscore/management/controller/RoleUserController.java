package com.emdata.messagewarningscore.management.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.RoleUserAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleUserQueryPageParam;
import com.emdata.messagewarningscore.management.entity.RoleUserDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.RoleUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author changfeng
 * @description 角色用户控制类
 * @date 2019/12/16
 */
@RestController
@Api(tags = "角色用户API", description = "角色用户API")
@RequestMapping("roles_users")
public class RoleUserController {

    @Autowired
    private RoleUserService roleUserService;

    @ApiOperation(value = "新增、修改、删除角色用户关系")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData addRoleUser(@RequestBody @Valid RoleUserAddParam param) {
        roleUserService.addRoleUser(param);
        return ResultData.success();
    }

//    @ApiOperation(value = "修改角色用户关系")
//    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultData<RoleUserDO> updateRole(@RequestBody @Valid RoleUpdateParam param) {
//        return ResultData.success(roleUserService.updateRole(param));
//    }

//    @LogMaker(type = LogType.DELETE)
//    @ApiOperation(value = "删除角色用户关系")
//    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultData deleteRole(@RequestParam(value = "roleAuthorityId", required = true) Integer roleId, String name) {
////        roleUserService.deleteRole(roleId);
//        return ResultData.success();
//    }

    @ApiOperation(value = "查询角色用户关系")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<RoleUserDO>> queryRoleUser(RoleUserQueryPageParam param) {
        return ResultData.success(roleUserService.queryRoleUser(param));
    }

    @ApiOperation(value = "分页查询角色用户关系")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<RoleUserDO>> queryRoleUserPage(RoleUserQueryPageParam param) {
        return ResultData.success(roleUserService.queryRoleUserPage(param));
    }
}
