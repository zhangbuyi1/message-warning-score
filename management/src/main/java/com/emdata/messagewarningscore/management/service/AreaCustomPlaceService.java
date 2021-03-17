package com.emdata.messagewarningscore.management.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.management.controller.vo.PlaceAddParam;
import com.emdata.messagewarningscore.management.controller.vo.PlaceQueryPageParam;
import com.emdata.messagewarningscore.management.controller.vo.PlaceUpdateParam;
import com.emdata.messagewarningscore.management.entity.AreaCustomPlaceDO;
import com.emdata.messagewarningscore.management.service.bo.AreaCustomPlaceBO;
import com.emdata.messagewarningscore.management.service.bo.AreaCustomPlacePageBO;

/**
 * @author: sunming
 * @date: 2020/1/10
 * @description:
 */
public interface AreaCustomPlaceService extends IService<AreaCustomPlaceDO> {

    /**
     * 根据地点编码查询地点信息
     *
     * @param placeCode 地点编码
     * @return
     */
    AreaCustomPlaceBO findByPlaceCode(String placeCode);

    AreaCustomPlaceDO createPlace(PlaceAddParam param);

    AreaCustomPlaceDO deleteByPlaceCode(String placeCode);

    AreaCustomPlaceDO updataPlace(PlaceUpdateParam param);

    Page<AreaCustomPlacePageBO> queryAreaCustomPlacePage(PlaceQueryPageParam param);
}
