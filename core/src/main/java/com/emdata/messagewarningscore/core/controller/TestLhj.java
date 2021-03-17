package com.emdata.messagewarningscore.core.controller;

import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.data.service.AlertContentResolveService;
import com.emdata.messagewarningscore.data.service.AlertOriginalContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desc TestLhj
 * @Author lihongjiang 机场警报
 * @Date 2020/12/15 14:45
 **/
@RestController
@RequestMapping("airport_alert")
public class TestLhj {

    @Autowired
    private AlertOriginalContentService alertOriginalContentService;
    @Autowired
    private AlertContentResolveService alertContentResolveService;

    /**
     * 解析原始文本
     **/
    @GetMapping(value = "/batch")
    public ResultData<List<AlertOriginalTextDO>> batch(@RequestParam(value = "dir") String dir,
                                                       @RequestParam(value = "folder") String folder,
                                                       @RequestParam(value = "name") String name){
//        String filePath = "D:\\em-data\\眼控系统\\气象\\机场警报解析\\机场警报-2020\\2020_03\\2020032603.jcjb.doc";
        String filePath = dir + "\\" + folder + "\\";
        return alertOriginalContentService.parseOriginalText(filePath, name);
    }

    /**
     * 解析正文重要参数
     **/
    @GetMapping(value = "/parseTextContent")
    public ResultData parseTextContent(@RequestParam(value = "fileName", required = false) String fileName){
        return alertContentResolveService.parseTextContent(fileName);
    }

}
