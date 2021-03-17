package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.common.common.constant.UserConsts;
import com.emdata.messagewarningscore.management.dao.AuthorityMapper;
import com.emdata.messagewarningscore.management.entity.AuthorityDO;
import com.emdata.messagewarningscore.management.entity.RoleAuthorityDO;
import com.emdata.messagewarningscore.common.enums.AuthorityLevelEnum;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.AuthorityService;
import com.emdata.messagewarningscore.management.service.RoleAuthorityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author changfeng
 * @description
 * @date 2019/12/4
 */
@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, AuthorityDO> implements AuthorityService {
    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private RoleAuthorityService roleAuthorityService;

    @Override
    public AuthorityDO addAuthority(AuthorityAddParam param) {
        // 根据权限地址值验重
        AuthorityDO authorityInBase = queryAuthorityByUrl(param.getUrl());
        if (authorityInBase != null) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_EXIST.getCode(), ResultCodeEnum.AUTHORITY_EXIST.getMessage());
        }
        AuthorityDO authority = new AuthorityDO();
        BeanUtils.copyProperties(param, authority);
        // 顶级level为0
        if (UserConsts.TOP_ID.equals(param.getParentId())) {
            authority.setLevel(AuthorityLevelEnum.ZERO.getCode());
        } else {
            // 根据父级level+1
            AuthorityDO authorityParent = authorityMapper.selectById(param.getParentId());
            if (authorityParent == null || StateEnum.OFF.getCode().equals(authorityParent.getState())) {
                throw new BusinessException(ResultCodeEnum.PARENT_AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.PARENT_AUTHORITY_NOT_EXIST.getMessage());
            }
            authority.setLevel(authorityParent.getLevel() + 1);
        }

        int insert = authorityMapper.insert(authority);
        if (insert != 1) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_ADD_ERROR.getCode(), ResultCodeEnum.AUTHORITY_ADD_ERROR.getMessage());
        }
        return authority;
    }

    @Override
    @CacheEvict(value = {"authorityList", "unAuthorityList"}, allEntries = true)
    public AuthorityDO updateAuthority(AuthorityUpdateParam param) {
        AuthorityDO authorityInBase = authorityMapper.selectById(param.getId());
        if (authorityInBase == null) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.AUTHORITY_NOT_EXIST.getMessage());
        }
        AuthorityDO authority = new AuthorityDO();
        BeanUtils.copyProperties(param, authority);
        // 顶级level为0
        if (UserConsts.TOP_ID.equals(param.getParentId())) {
            authority.setLevel(AuthorityLevelEnum.ZERO.getCode());
        } else {
            // 根据父级level+1
            AuthorityDO authorityParent = authorityMapper.selectById(param.getParentId());
            if (authorityParent == null || StateEnum.OFF.getCode().equals(authorityParent.getState())) {
                throw new BusinessException(ResultCodeEnum.PARENT_AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.PARENT_AUTHORITY_NOT_EXIST.getMessage());
            }
            authority.setLevel(authorityParent.getLevel() + 1);
        }
        int updateById = authorityMapper.updateById(authority);
        if (updateById != 1) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_UPDATE_ERROR.getCode(), ResultCodeEnum.AUTHORITY_UPDATE_ERROR.getMessage());
        }
        return authority;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"authorityList", "unAuthorityList"}, allEntries = true)
    public void deleteAuthority(AuthorityDeleteParam param) {
        AuthorityDO authorityInBase = authorityMapper.selectById(param.getId());
        if (authorityInBase == null) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.AUTHORITY_NOT_EXIST.getMessage());
        }
        // 如果有下级权限，不允许删除
        LambdaQueryWrapper<AuthorityDO> lqwAuthority = new LambdaQueryWrapper<>();
        lqwAuthority.eq(AuthorityDO::getState, StateEnum.ON.getCode())
                .eq(AuthorityDO::getParentId, authorityInBase.getId());
        List<AuthorityDO> authorityListInBase = authorityMapper.selectList(lqwAuthority);
        if (CollectionUtils.isNotEmpty(authorityListInBase)){
            throw new BusinessException(ResultCodeEnum.AUTHORITY_DELETE_ERROR_CAUSE_CHILD.getCode());
        }
        // 逻辑删除权限
        authorityInBase.setState(StateEnum.OFF.getCode());
        int delete = authorityMapper.updateById(authorityInBase);
        if (delete != 1) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_DELETE_ERROR.getCode(), ResultCodeEnum.AUTHORITY_DELETE_ERROR.getMessage());
        }
        // 物理删除角色权限中所有关联该权限的关系
        LambdaQueryWrapper<RoleAuthorityDO> lqwRoleAuthority = new LambdaQueryWrapper<>();
        lqwRoleAuthority.eq(RoleAuthorityDO::getAuthorityId, param.getId());
        List<RoleAuthorityDO> roleAuthorityListInBase = roleAuthorityService.list(lqwRoleAuthority);
        if (CollectionUtils.isEmpty(roleAuthorityListInBase)){
            return;
        }
        boolean remove = roleAuthorityService.remove(lqwRoleAuthority);
        if (!remove){
            throw new BusinessException(ResultCodeEnum.ROLE_AUTHORITY_DELETE_ERROR.getCode());
        }

