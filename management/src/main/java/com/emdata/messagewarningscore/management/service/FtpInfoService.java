package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.FtpInfoParam;
import com.emdata.messagewarningscore.management.controller.vo.FtpInfoQueryParam;
import com.emdata.messagewarningscore.management.entity.FtpInfoDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.bo.FtpInfoBO;

import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/2
 * @description:
 */
public interface FtpInfoService extends IService<FtpInfoDO> {

    /**
     * 新增ftp详细信息
     *
     * @param ftpInfoParam
     * @return
     */
    ResultData<FtpInfoDO> createFtpInfo(FtpInfoParam ftpInfoParam);

    /**
     * 编辑ftp详细信息
     *
     * @param ftpInfoParam
     * @return
     */
    ResultData<FtpInfoDO> updateFtpInfo(FtpInfoParam ftpInfoParam);

    /**
     * 获取所有ftp信息
     */
    ResultData<List<FtpInfoBO>> obtainFtpInfo();

    ResultData<Boolean> checkFtpInfo(FtpInfoParam ftpInfoParam);

    Page<FtpInfoBO> queryFtpInfoPage(FtpInfoQueryParam param);

    /**
     * 根据id删除ftp信息
     *
     * @param id
     * @return
     */
    ResultData<FtpInfoBO> deleteFtpInfo(Integer id);
}
