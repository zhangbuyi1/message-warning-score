package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.MailRecipientPageVO;
import com.emdata.messagewarningscore.management.controller.vo.MailRecipientParam;
import com.emdata.messagewarningscore.management.entity.MailRecipientDO;
import com.emdata.messagewarningscore.common.result.ResultData;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
public interface MailRecipientService extends IService<MailRecipientDO> {


    /**
     * 新增收件人信息
     *
     * @param mailRecipientParam
     * @return
     */
    MailRecipientDO createRecipientInfo(MailRecipientParam mailRecipientParam);


    /**
     * 修改收件人信息
     *
     * @param mailRecipientParam
     * @return
     */
    ResultData<MailRecipientDO> updateRecipientInfo(MailRecipientParam mailRecipientParam);


    /**
     * 删除收件人信息ID
     *
     * @param mailRecipientId
     * @return
     */
    ResultData<MailRecipientDO> deleteRecipientInfo(Integer mailRecipientId);

    /**
     * 分页查询收件人信息
     *
     * @param mailRecipientPageVO
     * @return
     */
    ResultData<Page<MailRecipientDO>> listMailRecipientsPage(MailRecipientPageVO mailRecipientPageVO);

}
