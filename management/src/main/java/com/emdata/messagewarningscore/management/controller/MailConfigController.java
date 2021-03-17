package com.emdata.messagewarningscore.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.MailConfigPageVO;
import com.emdata.messagewarningscore.management.controller.vo.MailConfigVO;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.MailConfig;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.MailConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: sunming
 * @date: 2020/1/8
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("mail_config")
@Api(tags = "邮件发送配置相关接口", description = "邮件发送配置等相关接口")
public class MailConfigController {

    @Autowired
    private MailConfigService mailConfigService;

    @LogAnnotation(type = LogTypeEnum.INSERT)
    @ApiOperation(value = "新增邮箱配置信息", notes = "新增邮箱配置信息")
    @PostMapping("create")
    public ResultData<MailConfig> createMailConfig(@RequestBody MailConfigVO mailConfigVO) {
        return mailConfigService.createMailConfig(mailConfigVO);
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE)
    @ApiOperation(value = "修改邮箱配置信息", notes = "修改邮箱配置信息")
    @PutMapping
    public ResultData<MailConfig> updateMailConfig(@RequestBody @Valid MailConfigVO mailConfigVO) {
        return mailConfigService.updateMailConfig(mailConfigVO);
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = "id")
    @ApiOperation(value = "删除邮箱配置信息", notes = "删除邮箱配置信息")
    @DeleteMapping
    public ResultData<MailConfig> deleteMailConfig(@RequestParam("id") Integer id) {
        return mailConfigService.deleteMailConfig(id);
    }

    @ApiOperation(value = "分页查询邮箱配置信息", notes = "分页查询邮箱配置信息")
    @GetMapping("page")
    public ResultData<Page<MailConfig>> listMailConfigsPage(MailConfigPageVO mailConfigPageVO) {
        return mailConfigService.listMailConfigsPage(mailConfigPageVO);
    }
}