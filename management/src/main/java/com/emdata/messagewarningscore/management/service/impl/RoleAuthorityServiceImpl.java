package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.RoleAuthorityAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleAuthorityQueryPageParam;
import com.emdata.messagewarningscore.management.dao.RoleAuthorityMapper;
import com.emdata.messagewarningscore.management.entity.RoleAuthorityDO;
import com.emdata.messagewarningscore.management.entity.RoleDO;
import com.emdata.messagewarningscore.common.enums.HaveAuthorityEnum;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.AuthorityService;
import com.emdata.messagewarningscore.management.service.RoleAuthorityService;
import com.emdata.messagewarningscore.management.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author changfeng
 * @description
 * @date 2019/12/4
 */
@Service
public class RoleAuthorityServiceImpl extends ServiceImpl<RoleAuthorityMapper, RoleAuthorityDO> implements RoleAuthorityService {
    @Autowired
    private RoleAuthorityMapper roleAuthorityMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthorityService authorityService;

    @Override
    @Transactional
    @CacheEvict(value = "authorityList", allEntries = true)
    public void addRoleAuthority(RoleAuthorityAddParam param) {
        RoleDO roleInBase = roleService.getById(param.getRoleId());
        if (roleInBase == null) {
            throw new BusinessException(ResultCodeEnum.ROLE_NOT_EXIST.getCode(), ResultCodeEnum.ROLE_NOT_EXIST.getMessage());
        }
        // 物理删除老的关系
        LambdaQueryWrapper<RoleAuthorityDO> lqwRoleAuthority = new LambdaQueryWrapper<>();
        lqwRoleAuthority.eq(RoleAuthorityDO::getRoleId, param.getRoleId());
        roleAuthorityMapper.delete(lqwRoleAuthority);
        // 插入新的关系
        if (CollectionUtils.isNotEmpty(param.getAuthorityId())) {
            List<RoleAuthorityDO> roleAuthorityList = new ArrayList<>();
            for (Integer authorityId : param.getAuthorityId()) {
                RoleAuthorityDO roleAuthority = new RoleAuthorityDO();
                roleAuthority.setRoleId(param.getRoleId());
                roleAuthority.setAuthorityId(authorityId);
                roleAuthority.setHave(HaveAuthorityEnum.YES.getCode());
                roleAuthorityList.add(roleAuthority);
            }
            // 批量插入
            boolean saveBatch = saveBatch(roleAuthorityList, 500);
            if (!saveBatch) {
                throw new BusinessException(ResultCodeEnum.ROLE_AUTHORITY_ADD_ERROR.getCode(), ResultCodeEnum.ROLE_AUTHORITY_ADD_ERROR.getMessage());
            }
        }
        // 插入新的黑名单
        if (CollectionUtils.isNotEmpty(param.getUnAuthorityId())){
            List<RoleAuthorityDO> roleAuthorityList = new ArrayList<>();
            for (Integer unAuthorityId : param.getUnAuthorityId()) {
                RoleAuthorityDO roleAuthority = new RoleAuthorityDO();
                roleAuthority.setRoleId(param.getRoleId());
                roleAuthority.setAuthorityId(unAuthorityId);
                roleAuthority.setHave(HaveAuthorityEnum.NO.getCode());
                roleAuthorityList.add(roleAuthority);
            }
            // 批量插入
            boolean saveBatch = saveBatch(roleAuthorityList, 500);
            if (!saveBatch) {
                throw new BusinessException(ResultCodeEnum.ROLE_AUTHORITY_ADD_ERROR.getCode(), ResultCodeEnum.ROLE_AUTHORITY_ADD_ERROR.getMessage());
            }
        }

        // 作废不要的权限
//        List<RoleAuthorityDO> roleAuthorityNewList = new ArrayList<>();
//        if (CollectionUtils.isEmpty(param.getUnAuthorityId())){
//            return;
//        }
//        for (Integer unAuthority:param.getUnAuthorityId()) {
//            LambdaQueryWrapper<RoleAuthorityDO> lqwRoleAuthorityNew = new LambdaQueryWrapper<>();
//            lqwRoleAuthorityNew.eq(RoleAuthorityDO::getState, StateEnum.ON.getCode())
//                    .eq(RoleAuthorityDO::getRoleId, param.getRoleId())
//                    .eq(RoleAuthorityDO::getAuthorityId, unAuthority);
//            RoleAuthorityDO roleAuthorityInBase = roleAuthorityMapper.selectOne(lqwRoleAuthorityNew);
//            if (roleAuthorityInBase != null){
//                roleAuthorityNewList.add(roleAuthorityInBase);
//            }
//        }
//        if (CollectionUtils.isEmpty(roleAuthorityNewList)) {
//            return;
//        }
//        roleAuthorityNewList.forEach(a -> a.setHave(HaveAuthorityEnum.NO.getCode()));
//        boolean updateBatchById = updateBatchById(roleAuthorityNewList, 500);
//        if (!updateBatchById) {
//            throw new BusinessException(ResultCodeEnum.ROLE_AUTHORITY_ADD_ERROR.getCode(), ResultCodeEnum.ROLE_AUTHORITY_ADD_ERROR.getMessage());
//        }
    }

    @Override
    public List<RoleAuthorityDO> queryRoleAuthority(RoleAuthorityQueryPageParam param) {
        LambdaQueryWrapper<RoleAuthorityDO> lqwRoleAuthority = new LambdaQueryWrapper<>();
        lqwRoleAuthority.eq(RoleAuthorityDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, RoleAuthorityDO::getId, param.getId())
                .eq(param.getRoleId() != null, RoleAuthorityDO::getRoleId, param.getRoleId())
                .eq(param.getAuthorityId() != null, RoleAuthorityDO::getAuthorityId, param.getAuthorityId())
                .eq(param.getHave() != null, RoleAuthorityDO::getHave, param.getHave());
        return roleAuthorityMapper.selectList(lqwRoleAuthority);
        /**
         * 查询为空即返回空，不报异常
         */
//        if (CollectionUtils.isEmpty(roleAuthorityListInBase)) {
//            throw new BusinessException(ResultCodeEnum.ROLE_AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.USER_EXIST.getMessage());
//        }
    }

    @Override
    public Page<RoleAuthorityDO> queryRoleAuthorityPage(RoleAuthorityQueryPageParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<RoleAuthorityDO> lqwRoleAuthority = new LambdaQueryWrapper<>();
        lqwRoleAuthority.eq(RoleAuthorityDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, RoleAuthorityDO::getId, param.getId())
                .eq(param.getRoleId() != null, RoleAuthorityDO::getRoleId, param.getRoleId())
                .eq(param.getAuthorityId() != null, RoleAuthorityDO::getAuthorityId, param.getAuthorityId())
                .eq(param.getHave() != null, RoleAuthorityDO::getHave, param.getHave());
        Page<RoleAuthorityDO> page = new Page<>(current, size);
        IPage<RoleAuthorityDO> roleAuthorityIPage = roleAuthorityMapper.selectPage(page, lqwRoleAuthority);
        List<RoleAuthorityDO> roleAuthorityList = roleAuthorityIPage.getRecords();
        Page<RoleAuthorityDO> pages = new Page<>(current, size);
        pages.setRecords(roleAuthorityList);
        pages.setTotal(roleAuthorityIPage.getTotal());
        pages.setPages(roleAuthorityIPage.getPages());
        return pages;
    }
}
