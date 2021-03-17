package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.dao.AreaCustomPlaceMapper;
import com.emdata.messagewarningscore.management.dao.AreaMapper;
import com.emdata.messagewarningscore.management.entity.AreaCustomPlaceDO;
import com.emdata.messagewarningscore.management.entity.AreaDO;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.management.service.AreaService;
import com.emdata.messagewarningscore.management.service.bo.AreaBO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, AreaDO> implements AreaService {

    @Autowired(required = false)
    private AreaMapper areaMapper;

    @Autowired
    private AreaCustomPlaceMapper areaCustomPlaceMapper;

    /**
     * 查询地区信息
     *
     * @param code 地区编码
     * @return
     */
    @Override
    public List<AreaBO> findAreas(String code) {
        if (code == null || code.trim().length() == 0) {
            code = "-1";
        }
        List<AreaBO> areas;

        if ("-1".equals(code) || code.length() <= 6 && code.endsWith("00")) {
            areas = findFromArea(code);
        } else {
            areas = findFromCustomPlace(code);
        }
        return areas;
    }

    private List<AreaBO> findFromCustomPlace(String code) {
        LambdaQueryWrapper<AreaCustomPlaceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AreaCustomPlaceDO::getState, StateEnum.ON.getCode());
        if (code.length() <= 6) {
            lqw.eq(AreaCustomPlaceDO::getDistrictCode, code);
            lqw.eq(AreaCustomPlaceDO::getLevel, 1);
        } else {
            lqw.eq(AreaCustomPlaceDO::getParentCode, code);
        }
        List<AreaCustomPlaceDO> areaCustomPlaces = areaCustomPlaceMapper.selectList(lqw);
        List<AreaBO> areaBOS = new ArrayList<>();
        if (areaCustomPlaces != null && areaCustomPlaces.size() > 0) {
            for (AreaCustomPlaceDO areaCustomPlace : areaCustomPlaces) {
                AreaBO areaBO = new AreaBO();
                String placeCode = areaCustomPlace.getPlaceCode();
                AreaCustomPlaceDO byParentCode = findByParentCode(placeCode);
                if (byParentCode == null) {
                    areaBO.setHasChildren(0);
                } else {
                    areaBO.setHasChildren(1);
                }
                areaBO.setCode(areaCustomPlace.getPlaceCode());
                areaBO.setName(areaCustomPlace.getPlaceName());
                areaBOS.add(areaBO);
            }
        }
        return areaBOS;
    }

    /**
     * 根据code查询是否有下级地点
     *
     * @param code
     * @return
     */
    private AreaCustomPlaceDO findByParentCode(String code) {
        LambdaQueryWrapper<AreaCustomPlaceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AreaCustomPlaceDO::getState, StateEnum.ON.getCode())
                .eq(AreaCustomPlaceDO::getParentCode, code);
        List<AreaCustomPlaceDO> areaCustomPlaceDOS = areaCustomPlaceMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(areaCustomPlaceDOS)) {
            return null;
        } else {
            return areaCustomPlaceDOS.get(0);
        }
    }

    /**
     * 从area表中查询省、市、区
     *
     * @param code
     * @return
     */
    private List<AreaBO> findFromArea(String code) {
        LambdaQueryWrapper<AreaDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AreaDO::getParentCode, code);
        List<AreaDO> areaDOS = areaMapper.selectList(lqw);
        if (areaDOS != null && areaDOS.size() > 0) {
            return areaDOS.stream().map(a -> new AreaBO(a.getAreaCode(), a.getAreaName(), 1)).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

}