//        // 物理删除菜单权限中所有关联该权限的关系
//        LambdaQueryWrapper<MenuAuthorityDO> lqwMenuAuthority = new LambdaQueryWrapper<>();
//        lqwMenuAuthority.eq(MenuAuthorityDO::getState, StateEnum.ON.getCode())
//                .eq(MenuAuthorityDO::getAuthorityId, param.getId());
//        boolean remove = menuAuthorityService.remove(lqwMenuAuthority);
//        if (!remove) {
//            throw new BusinessException(ResultCodeEnum.MENU_AUTHORITY_DELETE_ERROR.getCode(), ResultCodeEnum.MENU_AUTHORITY_DELETE_ERROR.getMessage());
//        }
    }

    @Override
    public List<AuthorityDO> queryAuthority(AuthorityQueryPageParam param) {
        LambdaQueryWrapper<AuthorityDO> lqwAuthority = new LambdaQueryWrapper<>();
        lqwAuthority.eq(AuthorityDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, AuthorityDO::getId, param.getId())
                .eq(StringUtils.isNoneBlank(param.getName()), AuthorityDO::getName, param.getName())
                .eq(StringUtils.isNoneBlank(param.getUrl()), AuthorityDO::getUrl, param.getUrl());
        List<AuthorityDO> authorityListInBase = authorityMapper.selectList(lqwAuthority);
        if (CollectionUtils.isEmpty(authorityListInBase)) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.AUTHORITY_NOT_EXIST.getMessage());
        }
        return authorityListInBase;
    }

    @Override
    public Page<AuthorityDO> queryAuthorityPage(AuthorityQueryPageParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<AuthorityDO> lqwAuthority = new LambdaQueryWrapper<>();
        lqwAuthority.eq(AuthorityDO::getState, StateEnum.ON.getCode())
                .eq(param.getId() != null, AuthorityDO::getId, param.getId())
                .like(StringUtils.isNoneBlank(param.getName()), AuthorityDO::getName, param.getName())
                .eq(StringUtils.isNoneBlank(param.getUrl()), AuthorityDO::getUrl, param.getUrl());
        Page<AuthorityDO> page = new Page<>(current, size);
        if (StringUtils.isBlank(param.getOrderBy())) {
            param.setOrderBy("created_time");
        }
        if (param.getSort() == Sort.Direction.DESC) {
            page.setDesc(param.getOrderBy());
        } else {
            page.setAsc(param.getOrderBy());
        }
        IPage<AuthorityDO> authorityIPage = authorityMapper.selectPage(page, lqwAuthority);
        return page;
    }

    @Override
    public List<AuthorityTreeVo> findTree() {
        List<AuthorityTreeVo> authorityTreeVoList = new ArrayList<>();
        LambdaQueryWrapper<AuthorityDO> lqwAuthority = new LambdaQueryWrapper<>();
        lqwAuthority.eq(AuthorityDO::getState, StateEnum.ON.getCode());
        List<AuthorityDO> authorityListInBase = authorityMapper.selectList(lqwAuthority);

        for (AuthorityDO authority : authorityListInBase) {
            if (authority.getParentId() == 0) {
                AuthorityTreeVo authorityTreeVo = new AuthorityTreeVo();
                BeanUtils.copyProperties(authority, authorityTreeVo);
                if (!exists(authorityTreeVoList, authority)) {
                    authorityTreeVoList.add(authorityTreeVo);
                }
            }
        }
        findChildren(authorityTreeVoList, authorityListInBase);
        return authorityTreeVoList;
    }

    @Override
    public AuthorityRelationVo findAuthoritysByAuthorityId(Integer authorityId) {
        AuthorityDO authorityInBase = authorityMapper.selectById(authorityId);
        if (authorityInBase == null || StateEnum.OFF.getCode().equals(authorityInBase.getState())) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.AUTHORITY_NOT_EXIST.getMessage());
        }
        AuthorityRelationVo authorityRelationVo = new AuthorityRelationVo();
        authorityRelationVo.setAuthorityId(authorityInBase.getId());
        authorityRelationVo.setAuthorityName(authorityInBase.getName());
        return getCircleAuthority(authorityInBase, authorityRelationVo);
    }

    @Override
    public List<Integer> findListByAuthorityId(Integer authorityId) {
        List<Integer> parents = new ArrayList<>();
        AuthorityDO authorityInBase = authorityMapper.selectById(authorityId);
        if (authorityInBase == null || StateEnum.OFF.getCode().equals(authorityInBase.getState())) {
            throw new BusinessException(ResultCodeEnum.AUTHORITY_NOT_EXIST.getCode(), ResultCodeEnum.AUTHORITY_NOT_EXIST.getMessage());
        }
        parents = getParentsAuthority(authorityInBase, parents);
        Collections.reverse(parents);
        return parents;
    }

    private List<Integer> getParentsAuthority(AuthorityDO authorityDO, List<Integer> list){
        list.add(authorityDO.getId());
        if (UserConsts.TOP_ID.equals(authorityDO.getParentId())){
            return list;
        }
        AuthorityDO authorityPa = authorityMapper.selectById(authorityDO.getParentId());
        return getParentsAuthority(authorityPa, list);
    }

    /**
     * 获取权限关系遍历方法
     *
     * @param authorityDO
     * @param authorityRelationVo
     * @return
     */
    private AuthorityRelationVo getCircleAuthority(AuthorityDO authorityDO, AuthorityRelationVo authorityRelationVo) {
        // 传入的权限不是顶级，继续查找
        if (!UserConsts.TOP_ID.equals(authorityDO.getParentId())) {
            AuthorityDO authorityPa = authorityMapper.selectById(authorityDO.getParentId());
            AuthorityRelationVo authorityRelationVoPa = new AuthorityRelationVo();
            authorityRelationVoPa.setAuthorityId(authorityPa.getId());
            authorityRelationVoPa.setAuthorityName(authorityPa.getName());
            authorityRelationVoPa.setChildVo(authorityRelationVo);
            return getCircleAuthority(authorityPa, authorityRelationVoPa);
        }
        return authorityRelationVo;

    }

    /**
     * 判断一个集合里是否包含该对象
     *
     * @param authorityTreeVoList
     * @param authorityDO
     * @return
     */
    private boolean exists(List<AuthorityTreeVo> authorityTreeVoList, AuthorityDO authorityDO) {
        boolean exist = false;
        for (AuthorityTreeVo departmentQuery : authorityTreeVoList) {

            if (departmentQuery.getId().equals(authorityDO.getId()) ) {
                exist = true;
            }
        }
        return exist;
    }
    /**
     * 找父节点对应的子节点集合
     *
     * @param authorityTreeVoList
     * @param authorityListInBase
     */
    private void findChildren(List<AuthorityTreeVo> authorityTreeVoList, List<AuthorityDO> authorityListInBase) {

        authorityTreeVoList.stream().forEach(vo -> {
            List<AuthorityTreeVo> children = new ArrayList<>();
            for (AuthorityDO department : authorityListInBase) {
                if (vo.getId().equals(department.getParentId()) && !exists(children, department)) {
                    AuthorityTreeVo departmentBO = new AuthorityTreeVo();
                    BeanUtils.copyProperties(department, departmentBO);
                    children.add(departmentBO);
                }
            }
            vo.setChildren(children);
            findChildren(children, authorityListInBase);
        });
    }


    /**
     * 根据url查询权限
     *
     * @param url
     * @return
     */
    public AuthorityDO queryAuthorityByUrl(String url) {
        LambdaQueryWrapper<AuthorityDO> lqwAuthority = new LambdaQueryWrapper<>();
        lqwAuthority.eq(AuthorityDO::getState, StateEnum.ON.getCode())
                .eq(AuthorityDO::getUrl, url)
                .orderByDesc(AuthorityDO::getId).last("limit 1");
        return authorityMapper.selectOne(lqwAuthority);
    }
}
