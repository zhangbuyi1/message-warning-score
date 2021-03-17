package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.management.dao.RoleMapper;
import com.emdata.messagewarningscore.management.entity.RoleDO;
import com.emdata.messagewarningscore.management.entity.RoleUserDO;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.RoleAuthorityService;
import com.emdata.messagewarningscore.management.service.RoleMenuService;
import com.emdata.messagewarningscore.management.service.RoleService;
import com.emdata.messagewarningscore.management.service.RoleUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author changfeng
 * @description 角色业务实现类
 * @date 2019/12/16
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleDO> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RoleAuthorityService roleAuthorityService;


    @Override
    @Transactional
    public RoleDO addRole(RoleAddParam param) {
        RoleDO role = new RoleDO();
        BeanUtils.copyProperties(param, role);
        int insert = roleMapper.insert(role);
        if (insert != 1) {
            throw new BusinessException(ResultCodeEnum.ROLE_ADD_ERROR.getCode(), ResultCodeEnum.ROLE_ADD_ERROR.getMessage());
        }
        // 绑定菜单
        if (CollectionUtils.isNotEmpty(param.getMenuIds())) {
            RoleMenuAddParam roleMenuAddParam = new RoleMenuAddParam();
            roleMenuAddParam.setRoleId(role.getId());
            roleMenuAddParam.setMenuId(param.getMenuIds());
            roleMenuService.addRoleMenu(roleMenuAddParam);
        }
        // 绑定权限
        if (CollectionUtils.isNotEmpty(param.getAuthorityIds()) || CollectionUtils.isNotEmpty(param.getUnAuthorityIds())){
            RoleAuthorityAddParam roleAuthorityAddParam = new RoleAuthorityAddParam();
            roleAuthorityAddParam.setRoleId(role.getId());
            roleAuthorityAddParam.setAuthorityId(param.getAuthorityIds());
            roleAuthorityAddParam.setUnAuthorityId(param.getUnAuthorityIds());
            roleAuthorityService.addRoleAuthority(roleAuthorityAddParam);
        }
        return role;
    }

    @Override
    @Transactional
    public RoleDO updateRole(RoleUpdateParam param) {
        RoleDO roleInBase = roleMapper.selectById(param.getId());
        if (roleInBase == null) {
            throw new BusinessException(ResultCodeEnum.ROLE_NOT_EXIST.getCode(), ResultCodeEnum.ROLE_NOT_EXIST.getMessage());
        }
        roleInBase.setName(param.getName());
        int updateById = roleMapper.updateById(roleInBase);
        if (updateById != 1) {
            throw new BusinessException(ResultCodeEnum.ROLE_UPDATE_ERROR.getCode(), ResultCodeEnum.ROLE_UPDATE_ERROR.getMessage());
        }
        // 绑定菜单
        RoleMenuAddParam roleMenuAddParam = new RoleMenuAddParam();
        roleMenuAddParam.setRoleId(param.getId());
        roleMenuAddParam.setMenuId(param.getMenuIds());
        roleMenuService.addRoleMenu(roleMenuAddParam);
        // 绑定权限
        RoleAuthorityAddParam roleAuthorityAddParam = new RoleAuthorityAddParam();
        roleAuthorityAddParam.setRoleId(param.getId());
        roleAuthorityAddParam.setAuthorityId(param.getAuthorityIds());
        roleAuthorityAddParam.setUnAuthorityId(param.getUnAuthorityIds());
        roleAuthorityService.addRoleAuthority(roleAuthorityAddParam);
        return roleInBase;
    }

    @Override
    @Transactional
    public void deleteRole(RoleDeleteParam param) {
        RoleDO roleInBase = roleMapper.selectById(param.getId());
        if (roleInBase == null) {
            throw new BusinessException(ResultCodeEnum.ROLE_NOT_EXIST.getCode(), ResultCodeEnum.ROLE_NOT_EXIST.getMessage());
        }
        // 检查是否有用户绑定该角色
        RoleUserQueryPageParam roleUserQueryPageParam = new RoleUserQueryPageParam();
        roleUserQueryPageParam.setRoleId(param.getId());
        List<RoleUserDO> roleUserListInBase = roleUserService.queryRoleUser(roleUserQueryPageParam);
        if (CollectionUtils.isNotEmpty(roleUserListInBase)) {
            throw new BusinessException(ResultCodeEnum.ROLE_DELETE_ERROR_CAUSE_USER.getCode(), ResultCodeEnum.ROLE_DELETE_ERROR_CAUSE_USER.getMessage());
        }
        // 逻辑删除角色
        roleInBase.setState(StateEnum.OFF.getCode());
        int delete = roleMapper.updateById(roleInBase);
        if (delete != 1) {
            throw new BusinessException(ResultCodeEnum.ROLE_DELETE_ERROR.getCode(), ResultCodeEnum.ROLE_DELETE_ERROR.getMessage());
        }
        // 物理删除角色菜单关系
        RoleMenuAddParam roleMenuAddParam = new RoleMenuAddParam();
        roleMenuAddParam.setRoleId(param.getId());
        roleMenuService.addRoleMenu(roleMenuAddParam);
        // 物理删除角色权限关系
        RoleAuthorityAddParam roleAuthorityAddParam = new RoleAuthorityAddParam();
        roleAuthorityAddParam.setRoleId(param.getId());
        roleAuthorityService.addRoleAuthority(roleAuthorityAddParam);
    }

    @Override
    public List<RoleDO> queryRole(RoleQueryPageParam param) {
        LambdaQueryWrapper<RoleDO> lqwRole = new LambdaQueryWrapper<>();
        lqwRole.eq(RoleDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, RoleDO::getId, param.getId())
                .eq(StringUtils.isNoneBlank(param.getName()), RoleDO::getName, param.getName());
        List<RoleDO> roleListInBase = roleMapper.selectList(lqwRole);
        if (CollectionUtils.isEmpty(roleListInBase)) {
            throw new BusinessException(ResultCodeEnum.ROLE_NOT_EXIST.getCode(), ResultCodeEnum.ROLE_NOT_EXIST.getMessage());
        }
        return roleListInBase;
    }

    @Override
    public Page<RoleDO> queryRolePage(RoleQueryPageParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<RoleDO> lqwRole = new LambdaQueryWrapper<>();
        lqwRole.eq(RoleDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, RoleDO::getId, param.getId())
                .likeRight(StringUtils.isNoneBlank(param.getName()), RoleDO::getName, param.getName());
        Page<RoleDO> page = new Page<>(current, size);
        if (StringUtils.isBlank(param.getOrderBy())) {
            param.setOrderBy("created_time");
        }

        if (param.getSort() == Sort.Direction.DESC) {
            page.setDesc(param.getOrderBy());
        } else {
            page.setAsc(param.getOrderBy());
        }
        IPage<RoleDO> roleIPage = roleMapper.selectPage(page, lqwRole);
        return page;
    }
}
