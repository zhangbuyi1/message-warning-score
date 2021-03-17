package com.emdata.messagewarningscore.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.WarningAutoEvalInfosMapper;
import com.emdata.messagewarningscore.common.dao.WarningAutoEvalMapper;
import com.emdata.messagewarningscore.common.dao.entity.WarningAutoEvalDO;
import com.emdata.messagewarningscore.common.dao.entity.WarningAutoEvalInfosDO;
import com.emdata.messagewarningscore.common.enums.EvaluateEnum;
import com.emdata.messagewarningscore.common.enums.WeatherStrengthEnum;
import com.emdata.messagewarningscore.common.service.IAirportService;
import com.emdata.messagewarningscore.core.controller.vo.WarningAutoEvalQueryParam;
import com.emdata.messagewarningscore.core.service.IWarningAutoEvalService;
import com.emdata.messagewarningscore.core.service.bo.WarningAutoEvalBO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @date: 2020/12/14
 * @author: sunming
 */
@Service
public class WarningAutoEvalServiceImpl extends ServiceImpl<WarningAutoEvalMapper, WarningAutoEvalDO> implements IWarningAutoEvalService {

    @Autowired
    private WarningAutoEvalMapper warningAutoEvalMapper;

    @Autowired
    private WarningAutoEvalInfosMapper warningAutoEvalInfosMapper;

    @Autowired
    private IAirportService iAirportService;


    /**
     * 预警自动评估分页查询
     *
     * @param airportCode 机场编码
     * @param param       分页查询参数
     * @return 查询后的结果
     */
    @Override
    public List<WarningAutoEvalBO> warningAutoEvalPage(String airportCode, WarningAutoEvalQueryParam param) {
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        List<WarningAutoEvalDO> dos = new ArrayList<>();
        // 如果没有选择查询日期，默认展示最近3天
        if (StringUtils.isBlank(startDate)) {
            Date date = new Date();
            Date pass3dayDate = Java8DateUtils.addHour(date, -(long) (3 * 24));
            String pass3Ymd = Java8DateUtils.format(pass3dayDate, Java8DateUtils.DATE);
            dos = findBetweenDate(airportCode, pass3Ymd, Java8DateUtils.format(date, Java8DateUtils.DATE));
        } else {
            dos = findBetweenDate(airportCode, startDate, endDate);
        }

        List<WarningAutoEvalBO> toBO = findToBO(dos);
        return toBO;
    }


    public List<WarningAutoEvalDO> findBetweenDate(String airportCode, String startDate, String endDate) {
        Date startDayDate = Java8DateUtils.parseDateStr(startDate, Java8DateUtils.DATE);
        Date endDayDate = Java8DateUtils.parseDateStr(endDate, Java8DateUtils.DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(startDayDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        startDayDate = c.getTime();
        c.setTime(endDayDate);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        endDayDate = c.getTime();
        LambdaQueryWrapper<WarningAutoEvalDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WarningAutoEvalDO::getAirportCode, airportCode);
        lqw.ge(WarningAutoEvalDO::getReleaseTime, startDayDate);
        lqw.le(WarningAutoEvalDO::getReleaseTime, endDayDate);
        lqw.orderByDesc(WarningAutoEvalDO::getReleaseTime);
        List<WarningAutoEvalDO> warningAutoEvalDOS = warningAutoEvalMapper.selectList(lqw);
        return warningAutoEvalDOS;
    }

    public List<WarningAutoEvalBO> findToBO(List<WarningAutoEvalDO> autoEvalDOList) {
        List<WarningAutoEvalBO> warningAutoEvalBOS = new ArrayList<>();
        for (WarningAutoEvalDO warningAutoEvalDO : autoEvalDOList) {
            WarningAutoEvalBO warningAutoEvalBO = new WarningAutoEvalBO();
            Integer id = warningAutoEvalDO.getId();
            LambdaQueryWrapper<WarningAutoEvalInfosDO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(WarningAutoEvalInfosDO::getEvalId, id);
            List<WarningAutoEvalInfosDO> infosDOList = warningAutoEvalInfosMapper.selectList(lqw);
            if (CollectionUtils.isNotEmpty(infosDOList)) {
                WarningAutoEvalInfosDO warningAutoEvalInfosDO = infosDOList.get(0);
                BeanUtils.copyProperties(warningAutoEvalDO, warningAutoEvalBO);
                BeanUtils.copyProperties(warningAutoEvalInfosDO, warningAutoEvalBO);
                warningAutoEvalBO.setAirportName(iAirportService.findByAirportCode(warningAutoEvalDO.getAirportCode()).getAirportName());
                warningAutoEvalBO.setPreStrength(WeatherStrengthEnum.getCode(warningAutoEvalInfosDO.getPreStrength()));
                warningAutoEvalBO.setMetarStrength(WeatherStrengthEnum.getCode(warningAutoEvalInfosDO.getMetarStrength()));
                warningAutoEvalBO.setEvaluate(EvaluateEnum.getCode(warningAutoEvalDO.getEvaluate()));
                warningAutoEvalBOS.add(warningAutoEvalBO);
            }
        }
        return warningAutoEvalBOS;
    }

}
