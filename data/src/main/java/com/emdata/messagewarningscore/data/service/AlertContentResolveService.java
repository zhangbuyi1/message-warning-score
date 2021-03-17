package com.emdata.messagewarningscore.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.result.ResultData;

/**
 * @Desc 警报内容解析
 * @author lihongjiang
 */
public interface AlertContentResolveService extends IService<AlertContentResolveDO> {

    /**
     * 解析正文内容
     */
    ResultData parseTextContent(String fileName);

}
