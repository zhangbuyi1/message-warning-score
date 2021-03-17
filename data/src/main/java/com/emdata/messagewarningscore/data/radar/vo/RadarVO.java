package com.emdata.messagewarningscore.data.radar.vo;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description: 雷达数据请求对象
 */
@Data
@Builder
public class RadarVO {

    /**
     * 机场
     */
    private String station;
    /**
     * 雷达数据类型
     */
    private String data_type;
    /**
     * 数据地址
     */
    private List<String> filepath_list;



}