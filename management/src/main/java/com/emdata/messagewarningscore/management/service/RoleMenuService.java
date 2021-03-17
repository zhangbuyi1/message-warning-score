package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuAddParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuVo;
import com.emdata.messagewarningscore.management.entity.RoleMenuDO;

import java.util.List;

/**
 * @author changfeng
 * @description 
 * @date 2019/12/4
 */
public interface RoleMenuService extends IService<RoleMenuDO> {


    /**
     * 新增角色菜单关系
     * @param param
     * @return
     */
    void addRoleMenu(RoleMenuAddParam param);

    List<RoleMenuVo> queryRoleMenu(RoleMenuQueryPageParam param);
}
