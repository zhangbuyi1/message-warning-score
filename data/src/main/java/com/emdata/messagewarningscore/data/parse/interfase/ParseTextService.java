package com.emdata.messagewarningscore.data.parse.interfase;

import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.result.ResultData;

import java.util.List;

/**
 * @author lihongjiang
 */
public interface ParseTextService {

    /**
     * 解析原始文本，获取重要参数
     *
     * @param airportAlert 原始文本
     * @return 重要参数
     **/
    ResultData<List<AlertContentResolveDO>> getImportantParam(AlertOriginalTextDO airportAlert);
}
