package com.emdata.messagewarningscore.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.FtpInfoParam;
import com.emdata.messagewarningscore.management.controller.vo.FtpInfoQueryParam;
import com.emdata.messagewarningscore.common.common.utils.ftp.FtpLoginInfo;
import com.emdata.messagewarningscore.common.common.utils.ftp.FtpUtil;
import com.emdata.messagewarningscore.common.common.utils.ftp.MyFtpUtilImpl;
import com.emdata.messagewarningscore.management.dao.FtpInfoMapper;
import com.emdata.messagewarningscore.management.entity.FtpInfoDO;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.management.service.FtpInfoService;
import com.emdata.messagewarningscore.management.service.bo.FtpInfoBO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: sunming
 * @date: 2020/1/2
 * @description:
 */
@Service
public class FtpInfoServiceImpl extends ServiceImpl<FtpInfoMapper, FtpInfoDO> implements FtpInfoService {

    @Autowired
    private FtpInfoMapper ftpInfoMapper;

    /**
     * 新增ftp详细信息
     *
     * @param ftpInfoParam
     * @return
     */
    @Override
    public ResultData<FtpInfoDO> createFtpInfo(FtpInfoParam ftpInfoParam) {
        FtpInfoDO ftpInfoDO = new FtpInfoDO();
        BeanUtils.copyProperties(ftpInfoParam, ftpInfoDO);
        ftpInfoMapper.insert(ftpInfoDO);
        return ResultData.success(ftpInfoDO);
    }

    /**
     * 编辑ftp的详细信息
     *
     * @param ftpInfoParam
     * @return
     */
    @Override
    public ResultData<FtpInfoDO> updateFtpInfo(FtpInfoParam ftpInfoParam) {
        //获取编辑的ftp的id
        Integer ftpInfoId = ftpInfoParam.getId();
        FtpInfoDO ftpInfoDO = new FtpInfoDO();
        BeanUtils.copyProperties(ftpInfoParam, ftpInfoDO);
        ftpInfoDO.setId(ftpInfoId);
        ftpInfoMapper.updateById(ftpInfoDO);
        return ResultData.success(ftpInfoDO);
    }

    /**
     * 获取所有ftp信息
     *
     * @return
     */
    @Override
    public ResultData<List<FtpInfoBO>> obtainFtpInfo() {
        LambdaQueryWrapper<FtpInfoDO> wrapper = new LambdaQueryWrapper<>();
        // 记录状态：1-正常；9-已删除
        wrapper.eq(FtpInfoDO::getState, 1);
        List<FtpInfoDO> ftpInfoDOS = ftpInfoMapper.selectList(wrapper);

        List<FtpInfoBO> ftpInfoBOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ftpInfoDOS)) {
            for (FtpInfoDO ftpInfoDO : ftpInfoDOS) {
                FtpInfoBO ftpInfoBO = new FtpInfoBO();
                BeanUtils.copyProperties(ftpInfoDO, ftpInfoBO);
                ftpInfoBOS.add(ftpInfoBO);
            }
        }

        //按照创建时间倒序
        List<FtpInfoBO> collect = ftpInfoBOS.stream().sorted(Comparator.comparing(FtpInfoBO::getCreateTime).reversed()).collect(Collectors.toList());
        return ResultData.success(collect);
    }

    @Override
    public ResultData<Boolean> checkFtpInfo(FtpInfoParam ftpInfoParam) {
        FtpLoginInfo ftpBo = new FtpLoginInfo();
        ftpBo.setConnectTimeout(5000);
        // 如果有id，根据id查询
        if (ftpInfoParam.getId() != null) {
            FtpInfoDO ftpInfoInBase = ftpInfoMapper.selectById(ftpInfoParam.getId());
            ftpBo.setHostName(ftpInfoInBase.getFtpHost());
            ftpBo.setUsername(ftpInfoInBase.getFtpUser());
            ftpBo.setPassword(ftpInfoInBase.getFtpPassword());
            ftpBo.setPort(Integer.valueOf(ftpInfoInBase.getFtpPort()));
        } else {
            ftpBo.setHostName(ftpInfoParam.getFtpHost());
            ftpBo.setUsername(ftpInfoParam.getFtpUser());
            ftpBo.setPassword(ftpInfoParam.getFtpPassword());
            ftpBo.setPort(Integer.valueOf(ftpInfoParam.getFtpPort()));
        }
        boolean success = checkFtp(ftpBo);
        return ResultData.success(success);
    }

    @Override
    public Page<FtpInfoBO> queryFtpInfoPage(FtpInfoQueryParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<FtpInfoDO> lqwFtpInfo = new LambdaQueryWrapper<>();
        lqwFtpInfo.eq(FtpInfoDO::getState, StateEnum.ON.getCode())
                .orderByDesc(FtpInfoDO::getCreateTime);
        //按照条件查询
        lqwFtpInfo.like(StringUtils.isNotBlank(param.getFtpHost()), FtpInfoDO::getFtpHost, param.getFtpHost());
        lqwFtpInfo.like(StringUtils.isNotBlank(param.getFtpName()), FtpInfoDO::getFtpName, param.getFtpName());

        Page<FtpInfoDO> page = new Page<>(current, size);
        IPage<FtpInfoDO> ftpInfoIPage = ftpInfoMapper.selectPage(page, lqwFtpInfo);
        List<FtpInfoDO> ftpInfoList = ftpInfoIPage.getRecords();
        //将DO转为BO
        List<FtpInfoBO> ftpInfoBOS = new ArrayList<>();
        for (FtpInfoDO ftpInfoDO : ftpInfoList) {
            FtpInfoBO ftpInfoBO = new FtpInfoBO();
            BeanUtils.copyProperties(ftpInfoDO, ftpInfoBO);
            ftpInfoBOS.add(ftpInfoBO);
        }
        Page<FtpInfoBO> pages = new Page<>(current, size);
        pages.setRecords(ftpInfoBOS);
        pages.setTotal(ftpInfoIPage.getTotal());
        pages.setPages(ftpInfoIPage.getPages());
        return pages;
    }

    /**
     * 根据id删除ftp信息
     *
     * @param id
     * @return
     */
    @Override
    public ResultData<FtpInfoBO> deleteFtpInfo(Integer id) {
        FtpInfoDO ftpInfoDO = ftpInfoMapper.selectById(id);

        ftpInfoDO.setState(9);
        ftpInfoMapper.updateById(ftpInfoDO);
        FtpInfoBO ftpInfoBO = new FtpInfoBO();
        BeanUtils.copyProperties(ftpInfoDO, ftpInfoBO);
        return ResultData.success(ftpInfoBO);
    }

    /**
     * 检测ftp能否登陆成功
     *
     * @param ftpBo
     * @return
     */
    private boolean checkFtp(FtpLoginInfo ftpBo) {
        FtpUtil ftpUtil = new MyFtpUtilImpl(ftpBo);
        if (ftpUtil.client() == null) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        FtpLoginInfo ftpBo = new FtpLoginInfo();
        ftpBo.setHostName("192.168.90.10");
        ftpBo.setUsername("ykftp");
        ftpBo.setPassword("em2019");
        ftpBo.setPort(21);
        ftpBo.setConnectTimeout(500);
//        ftpBo.setRemoteDir("Camera_Weather/Pudong_08");
//        ftpBo.setLocalDir("D:\\data\\ftp");

        FtpUtil ftpUtil = new MyFtpUtilImpl(ftpBo);
        System.out.println(ftpUtil.client());
    }
}
