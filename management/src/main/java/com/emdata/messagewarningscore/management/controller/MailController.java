//package com.emdata.messagewarningscore.management.controller;
//
//
//import com.emdata.messagewarningscore.management.controller.vo.MailMsgVO;
//import com.emdata.messagewarningscore.common.result.ResultData;
//import com.emdata.messagewarningscore.management.service.MsgService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author: sunming
// * @date: 2020/1/8
// * @description:
// */
//@Slf4j
//@RestController
//@RequestMapping("mail")
//@Api(tags = "邮件相关接口", description = "发送邮件等相关接口")
//public class MailController {
//
//    @Autowired
//    private MsgService msgService;
//
//
//    @ApiOperation(value = "发送邮件", notes = "发送邮件")
//    @PostMapping
//    public ResultData sendMailMsg(@RequestBody MailMsgVO mailMsgVO) {
//        return msgService.sendMailMsg(mailMsgVO);
//
//    }
//}