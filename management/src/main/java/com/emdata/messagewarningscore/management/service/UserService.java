package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.management.entity.AuthorityDO;
import com.emdata.messagewarningscore.management.entity.MenuDO;
import com.emdata.messagewarningscore.management.entity.UserDO;

import java.util.List;

/**
 * @author changfeng
 * @description 用户业务类
 * @date 2019/12/13
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据userId查询权限list
     * @param userId
     * @return
     */
    List<AuthorityDO> queryAuthorityListByUserId(Integer userId);

    /**
     * 根据userId查询排斥权限list
     * @param userId
     * @return
     */
    List<AuthorityDO> queryUnauthorityListByUserId(Integer userId);

    /**
     * 用户添加
     * @param param
     */
    UserDO addUser(UserAddParam param);

    /**
     * 修改用户信息
     * @param param
     * @return
     */
    UserDO updateUser(UserUpdateParam param);

    /**
     * 删除用户
     * @param param
     */
    void deleteUser(UserDeleteParam param);

    /**
     * 查询用户
     * @param param
     * @return
     */
    List<UserDO> queryUser(UserQueryPageParam param);

    /**
     * 分页查询
     * @param param
     * @return
     */
    Page<UserDO> queryUserPage(UserQueryPageParam param);

    /**
     * 查询用户 带角色
     * @param param
     * @return
     */
    Page<UserVO> queryUserWithRole(UserQueryPageParam param);

    /**
     * 根据userId查询菜单list
     * @param userId
     * @return
     */
    List<MenuDO> queryMenuListByUserId(Integer userId);
}
