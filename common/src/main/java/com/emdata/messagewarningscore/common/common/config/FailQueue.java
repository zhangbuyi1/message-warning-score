package com.emdata.messagewarningscore.common.common.config;/**
 * Created by zhangshaohu on 2020/9/23.
 */


import com.emdata.messagewarningscore.common.common.MonitorFileParam;

import java.util.concurrent.DelayQueue;

/**
 * @author: zhangshaohu
 * @date: 2020/9/23
 * @description: 全局发送失败mq队列
 */
public class FailQueue {
    /**
     * metar报文延迟入库队列
     */
    public static DelayQueue<MonitorFileParam> DELAYFAILQUEUE = new DelayQueue<>();

    /**
     * 雷达数据
     */
    public static DelayQueue<MonitorFileParam> RADARQUEUE = new DelayQueue<>();
}