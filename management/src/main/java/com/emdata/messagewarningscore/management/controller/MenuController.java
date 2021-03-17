package com.emdata.messagewarningscore.management.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.MenuDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.MenuService;
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
 * @description 菜单控制类
 * @date 2019/12/14
 */
@RestController
@Api(tags = "菜单API", description = "菜单API")
@RequestMapping("menus")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @LogAnnotation(type = LogTypeEnum.INSERT, prefix = {"菜单名称", "菜单路由", "父级id"}, field = {"name", "route", "parentId"})
    @ApiOperation(value = "新增菜单")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<MenuDO> addMenu(@RequestBody @Valid MenuAddParam param) {
        return ResultData.success(menuService.addMenu(param));
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE, prefix = {"菜单id"}, field = {"id"})
    @ApiOperation(value = "修改菜单信息")
    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<MenuDO> updateMenu(@RequestBody @Valid MenuUpdateParam param) {
        return ResultData.success(menuService.updateMenu(param));
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = {"菜单id"}, field = {"id"})
    @ApiOperation(value = "删除菜单信息")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<String> deleteMenu(@RequestBody @Valid MenuDeleteParam param) {
        menuService.deleteMenu(param);
        return ResultData.success();
    }

    @ApiOperation(value = "查询菜单信息")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<MenuDO>> queryMenu(MenuQueryPageParam param) {
        return ResultData.success(menuService.queryMenu(param));
    }

    @ApiOperation(value = "分页查询菜单信息")
    @GetMapping(value = "page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Page<MenuDO>> queryMenuPage(MenuQueryPageParam param) {
        return ResultData.success(menuService.queryMenuPage(param));
    }

    @ApiOperation(value = "查看所有树级信息")
    @GetMapping(value = "/findTree",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<MenuTreeVo>> findTree() {
        return ResultData.success(menuService.findTree());
    }

    @ApiOperation(value = "根据用户id查看树级信息")
    @GetMapping(value = "/findTreeByUser",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<MenuTreeVo>> findTreeByUser(@RequestParam Integer userId) {
        return ResultData.success(menuService.findTreeByUser(userId));
    }

    @ApiOperation(value = "根据菜单id查找多级菜单结构")
    @GetMapping(value = "/findMenusByMenuId/{menuId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<MenuRelationVo> findMenusByMenuId(@ApiParam(value = "菜单id", required = true) @PathVariable(value = "menuId") Integer menuId) {
        return ResultData.success(menuService.findMenusByMenuId(menuId));
    }

    @ApiOperation(value = "根据菜单id查询父级list")
    @GetMapping(value = "/findListByMenuId/{menuId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<List<Integer>> findListByMenuId(@ApiParam(value = "菜单id", required = true) @PathVariable(value = "menuId") Integer menuId) {
        return ResultData.success(menuService.findListByMenuId(menuId));
    }


}
