package com.emdata.messagewarningscore.common.accuracy.config;/**
 * Created by zhangshaohu on 2020/12/30.
 */

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/30
 * 起评阈值：中（阵）雨、大（阵）雨（各地根据
 * 用户服务协议确定是否评定，上报备案）。
 * @description: 中度以上阈值 配置文件
 */
@Data
public class RaConfig extends AirportCode{
    /**
     * ）起评阈值：中（阵）雨、大（阵）雨（各地根据
     * 用户服务协议确定是否评定，上报备案）
     */
    private List<String> ra = new ArrayList<String>() {{
        add("RA");
        add("+RA");
        add("SHRA");
        add("+SHRA");
        add("TSRA");
        add("+TSRA");
    }};
}