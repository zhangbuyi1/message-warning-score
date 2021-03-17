package com.emdata.messagewarningscore.data.service;

import java.nio.file.WatchEvent;
import java.util.concurrent.DelayQueue;

/**
 * @description:
 * @date: 2020/12/29
 * @author: sunming
 */
public interface MonitorFileService {

    /**
     * 监控文件夹
     *
     * @param filePath
     * @param event
     */
    void monitorFile(String filePath, WatchEvent.Kind<?> event, DelayQueue delayQueue);

    /**
     * 非阻塞
     * 监控文件下所有文件的创建
     *
     * @param filePath
     * @param event
     * @param delayQueue
     */
    void monitorFileNobockAll(String filePath, WatchEvent.Kind<?> event, DelayQueue delayQueue);

    /**
     * metar报入库
     */
    void insertMetar();

    void insartRadar();
}
