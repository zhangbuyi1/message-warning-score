package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuVo;
import com.emdata.messagewarningscore.management.dao.RoleMenuMapper;
import com.emdata.messagewarningscore.management.entity.RoleDO;
import com.emdata.messagewarningscore.management.entity.RoleMenuDO;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.RoleMenuService;
import com.emdata.messagewarningscore.management.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author changfeng
 * @description
 * @date 2020/2/21
 */
@Service
@Slf4j
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenuDO> implements RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional
    public void addRoleMenu(RoleMenuAddParam param) {
        RoleDO roleInBase = roleService.getById(param.getRoleId());
        if (roleInBase == null) {
            throw new BusinessException(ResultCodeEnum.ROLE_NOT_EXIST.getCode(), ResultCodeEnum.ROLE_NOT_EXIST.getMessage());
        }
        // 物理删除老的关系
        LambdaQueryWrapper<RoleMenuDO> lqwRoleMenu = new LambdaQueryWrapper<>();
        lqwRoleMenu.eq(RoleMenuDO::getRoleId, param.getRoleId());
        roleMenuMapper.delete(lqwRoleMenu);

        // 如果是删除直接返回
        if (CollectionUtils.isEmpty(param.getMenuId())) {
            return;
        }
        // 插入新的关系
        List<RoleMenuDO> roleMenuList = new ArrayList<>();
        for (Integer menuId : param.getMenuId()) {
            RoleMenuDO roleMenu = new RoleMenuDO();
            roleMenu.setRoleId(param.getRoleId());
            roleMenu.setMenuId(menuId);
            roleMenuList.add(roleMenu);
        }
        // 批量插入
        boolean saveBatch = saveBatch(roleMenuList, 500);
        if (!saveBatch) {
            throw new BusinessException(ResultCodeEnum.ROLE_MENU_ADD_ERROR.getCode(), ResultCodeEnum.ROLE_MENU_ADD_ERROR.getMessage());
        }
    }

    @Override
    public List<RoleMenuVo> queryRoleMenu(RoleMenuQueryPageParam param) {
//        List<RoleMenuVo> roleMenuVos = roleMenuMapper.queryMenuListByRoleId(param.getRoleId());
//        LambdaQueryWrapper<RoleMenuDO> lqwRoleMenu = new LambdaQueryWrapper<>();
//        lqwRoleMenu.eq(RoleMenuDO::getState, StateEnum.ON.getCode())
//                .eq(param.getId() != null, RoleMenuDO::getId, param.getId())
//                .eq(param.getRoleId() != null, RoleMenuDO::getRoleId, param.getRoleId())
//                .eq(param.getMenuId() != null, RoleMenuDO::getMenuId, param.getMenuId());
        return roleMenuMapper.queryMenuListByRoleId(param.getRoleId());
    }
}
