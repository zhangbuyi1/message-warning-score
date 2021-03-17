package com.emdata.messagewarningscore.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.LogQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.LogWriteParam;
import com.emdata.messagewarningscore.management.entity.LogDO;

/**
 * @author changfeng
 * @description
 * @date 2020/2/26
 */
public interface LogService extends IService<LogDO> {


    void addLog(LogWriteParam param);

    Page<LogDO> queryLogPage(LogQueryPageParam param);
}
