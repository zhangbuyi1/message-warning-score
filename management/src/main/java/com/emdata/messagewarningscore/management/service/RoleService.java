package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.RoleAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleDeleteParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleUpdateParam;
import com.emdata.messagewarningscore.management.entity.RoleDO;

import java.util.List;

/**
 * @author changfeng
 * @description 角色业务类
 * @date 2019/12/16
 */
public interface RoleService extends IService<RoleDO> {

    /**
     * 新增角色
     * @param param
     * @return
     */
    RoleDO addRole(RoleAddParam param);

    /**
     * 更新角色信息
     * @param param
     * @return
     */
    RoleDO updateRole(RoleUpdateParam param);

    /**
     * 删除角色
     * @param param
     */
    void deleteRole(RoleDeleteParam param);

    /**
     * 查询角色
     * @param param
     * @return
     */
    List<RoleDO> queryRole(RoleQueryPageParam param);

    /**
     * 分页查询角色
     * @param param
     * @return
     */
    Page<RoleDO> queryRolePage(RoleQueryPageParam param);
}
