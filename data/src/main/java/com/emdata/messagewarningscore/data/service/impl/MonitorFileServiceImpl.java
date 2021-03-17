package com.emdata.messagewarningscore.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emdata.messagewarningscore.common.cache.LocalCacheClient;
import com.emdata.messagewarningscore.common.common.MonitorFileParam;
import com.emdata.messagewarningscore.common.common.config.FailQueue;
import com.emdata.messagewarningscore.common.common.config.MonitorConfig;
import com.emdata.messagewarningscore.common.common.utils.*;
import com.emdata.messagewarningscore.common.dao.entity.CrPictureDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationDO;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.dao.entity.RadarInfoDO;
import com.emdata.messagewarningscore.common.metar.entity.Airport;
import com.emdata.messagewarningscore.common.service.*;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import com.emdata.messagewarningscore.data.radar.AccessRadarDataService;
import com.emdata.messagewarningscore.data.radar.bo.RadarBO;
import com.emdata.messagewarningscore.data.radar.bo.TimeRangeRadar;
import com.emdata.messagewarningscore.data.radar.handler.RadarHandler;
import com.emdata.messagewarningscore.data.radar.service.RadarService;
import com.emdata.messagewarningscore.data.radar.vo.RadarVO;
import com.emdata.messagewarningscore.data.service.IMetarSourceService;
import com.emdata.messagewarningscore.data.service.MonitorFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.stream.Collectors;

/**
 * @description:
 * @date: 2020/12/29
 * @author: sunming
 */
@Slf4j
@Service
public class MonitorFileServiceImpl implements MonitorFileService {

    @Autowired
    private MonitorConfig monitorConfig;
    @Autowired
    private ISeriousService seriousService;
    @Autowired
    private IRadarInfoService radarInfoService;
    @Autowired
    private RadarService radarService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IAirPointService airPointService;
    @Autowired
    private IMetarSourceService iMetarSourceService;
    @Autowired
    private AccessRadarDataService accessRadarDataService;
    @Autowired
    private CrPictureService crPictureService;
    @Value("${spring.cloud.client.ip-address}")
    private String host;
    @Value("${server.port}")
    private Integer port;

    public Map<Boolean, String> radarHandlerMap = new HashMap<Boolean, String>() {{
        put(true, "TimeRadarHandler");
        put(false, "AnRadarHandler");

    }};


