package com.emdata.messagewarningscore.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.common.utils.PicUtil;
import com.emdata.messagewarningscore.common.dao.CrPictureMapper;
import com.emdata.messagewarningscore.common.dao.entity.AirPointDO;
import com.emdata.messagewarningscore.common.dao.entity.CrPictureDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.RadarInfoDO;
import com.emdata.messagewarningscore.common.enums.RangeTypeEnum;
import com.emdata.messagewarningscore.common.radar.RadarParse;
import com.emdata.messagewarningscore.common.service.CrPictureService;
import com.emdata.messagewarningscore.common.service.IAirPointService;
import com.emdata.messagewarningscore.common.service.ILocationService;
import com.emdata.messagewarningscore.common.service.IRadarInfoService;
import com.emdata.messagewarningscore.common.service.bo.CrPictureBO;
import com.emdata.messagewarningscore.common.service.bo.PointRangeBO;
import com.emdata.messagewarningscore.common.service.bo.RadarEchoBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description:
 * @date: 2021/1/14
 * @author: sunming
 */
@Service
public class CrPictureServiceImpl extends ServiceImpl<CrPictureMapper, CrPictureDO> implements CrPictureService {
    @Autowired
    private IAirPointService airPointService;
    @Autowired
    private RadarParse radarParse;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IRadarInfoService radarInfoService;


