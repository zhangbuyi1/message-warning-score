package com.emdata.messagewarningscore.common.dao;/**
 * Created by zhangshaohu on 2020/12/31.
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/31
 * @description: 重要天气记录
 */
public interface SeriousMapper extends BaseMapper<SeriousDO> {
    /**
     * 获取指定日期内的重要天气自动记录数据
     *
     * @param airportCode 机场代码
     * @param startDay    开始日期 yyyy-MM-dd
     * @param endDay      结束日期 yyyy-MM-dd
     * @return
     */
    List<SeriousDO> findBetweenDate(@Param("airportCode") String airportCode,
                                    @Param("startDay") String startDay,
                                    @Param("endDay") String endDay);
}