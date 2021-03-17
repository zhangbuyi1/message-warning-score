package com.emdata.messagewarningscore.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.MdrsAutoEvalDO;
import com.emdata.messagewarningscore.core.controller.vo.MdrsAutoEvalQueryParam;
import com.emdata.messagewarningscore.core.service.bo.MdrsAutoEvalAllBO;

import java.util.List;

/**
 * @description:
 * @date: 2020/12/16
 * @author: sunming
 */
public interface IMdrsAutoEvalService extends IService<MdrsAutoEvalDO> {

    /**
     * mdrs自动评估分页查询
     *
     * @param airportCode            机场代码
     * @param mdrsAutoEvalQueryParam 查询参数
     * @return 查询结果
     */
    List<MdrsAutoEvalAllBO> mdrsAutoEvalPage(String airportCode, MdrsAutoEvalQueryParam mdrsAutoEvalQueryParam);

}
