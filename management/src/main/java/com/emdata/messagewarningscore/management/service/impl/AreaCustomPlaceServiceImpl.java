package com.emdata.messagewarningscore.management.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.management.controller.vo.PlaceAddParam;
import com.emdata.messagewarningscore.management.controller.vo.PlaceQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.PlaceUpdateParam;
import com.emdata.messagewarningscore.management.dao.AreaCustomPlaceMapper;
import com.emdata.messagewarningscore.management.entity.AreaCustomPlaceDO;
import com.emdata.messagewarningscore.management.entity.AreaDO;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.enums.StateEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.management.service.AreaCustomPlaceService;
import com.emdata.messagewarningscore.management.service.AreaService;
import com.emdata.messagewarningscore.management.service.bo.AreaCustomPlaceBO;
import com.emdata.messagewarningscore.management.service.bo.AreaCustomPlacePageBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/10
 * @description:
 */
@Slf4j
@Service
public class AreaCustomPlaceServiceImpl extends ServiceImpl<AreaCustomPlaceMapper, AreaCustomPlaceDO> implements AreaCustomPlaceService {

    @Autowired
    private AreaCustomPlaceMapper areaCustomPlaceMapper;

    @Autowired
    private AreaService areaService;

    /**
     * 根据地点编码查询地点信息
     *
     * @param placeCode 地点编码
     * @return
     */
    @Cacheable(value = "cache-ten-hour", key = "#root.targetClass+#p0", unless = "#result == null")
    @Override
    public AreaCustomPlaceBO findByPlaceCode(String placeCode) {
        AreaCustomPlaceBO areaCustomPlaceBO = new AreaCustomPlaceBO();
        LambdaQueryWrapper<AreaCustomPlaceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AreaCustomPlaceDO::getPlaceCode, placeCode);
        List<AreaCustomPlaceDO> areaCustomPlaceDOS = areaCustomPlaceMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(areaCustomPlaceDOS)) {
            return null;
        }

        BeanUtils.copyProperties(areaCustomPlaceDOS.get(0), areaCustomPlaceBO);
        //给bo的codes赋值
        String[] codes = codes(areaCustomPlaceBO);
        areaCustomPlaceBO.setCodes(codes);

        return areaCustomPlaceBO;
    }

    @Override
    public AreaCustomPlaceDO createPlace(PlaceAddParam param) {
        // 先判重
        LambdaQueryWrapper<AreaCustomPlaceDO> lqwAreaCustomPlace = new LambdaQueryWrapper<>();
        lqwAreaCustomPlace.eq(AreaCustomPlaceDO::getState, StateEnum.ON.getCode())
                .eq(AreaCustomPlaceDO::getPlaceCode, param.getPlaceCode());
        AreaCustomPlaceDO areaCustomPlaceInBase = areaCustomPlaceMapper.selectOne(lqwAreaCustomPlace);
        if (areaCustomPlaceInBase != null) {
            throw new BusinessException(ResultCodeEnum.AREA_HAS_EXIST.getCode());
        }
        AreaCustomPlaceDO areaCustomPlaceDO = new AreaCustomPlaceDO();
        // 一级地点
        if (param.getParentCode().length() == 6) {
            LambdaQueryWrapper<AreaDO> lqwArea = new LambdaQueryWrapper<>();
            lqwArea.eq(AreaDO::getAreaCode, param.getParentCode());
            // 区县
            AreaDO district = areaService.getOne(lqwArea);
            lqwArea = new LambdaQueryWrapper<>();
            lqwArea.eq(AreaDO::getAreaCode, district.getParentCode());
            // 市
            AreaDO city = areaService.getOne(lqwArea);
            lqwArea = new LambdaQueryWrapper<>();
            lqwArea.eq(AreaDO::getAreaCode, city.getParentCode());
            // 省
            AreaDO province = areaService.getOne(lqwArea);
            areaCustomPlaceDO.setPlaceCode(param.getPlaceCode());
            areaCustomPlaceDO.setPlaceName(param.getPlaceName());
            areaCustomPlaceDO.setLevel(1);
            areaCustomPlaceDO.setParentCode("-1");
            areaCustomPlaceDO.setProvinceCode(province.getAreaCode());
            areaCustomPlaceDO.setProvinceName(province.getAreaName());
            areaCustomPlaceDO.setCityCode(city.getAreaCode());
            areaCustomPlaceDO.setCityName(city.getAreaName());
            areaCustomPlaceDO.setDistrictCode(district.getAreaCode());
            areaCustomPlaceDO.setDistrictName(district.getAreaName());
            int insert = areaCustomPlaceMapper.insert(areaCustomPlaceDO);
            if (insert != 1) {
                throw new BusinessException(ResultCodeEnum.AREA_ADD_ERR.getCode());
            }
        }
        // 二级地点
        if (param.getParentCode().length() == 12) {
            // 直接查出对应的一级地点
            LambdaQueryWrapper<AreaCustomPlaceDO> lqwParentAreaCustomPlace = new LambdaQueryWrapper<>();
            lqwParentAreaCustomPlace.eq(AreaCustomPlaceDO::getState, StateEnum.ON.getCode())
                    .eq(AreaCustomPlaceDO::getPlaceCode, param.getParentCode());
            AreaCustomPlaceDO areaCustomPlaceParent = areaCustomPlaceMapper.selectOne(lqwParentAreaCustomPlace);
            if (areaCustomPlaceParent == null) {
                throw new BusinessException(ResultCodeEnum.PARENT_AREA_NOT_EXIST.getCode());
            }
            BeanUtils.copyProperties(areaCustomPlaceParent, areaCustomPlaceDO);
            areaCustomPlaceDO.setId(null);
            areaCustomPlaceDO.setPlaceCode(param.getPlaceCode());
            areaCustomPlaceDO.setPlaceName(param.getPlaceName());
            areaCustomPlaceDO.setParentCode(param.getParentCode());
            areaCustomPlaceDO.setLevel(areaCustomPlaceParent.getLevel() + 1);
            int insert = areaCustomPlaceMapper.insert(areaCustomPlaceDO);
            if (insert != 1) {
                throw new BusinessException(ResultCodeEnum.AREA_ADD_ERR.getCode());
            }
        }
        return areaCustomPlaceDO;
    }

    @Override
    public AreaCustomPlaceDO deleteByPlaceCode(String placeCode) {
        LambdaQueryWrapper<AreaCustomPlaceDO> lqwAreaCustomPlace = new LambdaQueryWrapper<>();
        lqwAreaCustomPlace.eq(AreaCustomPlaceDO::getState, StateEnum.ON.getCode())
                .eq(AreaCustomPlaceDO::getPlaceCode, placeCode);
        AreaCustomPlaceDO areaCustomPlaceInBase = areaCustomPlaceMapper.selectOne(lqwAreaCustomPlace);
        // 数据库判空
        if (areaCustomPlaceInBase == null) {
            throw new BusinessException(ResultCodeEnum.AREA_NOT_EXIST.getCode());
        }
        areaCustomPlaceInBase.setState(StateEnum.OFF.getCode());
        int updateById = areaCustomPlaceMapper.updateById(areaCustomPlaceInBase);
        if (updateById != 1) {
            throw new BusinessException(ResultCodeEnum.AREA_DELETE_ERRO.getCode());
        }
        return areaCustomPlaceInBase;
    }

    @Override
    public AreaCustomPlaceDO updataPlace(PlaceUpdateParam param) {
        AreaCustomPlaceDO areaCustomPlaceDO = new AreaCustomPlaceDO();
        BeanUtils.copyProperties(param, areaCustomPlaceDO);
        // 可以修改父级关系
        if (StringUtils.isNotBlank(param.getParentCode())) {
            if (param.getParentCode().length() == 6) {
                areaCustomPlaceDO.setLevel(1);
                areaCustomPlaceDO.setParentCode("-1");
            }
            if (param.getParentCode().length() == 12) {
                LambdaQueryWrapper<AreaCustomPlaceDO> lqwParentAreaCustomPlace = new LambdaQueryWrapper<>();
                lqwParentAreaCustomPlace.eq(AreaCustomPlaceDO::getState, StateEnum.ON.getCode())
                        .eq(AreaCustomPlaceDO::getPlaceCode, param.getParentCode());
                AreaCustomPlaceDO areaCustomPlaceParent = areaCustomPlaceMapper.selectOne(lqwParentAreaCustomPlace);
                if (areaCustomPlaceParent == null) {
                    throw new BusinessException(ResultCodeEnum.PARENT_AREA_NOT_EXIST.getCode());
                }
                areaCustomPlaceDO.setLevel(areaCustomPlaceParent.getLevel() + 1);
                areaCustomPlaceDO.setParentCode(param.getParentCode());
            }
        }
        int updateById = areaCustomPlaceMapper.updateById(areaCustomPlaceDO);
        if (updateById != 1) {
            throw new BusinessException(ResultCodeEnum.AREA_UPDATE_ERRO.getCode());
        }
        return areaCustomPlaceDO;
    }

    @Override
    public Page<AreaCustomPlacePageBO> queryAreaCustomPlacePage(PlaceQueryPageParam param) {
        log.info("查询参数： {}", param);
        // 设置分页参数
        Long current = param.getCurrent() == null ? 1 : param.getCurrent();
        Long size = param.getSize() == null ? 10 : param.getSize();

        // 分页查询
        Page<AreaCustomPlacePageBO> page = new Page<>(current, size);
        if (StringUtils.isBlank(param.getOrderBy())) {
            param.setOrderBy("create_time");
        }

        if (param.getSort() == Sort.Direction.DESC) {
            page.setDesc(param.getOrderBy());
        } else {
            page.setAsc(param.getOrderBy());
        }

        List<AreaCustomPlacePageBO> res = areaCustomPlaceMapper.selectPageFromView(param, page);
        page.setRecords(res);
        return page;
    }

    public String[] codes(AreaCustomPlaceBO areaCustomPlaceBO) {
        List<String> strs = new ArrayList<>();
        String provinceCode = areaCustomPlaceBO.getProvinceCode();
        String cityCode = areaCustomPlaceBO.getCityCode();
        String districtCode = areaCustomPlaceBO.getDistrictCode();
        String placeCode = areaCustomPlaceBO.getPlaceCode();
        String parentCode = areaCustomPlaceBO.getParentCode();
        Integer level = areaCustomPlaceBO.getLevel();
        strs.add(provinceCode);
        strs.add(cityCode);
        strs.add(districtCode);
        if (level == 1) {
            strs.add(placeCode);
        }
        if (level == 2) {
            strs.add(parentCode);
            strs.add(placeCode);
        }
        if (level == 3) {
            //查询出对应一级的code
            String code = getPlaceCodeByParentCode(parentCode);
            strs.add(code);
            strs.add(parentCode);
            strs.add(placeCode);
        }
        String[] codes = strs.toArray(new String[strs.size()]);
        return codes;
    }


    /**
     * 根据上一级code查询出对应的地点编码
     *
     * @param parentCode
     * @return
     */
    private String getPlaceCodeByParentCode(String parentCode) {
        LambdaQueryWrapper<AreaCustomPlaceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AreaCustomPlaceDO::getParentCode, parentCode);
        List<AreaCustomPlaceDO> areaCustomPlaceDOS = areaCustomPlaceMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(areaCustomPlaceDOS)) {
            return null;
        }
        String placeCode = areaCustomPlaceDOS.get(0).getPlaceCode();
        return placeCode;
    }
}
