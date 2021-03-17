package com.emdata.messagewarningscore.management.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.UserDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author changfeng
 * @description 用户控制类
 * @date 2019/12/14
 */
@RestController
@Api(tags = "用户API", description = "用户API")
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    @LogAnnotation(type = LogTypeEnum.INSERT, prefix = {"用户名称"}, field = {"name"})
    @ApiOperation(value = "新增用户")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<UserDO> register(@RequestBody @Valid UserAddParam param) {
        return ResultData.success(userService.addUser(param));
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE, prefix = {"用户id"}, field = {"id"})
    @ApiOperation(value = "修改用户信息")
    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<UserDO> updateUser(@RequestBody @Valid UserUpdateParam param) {
        return ResultData.success(userService.updateUser(param));
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = {"用户id"}, field = {"id"})
    @ApiOperation(value = "删除用户信息")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData deleteUser(@RequestBody @Valid UserDeleteParam param) {
        userService.deleteUser(param);
        return ResultData.success();
    }

    @ApiOperation(value = "查询用户信息")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<UserDO>> queryUser(UserQueryPageParam param) {
        return ResultData.success(userService.queryUser(param));
    }

    @ApiOperation(value = "查询携带角色信息的用户信息")
    @GetMapping(value = "page/withrole", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<UserVO>> queryUserWithRole(UserQueryPageParam param) {
        return ResultData.success(userService.queryUserWithRole(param));
    }

    @ApiOperation(value = "分页查询用户信息")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<UserDO>> queryUserPage(UserQueryPageParam param) {
        return ResultData.success(userService.queryUserPage(param));
    }


}
