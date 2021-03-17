package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.MailRecipientPageVO;
import com.emdata.messagewarningscore.management.controller.vo.MailRecipientParam;
import com.emdata.messagewarningscore.management.dao.MailRecipientMapper;
import com.emdata.messagewarningscore.management.entity.MailRecipientDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.MailRecipientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
@Service
public class MailRecipientServiceImpl extends ServiceImpl<MailRecipientMapper, MailRecipientDO> implements MailRecipientService {


    @Autowired
    private MailRecipientMapper mailRecipientMapper;

    /**
     * 新增收件人信息
     *
     * @param mailRecipientParam
     * @return
     */
    @Override
    public MailRecipientDO createRecipientInfo(MailRecipientParam mailRecipientParam) {
        MailRecipientDO mailRecipientDO = new MailRecipientDO();
        BeanUtils.copyProperties(mailRecipientParam, mailRecipientDO);
        mailRecipientMapper.insert(mailRecipientDO);

        return mailRecipientDO;
    }

    /**
     * 修改收件人信息
     *
     * @param mailRecipientParam
     * @return
     */
    @Override
    public ResultData<MailRecipientDO> updateRecipientInfo(MailRecipientParam mailRecipientParam) {
        MailRecipientDO mailRecipientDO = new MailRecipientDO();
        BeanUtils.copyProperties(mailRecipientParam, mailRecipientDO);
        mailRecipientMapper.updateById(mailRecipientDO);
        return ResultData.success(mailRecipientDO);
    }

    /**
     * 删除收件人信息
     *
     * @param mailRecipientId
     * @return
     */
    @Override
    public ResultData<MailRecipientDO> deleteRecipientInfo(Integer mailRecipientId) {
        MailRecipientDO mailRecipientDO = mailRecipientMapper.selectById(mailRecipientId);
        mailRecipientMapper.deleteById(mailRecipientId);
        return ResultData.success(mailRecipientDO);
    }

    /**
     * 分页查询收件人信息
     *
     * @param param
     * @return
     */
    @Override
    public ResultData<Page<MailRecipientDO>> listMailRecipientsPage(MailRecipientPageVO param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();

        //分页查询
        LambdaQueryWrapper<MailRecipientDO> lqw = new LambdaQueryWrapper<>();

        //按照条件查询
        lqw.like(StringUtils.isNotBlank(param.getName()), MailRecipientDO::getName, param.getName());
        lqw.like(StringUtils.isNotBlank(param.getAddress()), MailRecipientDO::getAddress, param.getAddress());

        //按照创建时间倒序排序
        lqw.orderByDesc(MailRecipientDO::getCreateTime);

        Page<MailRecipientDO> pages = new Page<>(current, size);
        IPage<MailRecipientDO> mailRecipientDOIPage = mailRecipientMapper.selectPage(pages, lqw);
        pages.setTotal(mailRecipientDOIPage.getTotal());
        pages.setPages(mailRecipientDOIPage.getPages());
        return ResultData.success(pages);
    }

}
