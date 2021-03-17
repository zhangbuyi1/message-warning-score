package com.emdata.messagewarningscore.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.result.ResultData;

import java.util.List;

/**
 * @Desc 警报原始内容解析
 * @author lihongjiang
 */
public interface AlertOriginalContentService extends IService<AlertOriginalTextDO> {

    /**
     * 解析警报原始文本
     *
     * @param folder    文件夹
     * @param name      文件名
     **/
    ResultData<List<AlertOriginalTextDO>> parseOriginalText(String folder, String name);

}
