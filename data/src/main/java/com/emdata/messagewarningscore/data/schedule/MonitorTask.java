package com.emdata.messagewarningscore.data.schedule;

import com.emdata.messagewarningscore.common.cache.LocalCacheClient;
import com.emdata.messagewarningscore.common.common.config.FailQueue;
import com.emdata.messagewarningscore.common.common.config.MonitorConfig;
import com.emdata.messagewarningscore.common.dao.entity.AirportDO;
import com.emdata.messagewarningscore.common.service.IAirportService;
import com.emdata.messagewarningscore.data.service.MonitorFileService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.StandardWatchEventKinds;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description: 接入metar报任务
 * @date: 2020/12/29
 * @author: sunming
 */
@Component
@Slf4j
public class MonitorTask {

    @Autowired
    private IAirportService iAirportService;


    /**
     * 初始化线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS
            , new LinkedBlockingDeque<>(1000), new ThreadFactoryBuilder().setNameFormat("thread").build());

    @Autowired
    private MonitorFileService monitorFileService;

    @Autowired
    private MonitorConfig monitorConfig;

    /**
     * 监控文件夹
     */
    @PostConstruct
    public void monitorFile() {
        init();
        String filePath = monitorConfig.getFilePath();
        CompletableFuture.runAsync(() -> {
            monitorFileService.monitorFileNobockAll(filePath, StandardWatchEventKinds.ENTRY_CREATE, FailQueue.DELAYFAILQUEUE);
        }, threadPoolExecutor);
    }

    @PostConstruct
    public void monitorRadarFile() {
        String radarPath = monitorConfig.getRadarPath();
        CompletableFuture.runAsync(() -> {
            monitorFileService.monitorFileNobockAll(radarPath, StandardWatchEventKinds.ENTRY_CREATE, FailQueue.RADARQUEUE);
        }, threadPoolExecutor);
    }

    /**
     * metar报入库
     */
    @PostConstruct
    public void insertMetar() {
        log.info("metar报入库...");
        CompletableFuture.runAsync(() -> {
            monitorFileService.insertMetar();
        }, threadPoolExecutor);
    }

    @PostConstruct
    public void insartRadar() {
        log.info("雷达数据启动...");
        CompletableFuture.runAsync(() -> {
            monitorFileService.insartRadar();
        }, threadPoolExecutor);
    }

    /**
     * 将所有机场code放入list中
     */
    public void init() {
        List<AirportDO> airportDOS = iAirportService.list();
        List<String> airportCodes = airportDOS.stream().map(AirportDO::getAirportCode).collect(Collectors.toList());
        LocalCacheClient.cacheList.addAll(airportCodes);
    }

}
