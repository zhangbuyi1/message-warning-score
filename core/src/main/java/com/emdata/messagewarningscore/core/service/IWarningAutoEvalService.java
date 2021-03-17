package com.emdata.messagewarningscore.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.WarningAutoEvalDO;
import com.emdata.messagewarningscore.core.controller.vo.WarningAutoEvalQueryParam;
import com.emdata.messagewarningscore.core.service.bo.WarningAutoEvalBO;
import com.emdata.messagewarningscore.core.service.bo.WarningAutoEvalTableBO;

import java.util.List;

/**
 * @description: 预警自动评估服务类
 * @date: 2020/12/14
 * @author: sunming
 */
public interface IWarningAutoEvalService extends IService<WarningAutoEvalDO> {


    /**
     * 预警自动评估分页查询
     *
     * @param airportCode               机场编码
     * @param warningAutoEvalQueryParam 分页查询参数
     * @return 查询后的结果
     */
    List<WarningAutoEvalBO> warningAutoEvalPage(String airportCode, WarningAutoEvalQueryParam warningAutoEvalQueryParam);

}