    @Override
    public CrPictureBO getCrPicture(Integer locationId, Date startTime, Date endTime) {

        CrPictureBO crPictureBO = new CrPictureBO();
        // 根据地点id查询到雷达id
        LocationDO locationDO = locationService.getOne(new LambdaQueryWrapper<LocationDO>().eq(LocationDO::getId, locationId));
        // 得到雷达
        RadarInfoDO radarInfoDO = radarInfoService.getOne(new LambdaQueryWrapper<RadarInfoDO>().eq(RadarInfoDO::getAirportCode, locationDO.getAirportCode()));
        LambdaQueryWrapper<CrPictureDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CrPictureDO::getRadarUuid, radarInfoDO.getUuid());
        lqw.ge(CrPictureDO::getPicTime, startTime);
        lqw.le(CrPictureDO::getPicTime, endTime);
        // 得到雷达图片
        List<CrPictureDO> crPictureDOS = this.list(lqw);
        crPictureBO.setRadarInfoDO(radarInfoDO);
        crPictureBO.setCrPictureDOS(crPictureDOS);
        return crPictureBO;
    }


    /**
     * @param locationId 地点id
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return
     */
    @Override
    public List<RadarEchoBO> getRadarEcho(Integer locationId, Date startTime, Date endTime) {
        // 获得所有范围信息
        List<PointRangeBO> point = airPointService.getPoint(locationId);
        // 雷达数据信息
        CrPictureBO crPicture = getCrPicture(locationId, startTime, endTime);
        // 获得雷达数据
        RadarInfoDO radarInfoDO = crPicture.getRadarInfoDO();
        // 图片
        List<CrPictureDO> crPictureDOS = crPicture.getCrPictureDOS();
        // 得到雷达回波
        return crPictureDOS.stream().flatMap(s -> {
            return getOneRadar(point,
                    new PicUtil.LatLng(radarInfoDO.getStartLongitude(), radarInfoDO.getStartLatitude()),
                    new PicUtil.LatLng(radarInfoDO.getEndLongitude(), radarInfoDO.getEndLatitude()),
                    s.getPicPath(),
                    s.getPicTime()).stream();
        }).collect(Collectors.toList());
    }

    @Override
    public List<RadarEchoBO> getOneRadar(Integer locationId, String picUuid) {
        // 获得所有范围信息
        List<PointRangeBO> point = airPointService.getPoint(locationId);
        // 根据地点id查询到雷达id
        LocationDO locationDO = locationService.getOne(new LambdaQueryWrapper<LocationDO>().eq(LocationDO::getId, locationId));
        String radarInfoId = locationDO.getRadarInfoId();
        // 得到雷达
        RadarInfoDO radarInfoDO = radarInfoService.getOne(new LambdaQueryWrapper<RadarInfoDO>().eq(RadarInfoDO::getUuid, radarInfoId));
        CrPictureDO one = this.getOne(new LambdaQueryWrapper<CrPictureDO>().eq(CrPictureDO::getUuid, picUuid));
        return getOneRadar(point,
                new PicUtil.LatLng(radarInfoDO.getStartLongitude(), radarInfoDO.getStartLatitude()),
                new PicUtil.LatLng(radarInfoDO.getEndLongitude(), radarInfoDO.getEndLatitude()),
                one.getPicPath(),
                one.getPicTime());
    }

    @Override
    public List<RadarEchoBO> getOneRadar(Integer locationId, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Date picTime, String base64) {
        // 获得所有范围信息
        List<PointRangeBO> point = airPointService.getPoint(locationId);
        return getOneRadar(point,
                startPoint,
                endPoint,
                picTime,
                base64);
    }


    /**
     * 解析一张图片
     *
     * @param point      需要解析的点
     * @param startPoint 图片开始经纬度
     * @param endPoint   图片结束经纬度
     * @param picPath    图片路径
     * @param picTime    图片时间
     * @return
     */
    @Override
    public List<RadarEchoBO> getOneRadar(List<PointRangeBO> point, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, String picPath, Date picTime) {
        BufferedImage image = PicUtil.read(picPath);
        return getOneRadar(point, startPoint, endPoint, picTime, image);
    }

    @Override
    public List<RadarEchoBO> getOneRadar(List<PointRangeBO> point, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Date picTime, BufferedImage image) {
        return point.stream().map(pointRangeBO -> {
            RadarEchoBO radarEchoBO = new RadarEchoBO();
            List<AirPointDO> all = pointRangeBO.getAll();
            // 所有点
            List<PicUtil.LatLng> points = all.stream().map(a -> {
                return new PicUtil.LatLng(a.getLongitude(), a.getLatitude());
            }).collect(Collectors.toList());
            // 处理范围
            Optional.ofNullable(pointRangeBO.getRangeTypeEnum()).filter(o -> {
                return o.equals(RangeTypeEnum.SURFACE);
            }).ifPresent(o -> {
                // 获得获得雷达回波
                List<Double> radarRange = null;
                if (points.size() == 1) {
                    radarRange = radarParse.parsePoint(image, points.get(0), startPoint, endPoint, pointRangeBO.getWide());
                } else {
                    radarRange = radarParse.parseRange(image, points, startPoint, endPoint);
                }
                radarEchoBO.setRadarRange(radarRange);
                radarEchoBO.setName(pointRangeBO.getName());
                radarEchoBO.setLocationInfoId(pointRangeBO.getLocationInfoId());
            });
            // 处理点
            Optional.ofNullable(pointRangeBO.getRangeTypeEnum()).filter(o -> {
                return o.equals(RangeTypeEnum.POINT);
            }).ifPresent(o -> {
                // 获得获得雷达回波
                List<Double> radarRange = radarParse.parsePoint(image, points.get(0), startPoint, endPoint, 2.0);
                radarEchoBO.setRadarRange(radarRange);
                radarEchoBO.setName(pointRangeBO.getName());
                radarEchoBO.setLocationInfoId(pointRangeBO.getLocationInfoId());
            });
            // 处理线
            Optional.ofNullable(pointRangeBO.getRangeTypeEnum()).filter(o -> {
                return o.equals(RangeTypeEnum.LINE);
            }).ifPresent(o -> {
                // 获得获得雷达回波
                List<Double> radarRange = radarParse.parseRoute(
                        image, points, startPoint, endPoint, 2.0, pointRangeBO.getWide());
                radarEchoBO.setRadarRange(radarRange);
                radarEchoBO.setName(pointRangeBO.getName());
                radarEchoBO.setLocationInfoId(pointRangeBO.getLocationInfoId());
            });
            radarEchoBO.setTime(picTime);
            return radarEchoBO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RadarEchoBO> getOneRadar(List<PointRangeBO> point, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Date picTime, String base64) {
        BufferedImage image = PicUtil.readBase64(base64);
        return getOneRadar(point, startPoint, endPoint, picTime, image);
    }


}
