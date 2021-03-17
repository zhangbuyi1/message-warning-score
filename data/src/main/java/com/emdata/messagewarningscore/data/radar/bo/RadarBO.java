package com.emdata.messagewarningscore.data.radar.bo;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import lombok.Data;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description: 雷达数据返回对象
 */
@Data
public class RadarBO {
    @Data
    public class RadarData {
        /**
         * 图片  图片是一个Base64
         */
        private List<String> cr_imgs;
        /**
         * 开始经纬度
         */
        private List<Double> lonlat_bottom_right;
        /**
         * 结束经纬度
         */
        private List<Double> lonlat_top_left;
    }

    /**
     * response code
     */
    private Integer code;
    /**
     * response 数据
     */
    private RadarData data;
    /**
     * response 提示
     */
    private String msg;
}