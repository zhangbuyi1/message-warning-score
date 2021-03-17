package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.common.common.constant.UserConsts;
import com.emdata.messagewarningscore.management.dao.MenuMapper;
import com.emdata.messagewarningscore.management.entity.MenuDO;
import com.emdata.messagewarningscore.management.entity.RoleMenuDO;
import com.emdata.messagewarningscore.common.enums.MenuLevelEnum;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.MenuService;
import com.emdata.messagewarningscore.management.service.RoleMenuService;
import com.emdata.messagewarningscore.management.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author changfeng
 * @description
 * @date 2019/12/4
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuDO> implements MenuService {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public MenuDO addMenu(MenuAddParam param) {
        // 根据菜单地址值验重
        MenuDO menuInBase = queryMenuByUrl(param.getRoute());
        if (menuInBase != null) {
            throw new BusinessException(ResultCodeEnum.MENU_HAS_EXIST.getCode(), ResultCodeEnum.MENU_HAS_EXIST.getMessage());
        }
        // 定义菜单实体类准备入库
        MenuDO menu = new MenuDO();
        BeanUtils.copyProperties(param, menu);
        // 判断是否根节点
        if (UserConsts.TOP_ID.equals(param.getParentId())) {
            menu.setLevel(1);
        } else {
            // 不是根节点级别为父级+1
            MenuDO menuParent = menuMapper.selectById(param.getParentId());
            menu.setLevel(menuParent.getLevel() + 1);
        }
        // 判断该上级下是否有该序号
        LambdaQueryWrapper<MenuDO> lqwMenu = new LambdaQueryWrapper<>();
        lqwMenu.eq(MenuDO::getState, StateEnum.ON.getCode())
                .eq(MenuDO::getParentId, param.getParentId());
        List<MenuDO> menuChilds = menuMapper.selectList(lqwMenu);
        for (MenuDO menuChild : menuChilds) {
            if (param.getMenuIndex().equals(menuChild.getMenuIndex())){
                throw new BusinessException(ResultCodeEnum.MENU_INDEX_CONFLICT.getCode(), ResultCodeEnum.MENU_INDEX_CONFLICT.getMessage());
            }
        }
        int insert = menuMapper.insert(menu);
        if (insert != 1) {
            throw new BusinessException(ResultCodeEnum.MENU_ADD_ERROR.getCode(), ResultCodeEnum.MENU_ADD_ERROR.getMessage());
        }
//        if (CollectionUtils.isNotEmpty(param.getAuthorityIds()) || CollectionUtils.isNotEmpty(param.getUnAuthorityIds())) {
//            // 绑定权限
//            MenuAuthorityAddParam menuAuthorityAddParam = new MenuAuthorityAddParam();
//            menuAuthorityAddParam.setMenuId(menu.getId());
//            menuAuthorityAddParam.setAuthorityId(param.getAuthorityIds());
//            menuAuthorityAddParam.setUnAuthorityId(param.getUnAuthorityIds());
//            menuAuthorityService.addMenuAuthority(menuAuthorityAddParam);
//        }

        return menu;
    }

    @Override
    public MenuDO updateMenu(MenuUpdateParam param) {
        MenuDO menuInBase = menuMapper.selectById(param.getId());
        if (menuInBase == null) {
            throw new BusinessException(ResultCodeEnum.MENU_NOT_EXIST.getCode(), ResultCodeEnum.MENU_NOT_EXIST.getMessage());
        }
        if (param.getParentId() == null) {
            param.setParentId(UserConsts.TOP_ID);
        }
        if (param.getMenuIndex() != null && !param.getMenuIndex().equals(menuInBase.getMenuIndex())){
            // 判断该上级下是否有该序号
            LambdaQueryWrapper<MenuDO> lqwMenu = new LambdaQueryWrapper<>();
            lqwMenu.eq(MenuDO::getState, StateEnum.ON.getCode())
                    .eq(MenuDO::getParentId, param.getParentId());
            List<MenuDO> menuChilds = menuMapper.selectList(lqwMenu);
            for (MenuDO menuChild : menuChilds) {
                if (param.getMenuIndex().equals(menuChild.getMenuIndex())){
                    throw new BusinessException(ResultCodeEnum.MENU_INDEX_CONFLICT.getCode(), ResultCodeEnum.MENU_INDEX_CONFLICT.getMessage());
                }
            }
        }
        BeanUtils.copyProperties(param, menuInBase);
        // 根据父菜单id计算菜单级别
        if (UserConsts.TOP_ID.equals(menuInBase.getParentId())) {
            menuInBase.setLevel(MenuLevelEnum.ONE.getCode());
        } else {
            MenuDO menuPa = menuMapper.selectById(menuInBase.getParentId());
            menuInBase.setLevel(menuPa.getLevel() + 1);
        }

        int updateById = menuMapper.updateById(menuInBase);
        if (updateById != 1) {
            throw new BusinessException(ResultCodeEnum.MENU_UPDATE_ERROR.getCode(), ResultCodeEnum.MENU_UPDATE_ERROR.getMessage());
        }
//        MenuAuthorityAddParam menuAuthorityAddParam = new MenuAuthorityAddParam();
//        menuAuthorityAddParam.setMenuId(param.getId());
//        menuAuthorityAddParam.setAuthorityId(param.getAuthorityIds());
//        menuAuthorityAddParam.setUnAuthorityId(param.getUnAuthorityIds());
//        menuAuthorityService.addMenuAuthority(menuAuthorityAddParam);
        return menuInBase;
    }

    @Override
    @Transactional
    public void deleteMenu(MenuDeleteParam param) {
        MenuDO menuInBase = menuMapper.selectById(param.getId());
        if (menuInBase == null) {
            throw new BusinessException(ResultCodeEnum.MENU_NOT_EXIST.getCode(), ResultCodeEnum.MENU_NOT_EXIST.getMessage());
        }
        // 如果有下级菜单，不允许删除
        LambdaQueryWrapper<MenuDO> lqwMenu = new LambdaQueryWrapper<>();
        lqwMenu.eq(MenuDO::getState, StateEnum.ON.getCode())
                .eq(MenuDO::getParentId, menuInBase.getId());
        List<MenuDO> menuListInBase = menuMapper.selectList(lqwMenu);
        if (CollectionUtils.isNotEmpty(menuListInBase)) {
            throw new BusinessException(ResultCodeEnum.MENU_DELETE_ERROR_CAUSE_CHILD.getCode(), ResultCodeEnum.MENU_DELETE_ERROR_CAUSE_CHILD.getMessage());
        }
        // 如果有绑定角色，不允许删除
//        LambdaQueryWrapper<RoleMenuDO> lqwRoleMenu = new LambdaQueryWrapper<>();
//        lqwRoleMenu.eq(RoleMenuDO::getState, StateEnum.ON.getCode())
//                .eq(RoleMenuDO::getMenuId, param.getId());
//        List<RoleMenuDO> roleMenuListInBase = roleMenuService.list(lqwRoleMenu);
//        if (CollectionUtils.isNotEmpty(roleMenuListInBase)) {
//            throw new BusinessException(ResultCodeEnum.MENU_DELETE_ERROR_CAUSE_ROLE.getCode(), ResultCodeEnum.MENU_DELETE_ERROR_CAUSE_ROLE.getMessage());
//        }

        // 逻辑删除菜单
        menuInBase.setState(StateEnum.OFF.getCode());
        int delete = menuMapper.updateById(menuInBase);
        if (delete != 1) {
            throw new BusinessException(ResultCodeEnum.MENU_DELETE_ERROR.getCode(), ResultCodeEnum.MENU_DELETE_ERROR.getMessage());
        }
        // 物理删除角色菜单中所有关联该菜单的关系
        LambdaQueryWrapper<RoleMenuDO> lqwRoleMenu = new LambdaQueryWrapper<>();
        lqwRoleMenu.eq(RoleMenuDO::getMenuId, param.getId());
        List<RoleMenuDO> roleMenuListInBase = roleMenuService.list(lqwRoleMenu);
        if (CollectionUtils.isNotEmpty(roleMenuListInBase)) {
            boolean remove = roleMenuService.remove(lqwRoleMenu);
            if (!remove) {
                throw new BusinessException(ResultCodeEnum.ROLE_MENU_DELETE_ERROR.getCode(), ResultCodeEnum.ROLE_MENU_DELETE_ERROR.getMessage());
            }
        }
        // 物理删除菜单权限中所有关联该菜单的关系
//        LambdaQueryWrapper<MenuAuthorityDO> lqwMenuAuthority = new LambdaQueryWrapper<>();
//        lqwMenuAuthority.eq(MenuAuthorityDO::getMenuId, param.getId());
//        List<MenuAuthorityDO> menuAuthorityListInBase = menuAuthorityService.list(lqwMenuAuthority);
//        if (CollectionUtils.isEmpty(menuAuthorityListInBase)) {
//            return;
//        }
//        boolean remove = menuAuthorityService.remove(lqwMenuAuthority);
//        if (!remove) {
//            throw new BusinessException(ResultCodeEnum.MENU_AUTHORITY_DELETE_ERROR.getCode(), ResultCodeEnum.MENU_AUTHORITY_DELETE_ERROR.getMessage());
//        }
    }

    @Override
    public List<MenuDO> queryMenu(MenuQueryPageParam param) {
        LambdaQueryWrapper<MenuDO> lqwMenu = new LambdaQueryWrapper<>();
        lqwMenu.eq(MenuDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, MenuDO::getId, param.getId())
                .eq(StringUtils.isNoneBlank(param.getName()), MenuDO::getName, param.getName())
                .eq(StringUtils.isNoneBlank(param.getRoute()), MenuDO::getRoute, param.getRoute())
                .eq(param.getLevel() != null, MenuDO::getLevel, param.getLevel())
                .eq(param.getParentId() != null, MenuDO::getParentId, param.getParentId());
        List<MenuDO> menuListInBase = menuMapper.selectList(lqwMenu);
        if (CollectionUtils.isEmpty(menuListInBase)) {
            throw new BusinessException(ResultCodeEnum.MENU_NOT_EXIST.getCode(), ResultCodeEnum.MENU_NOT_EXIST.getMessage());
        }
        return menuListInBase;
    }

    @Override
    public Page<MenuDO> queryMenuPage(MenuQueryPageParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<MenuDO> lqwMenu = new LambdaQueryWrapper<>();
        lqwMenu.eq(MenuDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, MenuDO::getId, param.getId())
                .like(StringUtils.isNoneBlank(param.getName()), MenuDO::getName, param.getName())
                .eq(StringUtils.isNoneBlank(param.getRoute()), MenuDO::getRoute, param.getRoute())
                .eq(param.getLevel() != null, MenuDO::getLevel, param.getLevel())
                .eq(param.getParentId() != null, MenuDO::getParentId, param.getParentId());
        Page<MenuDO> page = new Page<>(current, size);
        if (StringUtils.isBlank(param.getOrderBy())) {
            param.setOrderBy("created_time");
        }
        if (param.getSort() == Sort.Direction.DESC) {
            page.setDesc(param.getOrderBy());
        } else {
            page.setAsc(param.getOrderBy());
        }
        IPage<MenuDO> menuIPage = menuMapper.selectPage(page, lqwMenu);
        return page;
    }

    @Override
    public List<MenuTreeVo> findTree() {
        List<MenuTreeVo> menuTreeVoList = new ArrayList<>();
        LambdaQueryWrapper<MenuDO> lqwMenu = new LambdaQueryWrapper<>();
        lqwMenu.eq(MenuDO::getState, StateEnum.ON.getCode());
        List<MenuDO> menuListInBase = menuMapper.selectList(lqwMenu);

        for (MenuDO menu : menuListInBase) {
            if (menu.getParentId() == 0) {
                MenuTreeVo menuTreeVo = new MenuTreeVo();
                BeanUtils.copyProperties(menu, menuTreeVo);
//                menuTreeVo.setAuthorityIds(queryAuthoritysByid(menuTreeVo.getId(), true));
//                menuTreeVo.setUnAuthorityIds(queryAuthoritysByid(menuTreeVo.getId(), false));
                if (!exists(menuTreeVoList, menu)) {
                    menuTreeVoList.add(menuTreeVo);
                }
            }
        }
        menuTreeVoList.sort(Comparator.comparing(MenuTreeVo::getMenuIndex));
        findChildren(menuTreeVoList, menuListInBase);
        return menuTreeVoList;
    }

    @Override
    public List<MenuTreeVo> findTreeByUser(Integer userId) {
        List<MenuDO> menuListInBase = userService.queryMenuListByUserId(userId);
        List<MenuTreeVo> menuTreeVoList = new ArrayList<>();
        for (MenuDO menu : menuListInBase) {
            if (menu.getParentId() == 0) {
                MenuTreeVo menuTreeVo = new MenuTreeVo();
                BeanUtils.copyProperties(menu, menuTreeVo);
//                menuTreeVo.setAuthorityIds(queryAuthoritysByid(menuTreeVo.getId(), true));
//                menuTreeVo.setUnAuthorityIds(queryAuthoritysByid(menuTreeVo.getId(), false));
                if (!exists(menuTreeVoList, menu)) {
                    menuTreeVoList.add(menuTreeVo);
                }
            }
        }
        menuTreeVoList.sort(Comparator.comparing(MenuTreeVo::getMenuIndex));
        findChildren(menuTreeVoList, menuListInBase);
        return menuTreeVoList;
    }

    @Override
    public MenuRelationVo findMenusByMenuId(Integer menuId) {
        MenuDO menuInBase = menuMapper.selectById(menuId);
        if (menuInBase == null || StateEnum.OFF.getCode().equals(menuInBase.getState())) {
            throw new BusinessException(ResultCodeEnum.MENU_NOT_EXIST.getCode(), ResultCodeEnum.MENU_NOT_EXIST.getMessage());
        }
        MenuRelationVo menuRelationVo = new MenuRelationVo();
        menuRelationVo.setMenuId(menuInBase.getId());
        menuRelationVo.setMenuName(menuInBase.getName());
        return getCircleMenu(menuInBase, menuRelationVo);
    }

    @Override
    public List<Integer> findListByMenuId(Integer menuId) {
        List<Integer> parents = new ArrayList<>();
        MenuDO menuInBase = menuMapper.selectById(menuId);
        if (menuInBase == null || StateEnum.OFF.getCode().equals(menuInBase.getState())) {
            throw new BusinessException(ResultCodeEnum.MENU_NOT_EXIST.getCode(), ResultCodeEnum.MENU_NOT_EXIST.getMessage());
        }
        parents = getParentsMenu(menuInBase, parents);
        Collections.reverse(parents);
        return parents;
    }

    private List<Integer> getParentsMenu(MenuDO menuDO, List<Integer> list){
        list.add(menuDO.getId());
        if (UserConsts.TOP_ID.equals(menuDO.getParentId())){
            return list;
        }
        MenuDO menuPa = menuMapper.selectById(menuDO.getParentId());
        return getParentsMenu(menuPa, list);
    }

    /**
     * 获取菜单关系遍历方法
     *
     * @param menuDO
     * @param menuRelationVo
     * @return
     */
    private MenuRelationVo getCircleMenu(MenuDO menuDO, MenuRelationVo menuRelationVo) {
        // 传入的菜单不是顶级，继续查找
        if (!UserConsts.TOP_ID.equals(menuDO.getParentId())) {
            MenuDO menuPa = menuMapper.selectById(menuDO.getParentId());
            MenuRelationVo menuRelationVoPa = new MenuRelationVo();
            menuRelationVoPa.setMenuId(menuPa.getId());
            menuRelationVoPa.setMenuName(menuPa.getName());
            menuRelationVoPa.setChildVo(menuRelationVo);
            return getCircleMenu(menuPa, menuRelationVoPa);
        }
        return menuRelationVo;

    }


    /**
     * 判断一个集合里是否包含该对象
     *
     * @param menuTreeVoList
     * @param menuDO
     * @return
     */
    private boolean exists(List<MenuTreeVo> menuTreeVoList, MenuDO menuDO) {
        boolean exist = false;
        for (MenuTreeVo departmentQuery : menuTreeVoList) {

            if (departmentQuery.getId().equals(menuDO.getId())) {
                exist = true;
            }
        }
        return exist;
    }

    /**
     * 找父节点对应的子节点集合
     *
     * @param menuTreeVoList
     * @param menuListInBase
     */
    private void findChildren(List<MenuTreeVo> menuTreeVoList, List<MenuDO> menuListInBase) {

        menuTreeVoList.stream().forEach(vo -> {
            List<MenuTreeVo> children = new ArrayList<>();
            for (MenuDO department : menuListInBase) {
                if (vo.getId().equals(department.getParentId()) && !exists(children, department)) {
                    MenuTreeVo departmentBO = new MenuTreeVo();
                    BeanUtils.copyProperties(department, departmentBO);
//                    departmentBO.setAuthorityIds(queryAuthoritysByid(departmentBO.getId(), true));
//                    departmentBO.setUnAuthorityIds(queryAuthoritysByid(departmentBO.getId(), false));
                    children.add(departmentBO);
                }
            }
            children.sort(Comparator.comparing(MenuTreeVo::getMenuIndex));
            vo.setChildren(children);
            findChildren(children, menuListInBase);
        });
    }


    /**
     * 根据route查询菜单
     *
     * @param route
     * @return
     */
    public MenuDO queryMenuByUrl(String route) {
        LambdaQueryWrapper<MenuDO> lqwMenu = new LambdaQueryWrapper<>();
        lqwMenu.eq(MenuDO::getState, StateEnum.ON.getCode())
                .eq(MenuDO::getRoute, route)
                .orderByDesc(MenuDO::getId).last("limit 1");
        return menuMapper.selectOne(lqwMenu);
    }

//    /**
//     * 根据菜单id查询拥有或排斥的权限idlist
//     *
//     * @param id
//     * @param has
//     * @return
//     */
//    public List<Integer> queryAuthoritysByid(int id, boolean has) {
//        LambdaQueryWrapper<MenuAuthorityDO> lqwMenuAuthority = new LambdaQueryWrapper<>();
//        lqwMenuAuthority.eq(MenuAuthorityDO::getState, StateEnum.ON.getCode())
//                .eq(MenuAuthorityDO::getMenuId, id)
//                .eq(has, MenuAuthorityDO::getHave, HaveAuthorityEnum.YES.getCode())
//                .eq(!has, MenuAuthorityDO::getHave, HaveAuthorityEnum.NO.getCode());
//        List<MenuAuthorityDO> menuAuthorityList = menuAuthorityService.list(lqwMenuAuthority);
//        return menuAuthorityList.stream().map(r -> r.getAuthorityId()).collect(Collectors.toList());
//    }
}
