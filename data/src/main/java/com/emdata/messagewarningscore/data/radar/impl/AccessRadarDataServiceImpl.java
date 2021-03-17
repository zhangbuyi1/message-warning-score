package com.emdata.messagewarningscore.data.radar.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.emdata.messagewarningscore.common.common.config.MonitorConfig;
import com.emdata.messagewarningscore.common.common.utils.Guid;
import com.emdata.messagewarningscore.common.common.utils.PicUtil;
import com.emdata.messagewarningscore.common.dao.entity.*;
import com.emdata.messagewarningscore.common.service.CrPictureService;
import com.emdata.messagewarningscore.common.service.ILocationService;
import com.emdata.messagewarningscore.common.service.IRadarDataService;
import com.emdata.messagewarningscore.common.service.IRadarInfoService;
import com.emdata.messagewarningscore.common.service.bo.RadarEchoBO;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import com.emdata.messagewarningscore.data.radar.AccessRadarDataService;
import com.emdata.messagewarningscore.data.radar.bo.TimeRangeRadar;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description:
 * @date: 2021/1/15
 * @author: sunming
 */
@Service
public class AccessRadarDataServiceImpl implements AccessRadarDataService {

    @Autowired
    private IRadarInfoService iRadarInfoService;
    @Autowired
    private CrPictureService crPictureService;
    @Autowired
    private IRadarDataService iRadarDataService;
    @Autowired
    private ILocationService iLocationService;
    @Autowired
    private MonitorConfig monitorConfig;


    /**
     * 保存雷达数据
     *
     * @param airportCode 机场代码
     * @param file        文件
     */
    @Override
    public void saveRadarData(String airportCode, File file) {
        List<LocationDO> locationDOS = iLocationService.findByAirportCode(airportCode);
        CrPictureDO crPictureDO = packageCrPicture(airportCode, file);
        crPictureService.save(crPictureDO);
        RadarDataDO radarDataDO = packageRadarData(locationDOS.get(0).getId(), crPictureDO);
        iRadarDataService.save(radarDataDO);
    }

    @Override
    public CrPictureDO saveCrPicture(String airportCode, String base64, String radarUuid, TimeRangeRadar.TimeAnRangeRadar timeAnRangeRadar) {
        BufferedImage image = PicUtil.readBase64(base64);
        String url = timeAnRangeRadar.getUrl();
        String radarPath = monitorConfig.getRadarPath();
        String picPath = url.replace(radarPath, monitorConfig.getRadarPicPath()) + ".jpg";
        File file = new File(picPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        PicUtil.writeImage(image, picPath);
        String crPicUuid = Guid.newGUID();
        CrPictureDO crPictureDO = CrPictureDO.builder().airportCode(airportCode).picPath(picPath).picTime(timeAnRangeRadar.getUrlTime()).radarUuid(radarUuid).uuid(crPicUuid).build();
        crPictureService.save(crPictureDO);
        return crPictureDO;
    }

    @Override
    public void saveRadarData(CrPictureDO crPictureDO, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Integer locationId, String base64) {
        RadarDataDO radarDataDO = packageRadarData(locationId, startPoint, endPoint, crPictureDO, base64);
        iRadarDataService.save(radarDataDO);
    }

    @Override
    public void saveRadarAndPic(String airportCode, String base64, String radarUuid, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Integer locationId, TimeRangeRadar.TimeAnRangeRadar timeAnRangeRadar) {
        CrPictureDO crPictureDO = saveCrPicture(airportCode, base64, radarUuid, timeAnRangeRadar);
        //String airportCode, String base64, Date picTime, String radarUuid, String picName
        saveRadarData(crPictureDO, startPoint, endPoint, locationId, base64);
    }


    /**
     * 封装crPictureDO
     *
     * @param airportCode 机场code
     * @param file        文件
     * @return 雷达图片do
     */
    @Override
    public CrPictureDO packageCrPicture(String airportCode, File file) {
        CrPictureDO crPictureDO = CrPictureDO.builder().build();
        RadarInfoDO radarInfoDO = iRadarInfoService.findByAirportCode(airportCode);
        Optional<RadarInfoDO> infoDOOptional = Optional.ofNullable(radarInfoDO);
        String radarInfoUuid = infoDOOptional.map(RadarInfoDO::getUuid).orElse("");
        crPictureDO.setUuid(Guid.newGUID());
        crPictureDO.setAirportCode(airportCode);
        crPictureDO.setRadarUuid(radarInfoUuid);
        crPictureDO.setPicPath(file.getPath());
        crPictureDO.setColorPicPath("");
        String s1 = "20" + MatchUtil.get(file.getPath(), "\\d{10}");
        DateTime parse = DateUtil.parse(s1, "yyyyMMddHHmm");
        crPictureDO.setPicTime(parse);
        return crPictureDO;
    }

    /**
     * 封装radarDataDO
     *
     * @param locationId  位置id
     * @param crPictureDO 雷达图片do
     * @return 雷达数据do
     */
    @Override
    public RadarDataDO packageRadarData(Integer locationId, CrPictureDO crPictureDO) {
        List<RadarEchoBO> radarEchoBOS = crPictureService.getOneRadar(locationId, crPictureDO.getUuid());
        RadarDataDO radarDataDO = RadarDataDO.builder().build();
        List<RadarEchoDO> radarEchoDOS = extractRadar(radarEchoBOS);
        radarDataDO.setData(radarEchoDOS);
        radarDataDO.setPicId(crPictureDO.getUuid());
        radarDataDO.setLocationId(locationId);
        radarDataDO.setPicTime(crPictureDO.getPicTime());
        return radarDataDO;
    }

    public RadarDataDO packageRadarData(Integer locationId, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, CrPictureDO crPictureDO, String base64) {
        List<RadarEchoBO> radarEchoBOS = crPictureService.getOneRadar(locationId, startPoint, endPoint, crPictureDO.getPicTime(), base64);
        List<RadarEchoDO> radarEchoDOS = extractRadar(radarEchoBOS);
        return RadarDataDO.builder().data(radarEchoDOS).picId(crPictureDO.getUuid()).picTime(crPictureDO.getPicTime()).locationId(locationId).build();
    }

    private List<RadarEchoDO> extractRadar(List<RadarEchoBO> radarEchoBOS) {
        return radarEchoBOS.stream().map((RadarEchoBO cs) -> {
            RadarEchoDO radarEchoDO = new RadarEchoDO();
            BeanUtils.copyProperties(cs, radarEchoDO);
            List<Double> radarRange = cs.getRadarRange();
            List<Double> collect2 = radarRange.stream().filter(cs1 -> {
                return cs1 > 35;
            }).collect(Collectors.toList());
            double v = radarRange.size() != 0 ? (collect2.size() * 1.0) / radarRange.size() * 100.0 : 0.0;
            radarEchoDO.setRadarPercentage(new Double(v * 100000).intValue() / 100000.0);
            return radarEchoDO;
        }).collect(Collectors.toList());

    }
}
