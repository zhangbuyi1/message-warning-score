package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.MailConfigPageVO;
import com.emdata.messagewarningscore.management.controller.vo.MailConfigVO;
import com.emdata.messagewarningscore.management.entity.MailConfig;
import com.emdata.messagewarningscore.common.result.ResultData;

/**
 * @author: sunming
 * @date: 2020/1/8
 * @description:
 */
public interface MailConfigService extends IService<MailConfig> {


    /**
     * 新增邮箱配置信息
     *
     * @param mailConfigVO
     * @return
     */
    ResultData<MailConfig> createMailConfig(MailConfigVO mailConfigVO);

    /**
     * 修改邮箱配置信息
     *
     * @param mailConfigVO
     * @return
     */
    ResultData<MailConfig> updateMailConfig(MailConfigVO mailConfigVO);


    /**
     * 删除邮箱配置信息
     *
     * @param id
     * @return
     */
    ResultData<MailConfig> deleteMailConfig(Integer id);

    /**
     * 分页查询邮箱配置信息
     *
     * @param mailConfigPageVO
     * @return
     */
    ResultData<Page<MailConfig>> listMailConfigsPage(MailConfigPageVO mailConfigPageVO);

}
