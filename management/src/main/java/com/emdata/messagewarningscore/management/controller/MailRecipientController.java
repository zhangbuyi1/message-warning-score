package com.emdata.messagewarningscore.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.MailRecipientPageVO;
import com.emdata.messagewarningscore.management.controller.vo.MailRecipientParam;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.management.entity.MailRecipientDO;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.MailRecipientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
@RestController
@Api(tags = "告警收件人相关接口", description = "告警收件人相关接口")
@RequestMapping("mail_recipient")
public class MailRecipientController {

    @Autowired
    private MailRecipientService mailRecipientService;

    @ApiOperation(value = "获取收件人信息", notes = "获取邮箱收件人信息")
    @GetMapping
    public ResultData<List<MailRecipientDO>> obtainAll(@ApiParam(value = "id", example = "1") @RequestParam(value = "ids", required = false) Integer[] ids) {
        List<MailRecipientDO> mailRecipients;
        if (ids != null && ids.length > 0) {
            mailRecipients = (List<MailRecipientDO>) mailRecipientService.listByIds(Arrays.asList(ids));
        } else {
            mailRecipients = mailRecipientService.list();
        }

        return ResultData.success(mailRecipients);
    }

    @LogAnnotation(type = LogTypeEnum.INSERT, field = {"name", "address"}, prefix = {"收件人姓名", "邮箱地址"})
    @ApiOperation(value = "新增收件人信息", notes = "新增收件人信息")
    @PostMapping
    public ResultData<MailRecipientDO> createRecipientInfo(@RequestBody MailRecipientParam mailRecipientParam) {

        return ResultData.success(mailRecipientService.createRecipientInfo(mailRecipientParam));
    }

    @LogAnnotation(type = LogTypeEnum.UPDATE)
    @ApiOperation(value = "修改收件人信息", notes = "修改收件人信息")
    @PutMapping
    public ResultData<MailRecipientDO> updateRecipientInfo(@RequestBody @Valid MailRecipientParam mailRecipientParam) {

        return mailRecipientService.updateRecipientInfo(mailRecipientParam);
    }

    @LogAnnotation(type = LogTypeEnum.DELETE, prefix = "id")
    @ApiOperation(value = "删除收件人信息", notes = "删除收件人信息")
    @DeleteMapping
    public ResultData<MailRecipientDO> deleteRecipientInfo(@ApiParam(value = "收件人信息ID", example = "1") @RequestParam("mailRecipientId") Integer mailRecipientId) {
        return mailRecipientService.deleteRecipientInfo(mailRecipientId);
    }

    @ApiOperation(value = "分页查询收件人信息")
    @GetMapping("page")
    public ResultData<Page<MailRecipientDO>> listMailRecipientsPage(MailRecipientPageVO mailRecipientPageVO) {
        return mailRecipientService.listMailRecipientsPage(mailRecipientPageVO);
    }


}
