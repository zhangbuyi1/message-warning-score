package com.emdata.messagewarningscore.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emdata.messagewarningscore.common.common.utils.PicUtil;
import com.emdata.messagewarningscore.common.dao.entity.CrPictureDO;
import com.emdata.messagewarningscore.common.service.bo.CrPictureBO;
import com.emdata.messagewarningscore.common.service.bo.PointRangeBO;
import com.emdata.messagewarningscore.common.service.bo.RadarEchoBO;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @date: 2021/1/14
 * @author: sunming
 */
public interface CrPictureService extends IService<CrPictureDO> {
    /**
     * 根据地点 查询一个范围的所有雷达图片
     *
     * @param locationId
     * @param startTime
     * @param endTime
     * @return
     */
    CrPictureBO getCrPicture(Integer locationId, Date startTime, Date endTime);

    /**
     * 根据地点 以及开始结束时间 查询到所有的雷达回波
     *
     * @param locationId 地点id
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return
     */
    List<RadarEchoBO> getRadarEcho(Integer locationId, Date startTime, Date endTime);

    /**
     * 获得一张图片的地点信息
     *
     * @param locationId
     * @param picUuid
     * @return
     */
    List<RadarEchoBO> getOneRadar(Integer locationId, String picUuid);

    List<RadarEchoBO> getOneRadar(Integer locationId, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint , Date picTime, String base64);

    /**
     * 解析一张图片
     *
     * @param point      地点范围
     * @param startPoint 雷达图片开始经纬度
     * @param endPoint   雷达图片结束经纬度
     * @param picPath    图片地址
     * @param picTime    图片时间
     * @return
     */
    List<RadarEchoBO> getOneRadar(List<PointRangeBO> point, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, String picPath, Date picTime);

    /**
     * 解析一张图片
     *
     * @param point      地点范围
     * @param startPoint 雷达图片开始经纬度
     * @param endPoint   雷达图片结束经纬度
     * @param picTime    图片时间
     * @return
     */
    List<RadarEchoBO> getOneRadar(List<PointRangeBO> point, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Date picTime, BufferedImage image);

    /**
     * 解析一张图片
     *
     * @param point      地点范围
     * @param startPoint 雷达图片开始经纬度
     * @param endPoint   雷达图片结束经纬度
     * @param picTime    图片时间
     * @return
     */
    List<RadarEchoBO> getOneRadar(List<PointRangeBO> point, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Date picTime, String base64);

}
