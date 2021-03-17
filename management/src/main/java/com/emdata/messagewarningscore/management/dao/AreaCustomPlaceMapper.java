package com.emdata.messagewarningscore.management.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emdata.messagewarningscore.management.controller.vo.PlaceQueryPageParam;
import com.emdata.messagewarningscore.management.entity.AreaCustomPlaceDO;
import com.emdata.messagewarningscore.management.service.bo.AreaCustomPlacePageBO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
public interface AreaCustomPlaceMapper extends BaseMapper<AreaCustomPlaceDO> {

    /**
     * 从自定义地点的视图中查询分页信息，视图可以查出地点对应的所有上下级地点
     * @param param 查询参数
     * @param page 分页排序
     * @return 结果
     */
    @Select("<script>" +
            "select * from area_custom_place_view " +
            "<where>" +
            "  state = 1 " +
            "<if test='vo.placeCode != null and vo.placeCode != \"\"'>" +
            "  and place_code like concat(#{vo.placeCode}, '%')" +
            "</if>" +
            "<if test='vo.placeName != null and vo.placeName != \"\"'>" +
            "  and place_name like concat('%', #{vo.placeName}, '%')" +
            "</if>" +
            "</where>" +
            "</script>")
    List<AreaCustomPlacePageBO> selectPageFromView(@Param("vo") PlaceQueryPageParam param, Page<AreaCustomPlacePageBO> page);
}
