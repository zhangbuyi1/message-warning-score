package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.RoleUserAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleUserQueryPageParam;
import com.emdata.messagewarningscore.management.entity.RoleUserDO;

import java.util.List;

/**
 * @author changfeng
 * @description 角色用户业务类
 * @date 2019/12/4
 */
public interface RoleUserService extends IService<RoleUserDO> {

    /**
     * 新增角色用户关系
     * @param param
     */
    void addRoleUser(RoleUserAddParam param);

    /**
     * 查询
     * @param param
     * @return
     */
    List<RoleUserDO> queryRoleUser(RoleUserQueryPageParam param);

    /**
     * 分页查询
     * @param param
     * @return
     */
    Page<RoleUserDO> queryRoleUserPage(RoleUserQueryPageParam param);
}
