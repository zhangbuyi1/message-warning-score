package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.MailConfigPageVO;
import com.emdata.messagewarningscore.management.controller.vo.MailConfigVO;
import com.emdata.messagewarningscore.management.dao.MailConfigMapper;
import com.emdata.messagewarningscore.management.entity.MailConfig;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.MailConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: sunming
 * @date: 2020/1/8
 * @description:
 */
@Service
public class MailConfigServiceImpl extends ServiceImpl<MailConfigMapper, MailConfig> implements MailConfigService {


    @Autowired
    private MailConfigMapper mailConfigMapper;

    /**
     * 新增邮箱配置信息
     *
     * @param mailConfigVO
     * @return
     */
    @Override
    public ResultData<MailConfig> createMailConfig(MailConfigVO mailConfigVO) {

        MailConfig mailConfig = new MailConfig();
        BeanUtils.copyProperties(mailConfigVO, mailConfig);
        mailConfigMapper.insert(mailConfig);

        return ResultData.success(mailConfig);
    }

    /**
     * 修改邮箱配置信息
     *
     * @param mailConfigVO
     * @return
     */
    @Override
    public ResultData<MailConfig> updateMailConfig(MailConfigVO mailConfigVO) {
        MailConfig mailConfig = new MailConfig();
        BeanUtils.copyProperties(mailConfigVO, mailConfig);
        mailConfigMapper.updateById(mailConfig);


        return ResultData.success(mailConfig);

    }

    /**
     * 删除邮箱配置信息
     *
     * @param id
     * @return
     */
    @Override
    public ResultData<MailConfig> deleteMailConfig(Integer id) {
        MailConfig mailConfig = mailConfigMapper.selectById(id);
        mailConfigMapper.deleteById(id);
        return ResultData.success(mailConfig);
    }

    /**
     * 分页查询邮箱配置信息
     *
     * @param param 分页查询参数
     * @return
     */
    @Override
    public ResultData<Page<MailConfig>> listMailConfigsPage(MailConfigPageVO param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();

        //分页查询
        LambdaQueryWrapper<MailConfig> lqw = new LambdaQueryWrapper<>();

        //按照条件查询
        lqw.like(StringUtils.isNotBlank(param.getAppId()), MailConfig::getAppId, param.getAppId());
        lqw.like(StringUtils.isNotBlank(param.getUsername()), MailConfig::getUsername, param.getUsername());

        Page<MailConfig> pages = new Page<>(current, size);
        IPage<MailConfig> mailConfigDOIPage = mailConfigMapper.selectPage(pages, lqw);
        pages.setTotal(mailConfigDOIPage.getTotal());
        pages.setPages(mailConfigDOIPage.getPages());
        return ResultData.success(pages);
    }
}
