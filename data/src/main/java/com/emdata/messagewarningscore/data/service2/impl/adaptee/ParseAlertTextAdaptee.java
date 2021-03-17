package com.emdata.messagewarningscore.data.service2.impl.adaptee;

import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.data.dao.entity.TextContentTemplateDO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Desc 解析预警文本适配器（类型：机场、区域、终端...）
 * @Author lihongjiang
 * @Date 2020/12/24 10:33
 **/
@Component
public class ParseAlertTextAdaptee {

    /**
     * 保存警报文本内容
     **/
    public void saveTextContent(List<AlertContentResolveDO> airportAlertContents) {

    }

    /**
     * 通过模板类型，查询模板
     **/
    public List<TextContentTemplateDO> getTextContentTemplate(String type) {



        return null;
    }



}
