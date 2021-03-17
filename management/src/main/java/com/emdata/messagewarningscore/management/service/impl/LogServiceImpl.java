package com.emdata.messagewarningscore.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.LogQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.LogWriteParam;
import com.emdata.messagewarningscore.management.controller.vo.MenuRelationVo;
import com.emdata.messagewarningscore.management.dao.LogMapper;
import com.emdata.messagewarningscore.management.entity.LogDO;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.management.service.LogService;
import com.emdata.messagewarningscore.management.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author changfeng
 * @description 日志业务类
 * @date 2020/2/26
 */

@Slf4j
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogDO> implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private MenuService menuService;


    @Override
    public void addLog(LogWriteParam param) {
        log.info("记录日志内容：" + param.toString());
        LogDO logDO = new LogDO();
        BeanUtils.copyProperties(param, logDO);
        logDO.setType(param.getLogType().getType());
        logDO.setTypeStr(param.getLogType().getMessage());
        // 菜单id可能为空，登录登出等
        if (param.getMenuId() != null) {
            // 根据菜单id查找多级菜单
            MenuRelationVo menuRelationVo = menuService.findMenusByMenuId(param.getMenuId());
            logDO.setMenuIdLevelOne(menuRelationVo.getMenuId());
            logDO.setMenuNameLevelOne(menuRelationVo.getMenuName());
            if (menuRelationVo.getChildVo() != null) {
                MenuRelationVo childTwo = menuRelationVo.getChildVo();
                logDO.setMenuIdLevelTwo(childTwo.getMenuId());
                logDO.setMenuNameLevelTwo(childTwo.getMenuName());
                if (childTwo.getChildVo() != null) {
                    MenuRelationVo childThree = childTwo.getChildVo();
                    logDO.setMenuIdLevelThree(childThree.getMenuId());
                    logDO.setMenuNameLevelThree(childThree.getMenuName());
                }
            }
        }
        // 入库
        int insert = logMapper.insert(logDO);
    }

    @Override
    public Page<LogDO> queryLogPage(LogQueryPageParam param) {
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();
        // 分页查询
        LambdaQueryWrapper<LogDO> lqwLog = new LambdaQueryWrapper<>();
        lqwLog.eq(LogDO::getState, StateEnum.ON.getCode())
                .likeRight(StringUtils.isNoneBlank(param.getUserName()), LogDO::getUserName, param.getUserName())
                .eq(param.getType() != null, LogDO::getType, param.getType())
                .eq(param.getSuccess() != null, LogDO::getSuccess, param.getSuccess())
                .ge(StringUtils.isNotBlank(param.getStartTime()), LogDO::getOperateTime, param.getStartTime())
                .le(StringUtils.isNotBlank(param.getEndTime()), LogDO::getOperateTime, param.getEndTime());
//                .orderByDesc(LogDO::getOperateTime);
        if (param.getSort() == Sort.Direction.DESC) {
            lqwLog.orderByDesc(LogDO::getOperateTime);
        } else {
            lqwLog.orderByAsc(LogDO::getOperateTime);
        }

        Page<LogDO> page = new Page<>(current, size);
        IPage<LogDO> logIPage = logMapper.selectPage(page, lqwLog);
        List<LogDO> logList = logIPage.getRecords();
        Page<LogDO> pages = new Page<>(current, size);
        pages.setRecords(logList);
        pages.setTotal(logIPage.getTotal());
        pages.setPages(logIPage.getPages());
        return pages;
    }
}
