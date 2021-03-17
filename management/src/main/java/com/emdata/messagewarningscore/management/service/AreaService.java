package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.entity.AreaDO;
import com.emdata.messagewarningscore.management.service.bo.AreaBO;

import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
public interface AreaService extends IService<AreaDO> {


    /**
     * 查询地区信息
     *
     * @param code 地区编码
     * @return
     */
    List<AreaBO> findAreas(String code);
}
