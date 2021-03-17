package com.emdata.messagewarningscore.data.service2;

import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;

import java.io.FileInputStream;
import java.util.List;

/**
 * @Desc 解析预警文本
 * @Author lihongjiang
 * @Date 2020/12/24 10:31
 **/
public interface ParseTextContentService {

    /**
     * 文件格式转换
     *
     * @param
     * @return
     **/
    ResultData<AlertOriginalTextDO> fileFormatConver(FileInputStream fileInput);

    /**
     * 解析文件内容
     *
     * @param
     * @return
     **/
    ResultData<List<AlertContentResolveDO>> parseTextContent(AlertOriginalTextDO airportAlert);

    /**
     * 保存警报文本内容
     *
     * @param
     * @return
     **/
    void saveTextContent(List<AlertContentResolveDO> airportAlertContents);

}
