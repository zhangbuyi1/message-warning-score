package com.emdata.messagewarningscore.data.service2.controller;

import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.data.service2.ParseMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileInputStream;

/**
 * @Desc ParseAlertTextController
 * @Author lihongjiang
 * @Date 2020/12/24 11:28
 **/
@RequestMapping(value = "/parse")
public class ParseAlertTextController {

    @Autowired
    private ParseMainService parseMainService;

    /**
     * 批量解析预警文本
     *
     * 1、通过类型和文件夹地址，区分调用哪种类型的接口，若类型为空，则调用所有接口
     *
     * @param type 类型123...；不能为空。
     * @param startTime 年月范围开始；大于等于开始年月；不能为空；时间范围不能一个传一个空。
     * @param endTime 年月范围结束；小于等于结束年月；不能为空；结束年月必须大于等开始年月。
     * @param fileName 文件名；为空时，执行某个类型的年有范围内所有文件。不为空时，以上3个条件必须都传。
     **/
    @PostMapping(value = "/batch")
    public ResultData parseAlertTextBatch(@RequestParam(value = "type") String type,
                                          @RequestParam(value = "startTime") String startTime,
                                          @RequestParam(value = "endTime") String endTime,
                                          @RequestParam(value = "fileName", required = false) String fileName){
        // 解析预警文本
        return parseMainService.parseTextContent(type, startTime, endTime, fileName);
    }

    /**
     * 解析预警文本
     *
     * 1、通过类型和文件夹地址，区分调用哪种类型的接口，若类型为空，则调用所有接口
     *
     * @param type 类型123...；不能为空。
     * @param fileInput 年月范围开始；大于等于开始年月；不能为空；时间范围不能一个传一个空。
     **/
    @PostMapping
    public ResultData parseAlertText(@RequestParam(value = "type") String type,
                                     @RequestParam(value = "fileInput") FileInputStream fileInput){
        // 解析预警文本
        return parseMainService.parseTextContent(type, fileInput);
    }
}
