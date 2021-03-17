package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.*;
import com.emdata.messagewarningscore.management.entity.AuthorityDO;

import java.util.List;

/**
 * @author changfeng
 * @description
 * @date 2019/12/4
 */
public interface AuthorityService extends IService<AuthorityDO> {
    /**
     * 新增权限
     * @param param
     * @return
     */
    AuthorityDO addAuthority(AuthorityAddParam param);

    /**
     * 修改权限信息
     * @param param
     * @return
     */
    AuthorityDO updateAuthority(AuthorityUpdateParam param);

    /**
     * 删除权限
     * @param param
     */
    void deleteAuthority(AuthorityDeleteParam param);

    /**
     * 查询权限
     * @param param
     */
    List<AuthorityDO> queryAuthority(AuthorityQueryPageParam param);

    /**
     * 分页查询
     * @param param
     * @return
     */
    Page<AuthorityDO> queryAuthorityPage(AuthorityQueryPageParam param);

    List<AuthorityTreeVo> findTree();

    AuthorityRelationVo findAuthoritysByAuthorityId(Integer authorityId);

    List<Integer> findListByAuthorityId(Integer authorityId);
}
