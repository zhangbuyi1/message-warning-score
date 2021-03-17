package com.emdata.messagewarningscore.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.MdrsAutoEvalInfosMapper;
import com.emdata.messagewarningscore.common.dao.MdrsAutoEvalMapper;
import com.emdata.messagewarningscore.common.dao.entity.MdrsAutoEvalDO;
import com.emdata.messagewarningscore.common.dao.entity.MdrsAutoEvalInfosDO;
import com.emdata.messagewarningscore.common.enums.WeatherStrengthEnum;
import com.emdata.messagewarningscore.common.service.IAirportService;
import com.emdata.messagewarningscore.core.controller.vo.MdrsAutoEvalQueryParam;
import com.emdata.messagewarningscore.core.service.IMdrsAutoEvalService;
import com.emdata.messagewarningscore.core.service.bo.MdrsAutoEvalAllBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description: mdrs自动评估服务实现类
 * @date: 2020/12/16
 * @author: sunming
 */
@Service
public class MdrsAutoEvalServiceImpl extends ServiceImpl<MdrsAutoEvalMapper, MdrsAutoEvalDO> implements IMdrsAutoEvalService {

    @Autowired
    private MdrsAutoEvalMapper mdrsAutoEvalMapper;

    @Autowired
    private MdrsAutoEvalInfosMapper infosMapper;

    @Autowired
    private IAirportService iAirportService;

    /**
     * mdrs自动评估分页查询
     *
     * @param airportCode 机场代码
     * @param param       查询参数
     * @return 查询结果
     */
    @Override
    public List<MdrsAutoEvalAllBO> mdrsAutoEvalPage(String airportCode, MdrsAutoEvalQueryParam param) {
        List<MdrsAutoEvalDO> mdrsAutoEvalDOS = findBetweenDate(airportCode, param.getStartDate(), param.getEndDate());
        List<MdrsAutoEvalAllBO> autoEvalAllBOS = findAllBO(mdrsAutoEvalDOS);
        return autoEvalAllBOS;
    }

    public List<MdrsAutoEvalAllBO> findAllBO(List<MdrsAutoEvalDO> mdrsAutoEvalDOS) {
        List<MdrsAutoEvalAllBO> mdrsAutoEvalAllBOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mdrsAutoEvalDOS)) {
            for (MdrsAutoEvalDO mdrsAutoEvalDO : mdrsAutoEvalDOS) {
                MdrsAutoEvalAllBO mdrsAutoEvalAllBO = new MdrsAutoEvalAllBO();
                Integer id = mdrsAutoEvalDO.getId();
                LambdaQueryWrapper<MdrsAutoEvalInfosDO> lqw = new LambdaQueryWrapper<>();
                lqw.eq(MdrsAutoEvalInfosDO::getEvalId, id);
                List<MdrsAutoEvalInfosDO> mdrsAutoEvalInfosDOS = infosMapper.selectList(lqw);
                if (CollectionUtils.isNotEmpty(mdrsAutoEvalInfosDOS)) {
                    MdrsAutoEvalInfosDO mdrsAutoEvalInfosDO = mdrsAutoEvalInfosDOS.get(0);
                    BeanUtils.copyProperties(mdrsAutoEvalDO, mdrsAutoEvalAllBO);
                    BeanUtils.copyProperties(mdrsAutoEvalInfosDO, mdrsAutoEvalAllBO);
                    mdrsAutoEvalAllBO.setPreStrength(WeatherStrengthEnum.getCode(mdrsAutoEvalInfosDO.getPreStrength()));
                    mdrsAutoEvalAllBO.setMetarStrength(WeatherStrengthEnum.getCode(mdrsAutoEvalInfosDO.getMetarStrength()));
                    mdrsAutoEvalAllBO.setAirportName(iAirportService.findByAirportCode(mdrsAutoEvalDO.getAirportCode()).getAirportName());
                    mdrsAutoEvalAllBOS.add(mdrsAutoEvalAllBO);
                }
            }
        }
        return mdrsAutoEvalAllBOS;
    }

    /**
     * 获取指定日期内的重要天气自动记录数据
     *
     * @param airportCode 机场代码
     * @param startDay    开始日期 yyyy-MM-dd
     * @param endDay      结束日期 yyyy-MM-dd
     * @return
     */
    public List<MdrsAutoEvalDO> findBetweenDate(String airportCode, String startDay, String endDay) {
        Date startDayDate = Java8DateUtils.parseDateStr(startDay, Java8DateUtils.DATE);
        Date endDayDate = Java8DateUtils.parseDateStr(endDay, Java8DateUtils.DATE);
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
        LambdaQueryWrapper<MdrsAutoEvalDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MdrsAutoEvalDO::getAirportCode, airportCode);
        lqw.ge(MdrsAutoEvalDO::getReleaseTime, startDayDate);
        lqw.le(MdrsAutoEvalDO::getReleaseTime, endDayDate);
        lqw.orderByDesc(MdrsAutoEvalDO::getReleaseTime);
        List<MdrsAutoEvalDO> mdrsAutoEvalDOS = mdrsAutoEvalMapper.selectList(lqw);
        return mdrsAutoEvalDOS;
    }
}
