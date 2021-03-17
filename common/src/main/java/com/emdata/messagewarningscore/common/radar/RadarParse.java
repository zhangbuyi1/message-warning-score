package com.emdata.messagewarningscore.common.radar;/**
 * Created by zhangshaohu on 2021/1/5.
 */

import com.emdata.messagewarningscore.common.common.utils.PicUtil;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/5
 * @description: 雷达图片解析方法
 */
public interface RadarParse {
    /**
     * 雷达图片解析方法
     *
     * @param bufferedImage 雷达图片
     * @param points        范围内经纬度
     * @param startPoint    雷达图片左上角的经纬度
     * @param endPoint      雷达图片右下角经纬度
     * @return
     */
    List<Double> parseRange(BufferedImage bufferedImage, List<PicUtil.LatLng> points, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint);

    /**
     * 雷达图片解析方法
     * 获得航点 半径n的圆形范围内所有的雷达反射率
     *
     * @param bufferedImage 雷达图片
     * @param points        航点
     * @param startPoint    雷达图片开始经纬度
     * @param endPoint      雷达图片结束经纬度
     * @param radius        半径 半径单位为公里
     * @return
     */
    List<Double> parsePoint(BufferedImage bufferedImage, PicUtil.LatLng points, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Double radius);

    /**
     * 得到雷达图片
     * 标记的航线航点的雷达反射率值
     *
     * @param bufferedImage 雷达图片
     * @param points        航线点 最少传2个点  2点才能构建一条直线
     * @param startPoint    雷达图片开始经纬度
     * @param endPoint      雷达图片结束经纬度
     * @param radius        航点半径
     * @param routeWide     航线宽度
     * @return
     */
    List<Double> parseRoute(BufferedImage bufferedImage, List<PicUtil.LatLng> points, PicUtil.LatLng startPoint, PicUtil.LatLng endPoint, Double radius, Double routeWide);

}