package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;

import java.util.List;

/**
 * @description: 地点服务类
 * @date: 2021/1/6
 * @author: sunming
 */
public interface ILocationInfoService extends IService<LocationInfoDO> {

    /**
     * 根据雷达uuid查询出地点信息
     *
     * @param radarInfoUuid 雷达uuid
     * @return
     */
    List<LocationInfoDO> findByRadarUuid(String radarInfoUuid);

    LocationInfoDO findByName(String name);
}
