package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.RoleUserAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleUserQueryPageParam;
import com.emdata.messagewarningscore.management.dao.RoleUserMapper;
import com.emdata.messagewarningscore.management.entity.RoleUserDO;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.RoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author changfeng
 * @description 角色用户业务实现类
 * @date 2019/12/4
 */
@Service
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, RoleUserDO> implements RoleUserService {
    @Autowired
    private RoleUserMapper roleUserMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"authorityList","unAuthorityList"}, allEntries = true)
    public void addRoleUser(RoleUserAddParam param) {
        // 一个用户可以有多个角色
        LambdaQueryWrapper<RoleUserDO> lqwRoleUser = new LambdaQueryWrapper<>();
        lqwRoleUser.eq(RoleUserDO::getState, StateEnum.ON.getCode())
                .eq(RoleUserDO::getUserId, param.getUserId());
        List<RoleUserDO> roleUserListInBase = roleUserMapper.selectList(lqwRoleUser);
        // 先物理删除用户角色关系
        if (CollectionUtils.isNotEmpty(roleUserListInBase)) {
            List<Integer> ids = roleUserListInBase.stream().map(r -> r.getId()).collect(Collectors.toList());
            int deleteBatchIds = roleUserMapper.deleteBatchIds(ids);
            if (deleteBatchIds < 1) {
                throw new BusinessException(ResultCodeEnum.ROLE_USER_DELETE_ERROR.getCode(), ResultCodeEnum.ROLE_USER_DELETE_ERROR.getMessage());
            }
        }
        // 判断是否删除操作
        if (CollectionUtils.isEmpty(param.getRoleIds())) {
            return;
        }
        // 新增或修改，插入
        ArrayList<RoleUserDO> roleUserList = new ArrayList<>();
        for (Integer roleId : param.getRoleIds()) {
            RoleUserDO roleUser = new RoleUserDO();
            roleUser.setUserId(param.getUserId());
            roleUser.setRoleId(roleId);
            roleUserList.add(roleUser);
        }
        boolean saveBatch = saveBatch(roleUserList);
        if (!saveBatch) {
            throw new BusinessException(ResultCodeEnum.ROLE_USER_ADD_ERROR.getCode(), ResultCodeEnum.ROLE_USER_ADD_ERROR.getMessage());
        }
    }

    @Override
    public List<RoleUserDO> queryRoleUser(RoleUserQueryPageParam param) {
        LambdaQueryWrapper<RoleUserDO> lqwRoleUser = new LambdaQueryWrapper<>();
        lqwRoleUser.eq(RoleUserDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, RoleUserDO::getId, param.getId())
                .eq(param.getRoleId() != null, RoleUserDO::getRoleId, param.getRoleId())
                .eq(param.getUserId() != null, RoleUserDO::getUserId, param.getUserId());
        return roleUserMapper.selectList(lqwRoleUser);
        /**
         * 查询为空即返回空，不报异常
         */
//        if (CollectionUtils.isEmpty(roleUserListInBase)) {
//            throw new BusinessException(ResultCodeEnum.ROLE_USER_NOT_EXIST.getCode(), ResultCodeEnum.USER_EXIST.getMessage());
//        }
    }

    @Override
    public Page<RoleUserDO> queryRoleUserPage(RoleUserQueryPageParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<RoleUserDO> lqwRoleUser = new LambdaQueryWrapper<>();
        lqwRoleUser.eq(RoleUserDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, RoleUserDO::getId, param.getId())
                .eq(param.getRoleId() != null, RoleUserDO::getRoleId, param.getRoleId())
                .eq(param.getUserId() != null, RoleUserDO::getUserId, param.getUserId());
        Page<RoleUserDO> page = new Page<>(current, size);
        IPage<RoleUserDO> roleUserIPage = roleUserMapper.selectPage(page, lqwRoleUser);
        List<RoleUserDO> roleUserList = roleUserIPage.getRecords();
        Page<RoleUserDO> pages = new Page<>(current, size);
        pages.setRecords(roleUserList);
        pages.setTotal(roleUserIPage.getTotal());
        pages.setPages(roleUserIPage.getPages());
        return pages;
    }
}
