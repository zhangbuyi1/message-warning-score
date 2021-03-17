package com.emdata.messagewarningscore.data.service;/**
 * Created by zhangshaohu on 2020/12/29.
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.service.bo.SeriousBO;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/29
 * @description:
 */
public interface IMetarSourceService extends IService<MetarSourceDO> {

    List<Metar> getMetarSourceByTime(String airport, Date startTime, Date endTime);

    /**
     * 获得重要天气
     *
     * @param airport   机场code
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    List<SeriousBO> getMetarSourceToSerious(String airport, Date startTime, Date endTime, boolean ifFloating);

}