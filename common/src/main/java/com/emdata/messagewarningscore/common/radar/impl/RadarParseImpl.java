package com.emdata.messagewarningscore.common.radar.impl;/**
 * Created by zhangshaohu on 2021/1/5.
 */

import com.emdata.messagewarningscore.common.common.utils.Grayscale;
import com.emdata.messagewarningscore.common.common.utils.PicUtil;
import com.emdata.messagewarningscore.common.radar.RadarParse;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/5
 * @description: 得到雷达图片范围内所有的反射率
 */
@Service
public class RadarParseImpl implements RadarParse {
    /**
     * 雷达图片解析方法
     *
     * @param bufferedImage 雷达图片
     * @param latLngs       范围内经纬度
     * @param startLatLng   雷达图片左上角的经纬度
     * @param endLatLng     雷达图片右下角经纬度
     * @return
     */
    @Override
    public List<Double> parseRange(BufferedImage bufferedImage, List<PicUtil.LatLng> latLngs, PicUtil.LatLng startLatLng, PicUtil.LatLng endLatLng) {
        // 将经纬度转为像素点
        List<Point> pixels = PicUtil.LaLongitudeToPixels(latLngs, startLatLng, endLatLng, bufferedImage);
        // 得到所有像素点
        List<Point> allPoint = PicUtil.findAllPoint(pixels, bufferedImage);
        // 将灰度图像素点 转为雷达反射率 *255-127.5
        List<Double> collect = getRadarReflectivity(bufferedImage, allPoint);
        return collect;
    }

    @Override
    public List<Double> parsePoint(BufferedImage bufferedImage, PicUtil.LatLng points, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Double radius) {
        List<Point> circlePointPixel = PicUtil.findCirclePointPixel(points, startPoint, endPoint, bufferedImage, radius);
        List<Double> collect = getRadarReflectivity(bufferedImage, circlePointPixel);
        return collect;
    }


    @Override
    public List<Double> parseRoute(BufferedImage bufferedImage, List<PicUtil.LatLng> points, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Double radius, Double routeWide) {
        /**
         * 得到航线点
         */
        List<Point> linePointPixel = PicUtil.findLinePointPixel(points, startPoint, endPoint, bufferedImage, routeWide);
        points.stream().map(s -> {
            List<Point> circlePointPixel = PicUtil.findCirclePointPixel(s, startPoint, endPoint, bufferedImage, radius);
            linePointPixel.addAll(circlePointPixel);
            return circlePointPixel;
        }).collect(Collectors.toList());
        /**
         * 获得像素点
         */
        return getRadarReflectivity(bufferedImage, linePointPixel);
    }



    /**
     * 获得雷达反射率
     *
     * @param bufferedImage    雷达灰度图
     * @param circlePointPixel 需要获得的像素点
     * @return
     */
    private List<Double> getRadarReflectivity(BufferedImage bufferedImage, List<Point> circlePointPixel) {
        // 将灰度图像素点 转为雷达反射率 *255-127.5
        Grayscale grayscale = new Grayscale(bufferedImage);
        return circlePointPixel.stream().map(s -> {
            // 当前getRGB有个坑 8位的灰度图getRGB是错误的
            Integer r = grayscale.getDbz(s.x, s.y);
            return r.doubleValue();
        }).collect(Collectors.toList());
    }
}