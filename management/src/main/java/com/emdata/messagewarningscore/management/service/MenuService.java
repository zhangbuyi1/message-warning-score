package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.management.entity.MenuDO;

import java.util.List;

/**
 * @author changfeng
 * @description
 * @date 2019/12/4
 */
public interface MenuService extends IService<MenuDO> {
    /**
     * 新增菜单
     * @param param
     * @return
     */
    MenuDO addMenu(MenuAddParam param);

    /**
     * 修改菜单信息
     * @param param
     * @return
     */
    MenuDO updateMenu(MenuUpdateParam param);

    /**
     * 删除菜单
     * @param param
     */
    void deleteMenu(MenuDeleteParam param);

    /**
     * 查询菜单
     * @param param
     */
    List<MenuDO> queryMenu(MenuQueryPageParam param);

    /**
     * 分页查询
     * @param param
     * @return
     */
    Page<MenuDO> queryMenuPage(MenuQueryPageParam param);

    List<MenuTreeVo> findTree();

    List<MenuTreeVo> findTreeByUser(Integer userId);

    /**
     * 根据菜单id查询菜单关系
     * @param menuId
     * @return
     */
    MenuRelationVo findMenusByMenuId(Integer menuId);

    List<Integer> findListByMenuId(Integer menuId);
}
