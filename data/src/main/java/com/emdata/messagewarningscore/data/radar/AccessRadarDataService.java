package com.emdata.messagewarningscore.data.radar;

import com.emdata.messagewarningscore.common.common.utils.PicUtil;
import com.emdata.messagewarningscore.common.dao.entity.CrPictureDO;
import com.emdata.messagewarningscore.common.dao.entity.RadarDataDO;
import com.emdata.messagewarningscore.data.radar.bo.TimeRangeRadar;

import java.io.File;

/**
 * @description: 接入雷达数据服务层
 * @date: 2021/1/15
 * @author: sunming
 */
public interface AccessRadarDataService {

    /**
     * 保存雷达数据
     *
     * @param airportCode 机场代码
     * @param file        文件
     */
    void saveRadarData(String airportCode, File file);

    /**
     * 保存雷达图片
     *
     * @param airportCode
     * @param base64
     * @param timeAnRangeRadar
     */
    CrPictureDO saveCrPicture(String airportCode, String base64, String radarUuid, TimeRangeRadar.TimeAnRangeRadar timeAnRangeRadar);

    void saveRadarData(CrPictureDO crPictureDO, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Integer locationId, String base64);

    void saveRadarAndPic(String airportCode, String base64, String radarUuid, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Integer locationId, TimeRangeRadar.TimeAnRangeRadar timeAnRangeRadar);


    /**
     * 封装crPictureDO
     *
     * @param airportCode 机场code
     * @param file        文件
     * @return 雷达图片do
     */
    CrPictureDO packageCrPicture(String airportCode, File file);

    /**
     * 封装radarDataDO
     *
     * @param locationId  位置id
     * @param crPictureDO 雷达图片do
     * @return 雷达数据do
     */
    RadarDataDO packageRadarData(Integer locationId, CrPictureDO crPictureDO);
}
