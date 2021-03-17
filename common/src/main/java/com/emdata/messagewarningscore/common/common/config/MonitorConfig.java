package com.emdata.messagewarningscore.common.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @date: 2020/12/10
 * @author: sunming
 */
@Component
@ConfigurationProperties(prefix = "monitor")
@Data
public class MonitorConfig {
    /**
     * 监控的文件夹路径
     */
    private String filePath;
    /**
     * 监控雷达数据path
     */
    private String radarPath;
    /**
     * 雷达原始数据时间范围
     */
    private Integer radarTimeRange;
    /**
     * 雷达图片位置
     */
    private String radarPicPath;
    /**
     * 是否时候时间范围原始图片 生成雷达图片
     */
    private Boolean radarIsTimeRange;
    /**
     * 雷达对应的机场code；
     */
    private String radarAirportCode;

    /**
     * 本地发送到远端mq重试次数
     */
    private Integer thisQueueRetrtCount;
}
