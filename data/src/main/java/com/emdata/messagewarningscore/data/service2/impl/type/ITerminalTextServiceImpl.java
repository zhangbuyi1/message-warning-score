package com.emdata.messagewarningscore.data.service2.impl.type;

import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.data.service2.ParseTextContentService;
import com.emdata.messagewarningscore.data.service2.impl.adaptee.ParseAlertTextAdaptee;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.List;

/**
 * @Desc 解析预警文本适配器（类型：机场、区域、终端...）
 * @Author lihongjiang
 * @Date 2020/12/24 10:33
 **/
@Service
public class ITerminalTextServiceImpl extends ParseAlertTextAdaptee implements ParseTextContentService {

    @Override
    public ResultData<AlertOriginalTextDO> fileFormatConver(FileInputStream fileInput) {
        return null;
    }

    @Override
    public ResultData<List<AlertContentResolveDO>> parseTextContent(AlertOriginalTextDO airportAlert) {
        return null;
    }

}