    /**
     * 监控文件夹
     *
     * @param filePath 路径
     * @param event    监控文件的创建，修改，删除
     */
    @Override
    public void monitorFile(String filePath, WatchEvent.Kind<?> event, DelayQueue delayQueue) {
        log.info("监控文件夹:{}", filePath);
        try {
            // 实例化watchService
            WatchService watchService = FileSystems.getDefault().newWatchService();
            // 利用path实例化监控对象watchAble
            Path path = Paths.get(filePath);
            // 将path注册到watchService中
            WatchKey register = path.register(watchService, event);
            // 尝试获取下一个变化信息的监控池，如果没有变化就一直等待 当前
            while ((register = watchService.take()) != null) {
                List<WatchEvent<?>> watchEvents = register.pollEvents();
                for (WatchEvent<?> watchEvent : watchEvents) {
                    WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) watchEvent.kind();
                    if (kind.equals(event)) {
                        WatchEvent<Path> watchEvent1 = (WatchEvent<Path>) watchEvent;
                        Path fileName = watchEvent1.context().getFileName();
                        MonitorFileParam build = MonitorFileParam.build(fileName, 1);
                        delayQueue.add(build);
                    }
                }
                register.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MonitorFileServiceImpl monitorFileService = new MonitorFileServiceImpl();
        monitorFileService.monitorFileNobockAll("D:\\data\\radar", StandardWatchEventKinds.ENTRY_CREATE, null)
        ;
    }

    @Override
    public void monitorFileNobockAll(String filePath, WatchEvent.Kind<?> event, DelayQueue delayQueue) {
        Map<WatchKey, String> watchKeyMap = new ConcurrentHashMap<>();
        log.info("监控文件夹:{}", filePath);
        try {
            // 实例化watchService
            WatchService watchService = FileSystems.getDefault().newWatchService();
            // 利用path实例化监控对象watchAble
            List<String> allDir = getAllDir(filePath, new ArrayList<>());
            allDir.stream().map(s -> {
                try {
                    WatchKey register = Paths.get(s).register(watchService, event);
                    watchKeyMap.put(register, s);
                    return register;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            // 将path注册到watchService中
            for (; ; ) {
                WatchKey poll = watchService.poll();
                if (poll != null) {
                    String s = watchKeyMap.get(poll);
                    List<WatchEvent<?>> watchEvents = poll.pollEvents();
                    for (WatchEvent<?> watchEvent : watchEvents) {
                        WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) watchEvent.kind();
                        if (kind.equals(event)) {
                            WatchEvent<Path> watchEvent1 = (WatchEvent<Path>) watchEvent;
                            String fileName = s + File.separatorChar + watchEvent1.context().getFileName();
                            File file = new File(fileName);
                            if (file.isDirectory()) {
                                log.info("监控到文件夹：{}", file);
                                WatchKey register = Paths.get(fileName).register(watchService, event);
                                watchKeyMap.put(register, fileName);
                            } else {
                                Path path = Paths.get(fileName);
                                log.info("监控到文件：{}", path.toString());
                                MonitorFileParam build = MonitorFileParam.build(path, 1);
                                delayQueue.add(build);
                            }
                        }
                    }
                    poll.reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getAllDir(String filePath, List<String> re) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            re.add(file.getPath());
            File[] files = file.listFiles();
            for (File file1 : files) {
                getAllDir(file1.getPath(), re);
            }
        }
        return re;
    }

    /**
     * metar报入库
     */
    @Override
    public void insertMetar() {
        DelayQueue<MonitorFileParam> queue = FailQueue.DELAYFAILQUEUE;
        for (; ; ) {
            MonitorFileParam poll = queue.poll();
            try {
                if (poll != null) {
                    Path path = poll.getPath();
                    String filePath = path.toString();
                    FileInputStream inputStream = new FileInputStream(filePath);
                    List<String> metarList = FtpUtils.readMessage(inputStream);
                    // 构建do
                    List<MetarSourceDO> metarSourceDOS = metarList.stream().map(a -> {
                        MetarSourceDO metarSourceDO = buildMetarSourceDO(a);
                        if (metarSourceDO != null) {
                            return metarSourceDO;
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
                    // 消费
                    iMetarSourceService.saveBatch(metarSourceDOS);
                    // 检查重要天气
                    metarSourceDOS.stream().map(s -> {
                        seriousService.consumer(s, null);
                        return s;
                    }).collect(Collectors.toList());
                }
            } catch (Exception e) {
                // 重新放入队列
                if (isReSend(poll)) {
                    MonitorFileParam monitorFileParam = reMonitorFileParam(poll);
                    FailQueue.DELAYFAILQUEUE.add(monitorFileParam);
                }
                e.printStackTrace();
            }
        }

    }


    @Override
    public void insartRadar() {
        // 雷达队列
        DelayQueue<MonitorFileParam> queue = FailQueue.RADARQUEUE;
        for (; ; ) {
            MonitorFileParam poll = queue.poll();
            try {
                if (poll != null) {
                    Path path = poll.getPath();
                    log.info("解析雷达数据：{}", path);
                    String filePath = path.toString();
                    // 直接根据文件名 得到机场code 从而查询到雷达数据 然后关联到雷达
                    List<RadarInfoDO> list = radarInfoService.list();
                    Optional<RadarInfoDO> first = list.stream().filter(s -> {
                        if (StringUtils.isNotEmpty(MatchUtil.get(filePath, s.getAirportCode() + "|" + s.getAirportCode().toLowerCase()))) {
                            return true;
                        }
                        return false;
                    }).findFirst();
                    first.ifPresent(s -> {
                        save(filePath, s);
                    });
                }
            } catch (RuntimeException ex) {
                log.error("雷达解析服务出错：{}", ex);
            } catch (Exception e) {
                // 重新放入队列
                if (isReSend(poll)) {
                    MonitorFileParam monitorFileParam = reMonitorFileParam(poll);
                    FailQueue.RADARQUEUE.add(monitorFileParam);
                }
            }
        }

    }

    /**
     * 插入雷达 储存雷达图片 插入雷达图片
     *
     * @param filePath
     * @param radarInfoDO
     */
    private void save(String filePath, RadarInfoDO radarInfoDO) {
        String radarHander = null;
        if (radarInfoDO.getAirportCode().equals("ZSSS")) {
            radarHander = radarHandlerMap.get(true);
        } else {
            radarHander = radarHandlerMap.get(false);
        }
        RadarHandler bean = (RadarHandler) SpringUtil.getBean(radarHander);
        List<List<TimeRangeRadar.TimeAnRangeRadar>> radarSourceData = bean.getRadarSourceData(radarInfoDO.getAirportCode(), radarInfoDO.getDataType(), filePath, Calendar.MINUTE, monitorConfig.getRadarTimeRange());
        log.info("当前处理器：{}", radarSourceData);
        if (radarSourceData != null) {
            radarSourceData.stream().peek(s -> {
                // 转换雷达数据 为雷达可访问
                List<String> filepath_list = s.stream().map(u -> {
                    return u.getHttpUrl(host, port);
                }).collect(Collectors.toList());
                // 图片时间
                Optional<TimeRangeRadar.TimeAnRangeRadar> picTime = s.stream().min(Comparator.comparing(c -> {
                    return c.getUrlTime();
                }));
                RadarVO radarVO = RadarVO.builder().data_type(radarInfoDO.getDataType()).filepath_list(filepath_list).station(radarInfoDO.getAirportCode()).build();
                // 调用接口 返回null 说明异常
                RadarBO radarBO = radarService.analysisRadar(radarVO);
                if (radarBO != null && radarBO.getCode() >= 0) {
                    // 得到解析后的数据
                    RadarBO.RadarData analysisData = radarBO.getData();
                    // 获得一张图片Base64
                    String base64 = analysisData.getCr_imgs().get(0);
                    // 查询到当前雷达包含的范围
                    List<LocationDO> list = locationService.list(new LambdaQueryWrapper<LocationDO>().eq(LocationDO::getRadarInfoId, radarInfoDO.getUuid()));
                    // 结束经纬度
                    List<Double> lonlat_bottom_right = analysisData.getLonlat_bottom_right();
                    // 开始经纬度
                    List<Double> lonlat_top_left = analysisData.getLonlat_top_left();
                    // 圖片時間
                    Date urlTime = picTime.get().getUrlTime();
                    // 插入圖片
                    CrPictureDO one = crPictureService.getOne(new LambdaQueryWrapper<CrPictureDO>().eq(CrPictureDO::getAirportCode, radarInfoDO.getAirportCode()).eq(CrPictureDO::getPicTime, urlTime).eq(CrPictureDO::getRadarUuid, radarInfoDO.getUuid()));
                    if (one == null) {
                        CrPictureDO crPictureDO = accessRadarDataService.saveCrPicture(radarInfoDO.getAirportCode(), base64, radarInfoDO.getUuid(), picTime.get());
                        list.stream().peek((LocationDO Location) -> {
                            // 插入数据
                            accessRadarDataService.saveRadarData(crPictureDO,
                                    new PicUtil.LatLng(lonlat_top_left.get(0),
                                            lonlat_top_left.get(1)),
                                    new PicUtil.LatLng(lonlat_bottom_right.get(0),
                                            lonlat_bottom_right.get(1)),
                                    Location.getId(),
                                    base64);
                        }).collect(Collectors.toList());
                    }
                }
            }).collect(Collectors.toList());
        }
    }


    /**
     * 构建metarSourceDO
     *
     * @param metar
     * @return
     */
    public MetarSourceDO buildMetarSourceDO(String metar) {
        Airport airport = new Airport(metar);
        String airportCode = airport.getAirport();
        if (StringUtils.isEmpty(airportCode) || !LocalCacheClient.cacheList.contains(airportCode)) {
            return null;
        }
        String metarTime = JudgeUtil.getMessageTime(metar, "yyyyMMdd HH:mm:ss", "metar");
        log.info("metar:{}", metar);
        MetarSourceDO metarSourceDO = new MetarSourceDO();
        metarSourceDO.setUuid(Guid.newGUID());
        metarSourceDO.setMetarSource(metar);
        metarSourceDO.setMetarTime(Java8DateUtils.parseDateTimeStr(metarTime, Java8DateUtils.FULL_PATTERN_1));
        metarSourceDO.setAirportCode(airportCode);
        return metarSourceDO;
    }

    /**
     * 是否重新发送
     *
     * @param monitorFileParam
     * @return
     */
    private boolean isReSend(MonitorFileParam monitorFileParam) {
        int num = monitorFileParam.getNum();
        if (num < monitorConfig.getThisQueueRetrtCount()) {
            return true;
        }
        log.info("当前重试次数为：{} 超过最大重试次数丢弃该消息：{}", num, monitorFileParam);
        return false;
    }

    /**
     * 重置属性
     *
     * @param monitorFileParam
     * @return
     */
    private MonitorFileParam reMonitorFileParam(MonitorFileParam monitorFileParam) {
        monitorFileParam.setNum(monitorFileParam.getNum() + 1);
        monitorFileParam.setTime(System.currentTimeMillis());
        return monitorFileParam;
    }


}
