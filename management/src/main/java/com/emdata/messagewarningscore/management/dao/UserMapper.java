package com.emdata.messagewarningscore.management.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emdata.messagewarningscore.management.entity.AuthorityDO;
import com.emdata.messagewarningscore.management.entity.MenuDO;
import com.emdata.messagewarningscore.management.entity.RoleDO;
import com.emdata.messagewarningscore.management.entity.UserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author changfeng
 * @description
 * @date 2019/12/13
 */
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 查询用户的权限集合
     * @return
     */
    @Select("select a.* from authority a, role_authority ra, role_user ru\n" +
            "            where a.state = 1 \n" +
            "            and ra.have=1 \n" +
            "            and a.id = ra.authority_id and ra.role_id = ru.role_id and ru.user_id = #{userId}")
//    @Select("select a.* from authority a,role_authority ra,role_user ru where a.state = 1 and ra.have = 1 and ra.state = 1 and ru.state = 1 and a.id = ra.authority_id and ra.role_id = ru.role_id and ru.user_id = #{userId}")
    List<AuthorityDO> queryAuthorityListByUserId(@Param("userId") int userId);

    /**
     * 查询用户的排斥权限集合
     * @return
     */
    @Select("select a.* from authority a, role_authority ra, role_user ru\n" +
            "            where a.state = 1 \n" +
            "            and ra.have=0 \n" +
            "            and a.id = ra.authority_id and ra.role_id = ru.role_id and ru.user_id = #{userId}")
//    @Select("select a.* from authority a,role_authority ra,role_user ru where a.state = 1 and ra.have = 1 and ra.state = 1 and ru.state = 1 and a.id = ra.authority_id and ra.role_id = ru.role_id and ru.user_id = #{userId}")
    List<AuthorityDO> queryUnauthorityListByUserId(@Param("userId") int userId);

    /**
     * 查询用户的角色集合
     * @return
     */
    @Select("select r.* from role r,role_user ru where r.state = 1 and ru.state = 1 and ru.role_id = r.id and ru.user_id = #{userId}")
    List<RoleDO> queryRoleListByUserId(Integer userId);


    @Select("select m.* from menu m, role_menu rm, role_user ru \n" +
            "where m.state = 1 and rm.state = 1 and ru.state=1\n" +
            "and m.id = rm.menu_id and rm.role_id = ru.role_id and ru.user_id = #{userId}")
    List<MenuDO> queryMenuListByUserId(Integer userId);
}
