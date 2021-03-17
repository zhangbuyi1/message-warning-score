package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.common.utils.Guid;
import com.emdata.messagewarningscore.common.common.utils.password.PasswordUtils;
import com.emdata.messagewarningscore.common.dao.UserAirportMapper;
import com.emdata.messagewarningscore.common.dao.entity.AirportDO;
import com.emdata.messagewarningscore.common.dao.entity.UserAirportDO;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.common.service.IAirportService;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.management.dao.UserMapper;
import com.emdata.messagewarningscore.management.entity.AuthorityDO;
import com.emdata.messagewarningscore.management.entity.MenuDO;
import com.emdata.messagewarningscore.management.entity.RoleDO;
import com.emdata.messagewarningscore.management.entity.UserDO;
import com.emdata.messagewarningscore.management.service.RoleUserService;
import com.emdata.messagewarningscore.management.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunming on 2019/8/5.
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private IAirportService iAirportService;

    @Autowired
    private UserAirportMapper userAirportMapper;


    @Cacheable(value = "authorityList", unless = "#result.size() == 0")
    @Override
    public List<AuthorityDO> queryAuthorityListByUserId(Integer userId) {
        return userMapper.queryAuthorityListByUserId(userId);
    }

    @Cacheable(value = "unAuthorityList", unless = "#result.size() == 0")
    @Override
    public List<AuthorityDO> queryUnauthorityListByUserId(Integer userId) {
        return userMapper.queryUnauthorityListByUserId(userId);
    }

    @Override
    @Transactional
    public UserDO addUser(UserAddParam param) {
        UserDO userInBase = queryUserByName(param.getName());
        if (userInBase != null) {
            throw new BusinessException(ResultCodeEnum.USER_EXIST.getCode(), ResultCodeEnum.USER_EXIST.getMessage());
        }
        UserDO user = new UserDO();
        BeanUtils.copyProperties(param, user);
        // 密码加密
        String salt = Guid.newGUID();
        String encPassword = PasswordUtils.encode(param.getPassword(), salt);
        user.setPassword(encPassword);
        user.setSalt(salt);
        int insert = userMapper.insert(user);
        if (insert != 1) {
            throw new BusinessException(ResultCodeEnum.USER_ADD_ERROR.getCode(), ResultCodeEnum.USER_ADD_ERROR.getMessage());
        }
        // 根据机场代码查询机场信息
        AirportDO airportDO = iAirportService.findByAirportCode(param.getAirportCode());
        String airportUuid = "";
        if (airportDO != null) {
            airportUuid = airportDO.getUuid();
        }
        UserAirportDO userAirportDO = new UserAirportDO();
        userAirportDO.setUserId(user.getId());
        userAirportDO.setAirportCode(param.getAirportCode());
        userAirportDO.setAirportUuid(airportUuid);
        userAirportMapper.insert(userAirportDO);

        RoleUserAddParam roleUserAddParam = new RoleUserAddParam();
        roleUserAddParam.setUserId(user.getId());
        roleUserAddParam.setRoleIds(param.getRoleIds());
        roleUserService.addRoleUser(roleUserAddParam);
        return user;
    }

    @Override
    @Transactional
    public UserDO updateUser(UserUpdateParam param) {
        UserDO userInBase = userMapper.selectById(param.getId());
        if (userInBase == null) {
            throw new BusinessException(ResultCodeEnum.USER_EMPTY.getCode(), ResultCodeEnum.USER_EMPTY.getMessage());
        }
        UserDO user = new UserDO();
        BeanUtils.copyProperties(param, user);
        // 要修改密码
        if (StringUtils.isNotBlank(param.getPassword())) {
            // 密码加密
            String salt = Guid.newGUID();
            String encPassword = PasswordUtils.encode(param.getPassword(), salt);
            user.setPassword(encPassword);
            user.setSalt(salt);
        }
        int updateById = userMapper.updateById(user);
        if (updateById != 1) {
            throw new BusinessException(ResultCodeEnum.USER_UPDATE_ERROR.getCode(), ResultCodeEnum.USER_UPDATE_ERROR.getMessage());
        }
        RoleUserAddParam roleUserAddParam = new RoleUserAddParam();
        roleUserAddParam.setUserId(userInBase.getId());
        roleUserAddParam.setRoleIds(param.getRoleIds());
        roleUserService.addRoleUser(roleUserAddParam);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(UserDeleteParam param) {
        UserDO userInBase = userMapper.selectById(param.getId());
        if (userInBase == null) {
            throw new BusinessException(ResultCodeEnum.USER_EMPTY.getCode(), ResultCodeEnum.USER_EMPTY.getMessage());
        }
        userInBase.setState(StateEnum.OFF.getCode());
        int delete = userMapper.updateById(userInBase);
        if (delete != 1) {
            throw new BusinessException(ResultCodeEnum.USER_DELETE_ERROR.getCode(), ResultCodeEnum.USER_DELETE_ERROR.getMessage());
        }
        // 物理删除角色用户关系
        RoleUserAddParam roleUserAddParam = new RoleUserAddParam();
        roleUserAddParam.setUserId(param.getId());
        roleUserService.addRoleUser(roleUserAddParam);
    }


    @Override
    public List<UserDO> queryUser(UserQueryPageParam param) {
        List<UserDO> userList = new ArrayList<>();
        // 返回单个用户
        if (StringUtils.isNoneBlank(param.getName())) {
            UserDO userInBase = queryUserByName(param.getName());
            if (userInBase == null) {
                throw new BusinessException(ResultCodeEnum.USER_EMPTY.getCode(), ResultCodeEnum.USER_EMPTY.getMessage());
            }
            userList.add(userInBase);
            return userList;
        }
        // 返回所有用户
        LambdaQueryWrapper<UserDO> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.eq(UserDO::getState, StateEnum.ON.getCode());
        List<UserDO> userListInBase = userMapper.selectList(lqwUser);
        if (CollectionUtils.isEmpty(userListInBase)) {
            throw new BusinessException(ResultCodeEnum.USER_EMPTY.getCode(), ResultCodeEnum.USER_EMPTY.getMessage());
        }
        userList.addAll(userListInBase);
        return userList;
    }

    @Override
    public Page<UserDO> queryUserPage(UserQueryPageParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<UserDO> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.eq(UserDO::getState, StateEnum.ON.getCode())
                .likeRight(StringUtils.isNoneBlank(param.getName()), UserDO::getName, param.getName())
                .likeRight(StringUtils.isNoneBlank(param.getRealName()), UserDO::getRealName, param.getRealName());
        Page<UserDO> page = new Page<>(current, size);

//        IPage<UserDO> userIPage = userMapper.selectPage(page, lqwUser);
//        List<UserDO> userList = userIPage.getRecords();
//        Page<UserDO> pages = new Page<>(current, size);
        if (StringUtils.isBlank(param.getOrderBy())) {
            param.setOrderBy("created_time");
        }

        if (param.getSort() == Sort.Direction.DESC) {
            page.setDesc(param.getOrderBy());
        } else {
            page.setAsc(param.getOrderBy());
        }
        userMapper.selectPage(page, lqwUser);
//        pages.setRecords(userList);
//        pages.setTotal(userIPage.getTotal());
//        pages.setPages(userIPage.getPages());
        return page;
    }

    @Override
    public Page<UserVO> queryUserWithRole(UserQueryPageParam param) {
        Page<UserDO> usersPage = queryUserPage(param);
        List<UserDO> users = usersPage.getRecords();
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        List<UserVO> voList = new ArrayList<>();
        for (UserDO user : users) {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            List<RoleDO> roleListInBase = queryRoleListByUserId(user.getId());
            if (CollectionUtils.isEmpty(roleListInBase)) {
                voList.add(vo);
                continue;
            }
            String roleStr = "";
            for (int i = 0; i < roleListInBase.size(); i++) {
                if (i == roleListInBase.size() - 1) {
                    roleStr = roleStr + roleListInBase.get(i).getName();
                } else {
                    roleStr = roleStr + roleListInBase.get(i).getName() + ",";
                }
            }
            vo.setRoleStr(roleStr);
            voList.add(vo);
        }
        Page<UserVO> pages = new Page<>(usersPage.getCurrent(), usersPage.getSize());
        pages.setRecords(voList);
        pages.setTotal(usersPage.getTotal());
        pages.setPages(usersPage.getPages());
        return pages;
    }

    @Override
    public List<MenuDO> queryMenuListByUserId(Integer userId) {
        return userMapper.queryMenuListByUserId(userId);
    }

    /**
     * 根据userid查角色集合
     *
     * @param userId
     * @return
     */
    public List<RoleDO> queryRoleListByUserId(Integer userId) {
        return userMapper.queryRoleListByUserId(userId);
    }

    /**
     * 根据name查询用户信息
     *
     * @return
     */
    public UserDO queryUserByName(String name) {
        LambdaQueryWrapper<UserDO> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.eq(UserDO::getName, name)
                .eq(UserDO::getState, StateEnum.ON.getCode())
                .orderByDesc(UserDO::getId).last("limit 1");
        return userMapper.selectOne(lqwUser);
    }
}
