package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.RoleAuthorityAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleAuthorityQueryPageParam;
import com.emdata.messagewarningscore.management.entity.RoleAuthorityDO;

import java.util.List;

/**
 * @author changfeng
 * @description 
 * @date 2019/12/4
 */
public interface RoleAuthorityService extends IService<RoleAuthorityDO> {

    /**
     * 新增角色权限关系
     * @param param
     * @return
     */
    void addRoleAuthority(RoleAuthorityAddParam param);

    /**
     * 查询角色权限关系
     * @param param
     * @return
     */
    List<RoleAuthorityDO> queryRoleAuthority(RoleAuthorityQueryPageParam param);

    /**
     * 分页查询角色权限关系
     * @param param
     * @return
     */
    Page<RoleAuthorityDO> queryRoleAuthorityPage(RoleAuthorityQueryPageParam param);
}
