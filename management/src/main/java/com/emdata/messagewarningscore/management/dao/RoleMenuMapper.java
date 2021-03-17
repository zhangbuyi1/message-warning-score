package com.emdata.messagewarningscore.management.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emdata.messagewarningscore.management.controller.vo.RoleMenuVo;
import com.emdata.messagewarningscore.management.entity.RoleMenuDO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author changfeng
 * @description 
 * @date 2019/12/4
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenuDO> {

    @Select("select rm.*,IF(exists(select 0 from menu me where me.state = 1 and me.parent_id = m.id),1,0) father from role_menu rm, menu m where " +
            "m.state =1 and rm.menu_id =m.id and rm.role_id = #{roleId}")
    List<RoleMenuVo> queryMenuListByRoleId(Integer roleId);
}
