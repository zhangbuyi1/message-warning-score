//package com.emdata.messagewarningscore.core;
//
//import cn.hutool.core.date.DateTime;
//import cn.hutool.core.date.DateUtil;
//import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
//import com.emdata.messagewarningscore.common.accuracy.enums.WarningNature;
//import com.emdata.messagewarningscore.common.common.utils.Guid;
//import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
//import com.emdata.messagewarningscore.common.common.utils.PicUtil;
//import com.emdata.messagewarningscore.common.dao.entity.CrPictureDO;
//import com.emdata.messagewarningscore.common.dao.entity.RadarDataDO;
//import com.emdata.messagewarningscore.common.dao.entity.RadarEchoDO;
//import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
//import com.emdata.messagewarningscore.common.service.*;
//import com.emdata.messagewarningscore.common.service.bo.PointRangeBO;
//import com.emdata.messagewarningscore.common.service.bo.RadarEchoBO;
//import com.emdata.messagewarningscore.common.service.bo.SeriousBO;
//import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
//import com.emdata.messagewarningscore.core.score.Run;
//import com.emdata.messagewarningscore.core.score.accuracy.Accuracy;
//import com.emdata.messagewarningscore.core.score.accuracy.tmp.AccuracyTmp;
//import com.emdata.messagewarningscore.data.parse.interfase.ParseMdrsInterface;
//import com.emdata.messagewarningscore.data.radar.AccessRadarDataService;
//import com.emdata.messagewarningscore.data.radar.bo.RadarBO;
//import com.emdata.messagewarningscore.data.radar.service.RadarService;
//import com.emdata.messagewarningscore.data.radar.vo.RadarVO;
//import com.emdata.messagewarningscore.data.schedule.PullFTPTask;
//import com.emdata.messagewarningscore.data.service.IMetarSourceService;
//import com.emdata.messagewarningscore.data.service.impl.MonitorFileServiceImpl;
//import com.emdata.messagewarningscore.data.transfer.Transfer;
//import net.sf.json.JSONObject;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.util.*;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class CoreApplicationTests {
//    @Autowired
//    private ISeriousService seriousMapper;
//    @Autowired
//    private IMetarSourceService metarSourceService;
//    @Autowired
//    private MonitorFileServiceImpl monitorFileService;
//    @Autowired
//    private Run run;
//    @Autowired
//    private CrPictureService crPictureService;
//    @Resource
//    private IAirPointService airPointService;
//    @Autowired
//    private IRadarDataService radarDataService;
//    @Autowired
//    private RadarService radarService;
//
//    @Autowired
//    private AccessRadarDataService accessRadarDataService;
//
//
//
//    @Autowired
//    private ParseMdrsInterface parseMdrsInterface;
//    @Autowired
//    private IAlertContentService iAlertContentService;
//    @Autowired
//    private Accuracy accuracy;
//    @Autowired
//    private PullFTPTask pullRadarData;
//    @Autowired
//    private Transfer transfer;
//
//    @Test
//    public void test() {
//        transfer.transfer("D:\\radar", "D:\\data\\radar");
//    }
//
//    @Test
//    public void pull() {
//        pullRadarData.pullRadarData();
//
//    }
//
//    @Test
//    public void test221() {
//        run.run();
//
//    }
//
//    @Test
//    public void radarTest() {
//        List<SeriousDO> list = seriousMapper.list();
//        System.out.println(list);
//
//    }
//
//    @Test
//    public void test12() {
//        RadarVO radarVO = RadarVO.builder().build();
//        radarVO.setData_type("RAW_VOL");
//        radarVO.setStation("ZSJN");
//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("http://192.168.70.70:8510/radar/test/ZSJN/QZSJNBVT200815175454.010");
//        radarVO.setFilepath_list(strings);
//        RadarBO radarBO = radarService.analysisRadar(radarVO);
//        System.out.println(radarBO);
//        String s = radarService.radarSourceExist();
//        System.out.println(s);
//        //
//    }
//
//
//    @Test
//    public void test1() {
//        run.run();
//
//    }
//
//    @Test
//    public void example1() {
//        Date sendTime = Java8DateUtils.parseDateTimeStr("2021-01-18 05:05:00", Java8DateUtils.DATE_TIME);
//        Date metarStartTime = Java8DateUtils.parseDateTimeStr("2021-01-18 06:40:00", Java8DateUtils.DATE_TIME);
//        Date metarEndTime = Java8DateUtils.parseDateTimeStr("2021-01-18 08:20:00", Java8DateUtils.DATE_TIME);
//        Date preStartTime = Java8DateUtils.parseDateTimeStr("2021-01-18 06:00:00", Java8DateUtils.DATE_TIME);
//        Date preEndTime = Java8DateUtils.parseDateTimeStr("2021-01-18 08:00:00", Java8DateUtils.DATE_TIME);
//        AccuracyTmp accuracyTmp = new AccuracyTmp(preStartTime, preEndTime, metarStartTime
//                , metarEndTime, sendTime
//                , WarningNature.FIRST.getValue());
//        System.out.println("提前量--" + accuracyTmp.leadTime());
//        System.out.println("偏差量--" + accuracyTmp.deviation());
//        System.out.println("重合率--" + accuracyTmp.coincidenceRate());
//    }
//
//    @Test
//    public void example2() {
//        Date sendTime = Java8DateUtils.parseDateTimeStr("2021-01-18 07:25:00", Java8DateUtils.DATE_TIME);
//        Date metarStartTime = Java8DateUtils.parseDateTimeStr("2021-01-18 06:40:00", Java8DateUtils.DATE_TIME);
//        Date metarEndTime = Java8DateUtils.parseDateTimeStr("2021-01-18 08:20:00", Java8DateUtils.DATE_TIME);
//        Date preStartTime = Java8DateUtils.parseDateTimeStr("2021-01-18 07:30:00", Java8DateUtils.DATE_TIME);
//        Date preEndTime = Java8DateUtils.parseDateTimeStr("2021-01-18 08:30:00", Java8DateUtils.DATE_TIME);
//        AccuracyTmp accuracyTmp = new AccuracyTmp(preStartTime, preEndTime, metarStartTime
//                , metarEndTime, sendTime
//                , WarningNature.UPDATE.getValue());
//        System.out.println("提前量--" + accuracyTmp.leadTime());
//        System.out.println("偏差量--" + accuracyTmp.deviation());
//        System.out.println("重合率--" + accuracyTmp.coincidenceRate());
//    }
//
////    @Test
////    public void mdrs() {
////        String filePath = "D:\\PWSMR9E07.151.doc";
////        File file = new File(filePath);
////        FileInputStream inputStream = null;
////        try {
////            inputStream = new FileInputStream(file);
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        }
////        List<AlertContentResolveDO> alertContentResolveDOS = parseMdrsInterface.parseMdrsText(inputStream);
////        iAlertContentService.saveBatch(alertContentResolveDOS);
//////        System.out.println();
////    }
//
//    @Test
//    public void ce() {
//        SeriousDO seriousDO = new SeriousDO();
//        seriousDO.setAirportCode("ZSPD");
//        seriousDO.setStartTime(new Date());
//        seriousDO.setEndTime(new Date());
//        seriousDO.setSeriousWeather(EvaluationWeather.TS);
//        seriousMapper.save(seriousDO);
//        List<SeriousDO> list = seriousMapper.list();
//        System.out.println(list);
//    }
//
//    @Test
//    public void metarSerious() {
//        Calendar instance = Calendar.getInstance();
//        instance.setTime(new Date(new Date().getTime() - 1000 * 60 * 60 * 80L));
//        Date time = instance.getTime();
//        List<SeriousBO> zspd = metarSourceService.getMetarSourceToSerious("ZSLG", time, new Date(), true);
//        System.out.println(zspd);
//    }
//
//    @Test
//    public void readRadar() {
//
//        List<File> list = list("D:\\radar", new ArrayList<>());
////D:\radar\20210114\2101140003
//        final int[] i = {0};
//        List<CrPictureDO> collect = list.stream().map((File s) -> {
//            CrPictureDO crPictureDO = CrPictureDO.builder().build();
//            String path = s.getPath();
//            crPictureDO.setPicPath(path);
//            crPictureDO.setRadarUuid("1");
//            String picUuid = Guid.newGUID();
//            crPictureDO.setUuid(picUuid);
//            String s1 = "20" + MatchUtil.get(path, "\\d{10}");
//            DateTime parse = DateUtil.parse(s1, "yyyyMMddHHmm");
//            System.out.println(i[0]++);
//            crPictureDO.setPicTime(parse);
//            boolean save = crPictureService.save(crPictureDO);
//            System.out.println(save);
//            List<RadarEchoBO> oneRadar = crPictureService.getOneRadar(3, picUuid);
//            RadarDataDO radarDataDO = RadarDataDO.builder().build();
//            List<RadarEchoDO> collect1 = oneRadar.stream().map((RadarEchoBO cs) -> {
//                RadarEchoDO radarEchoDO = new RadarEchoDO();
//                BeanUtils.copyProperties(cs, radarEchoDO);
//                List<Double> radarRange = cs.getRadarRange();
//                List<Double> collect2 = radarRange.stream().filter(cs1 -> {
//                    return cs1 > 35;
//                }).collect(Collectors.toList());
//                if (collect2.size() > 0) {
//                    int a = 0;
//                }
//                Double v = radarRange.size() != 0 ? (collect2.size() * 1.0) / radarRange.size() * 100.0 : 0.0;
//                radarEchoDO.setRadarPercentage(new Double(v * 100000).intValue() / 100000.0);
//                return radarEchoDO;
//            }).collect(Collectors.toList());
//            radarDataDO.setData(collect1);
//            radarDataDO.setPicId(picUuid);
//            radarDataDO.setLocationId(3);
//            radarDataDO.setPicTime(parse);
//            radarDataService.save(radarDataDO);
//            return crPictureDO;
//        }).collect(Collectors.toList());
//        for (File file : list) {
//            accessRadarDataService.saveRadarData("", file);
//        }
//
//    }
//
//    public static List<File> list(String path, List<File> files) {
//        File file = new File(path);
//        if (file.isFile()) {
//            files.add(file);
//        }
//        if (file.isDirectory()) {
//            File[] files1 = file.listFiles();
//            for (File file1 : files1) {
//                list(path + "/" + file1.getName(), files);
//            }
//        }
//        return files;
//    }
//
//    @Test
//    public void ees() throws IOException {
//        // 获得所有范围信息
//        List<PointRangeBO> point = airPointService.getPoint(3);
//        List<PicUtil.LatLng> collect = point.get(0).getAll().stream().map(s -> {
//            return new PicUtil.LatLng(s.getLongitude(), s.getLatitude());
//        }).collect(Collectors.toList());
//        BufferedImage image = ImageIO.read(new File("D:\\radar\\20210114\\2101140003\\0000.png"));
//        List<Point> points = PicUtil.LaLongitudeToPixels(collect, new PicUtil.LatLng(124.012678, 33.457045),
//                new PicUtil.LatLng(118.628416, 28.965465)
//                , image);
//        // 验证是否正确 所有可以将当前图片输出
//        BufferedImage bi = PicUtil.sketch(points, image.getWidth(), image.getHeight());
//        ImageIO.write(bi, "JPEG", new FileOutputStream("./a.jpg"));
//    }
//
//    @Test
//    public void ts() {
//        Calendar instance = Calendar.getInstance();
//        instance.setTime(new Date(new Date().getTime() - 1000 * 60 * 60 * 80L));
//        Date time = instance.getTime();
//        long l = System.currentTimeMillis();
//        List<RadarEchoBO> radarEcho = crPictureService.getRadarEcho(3, time, new Date());
//        List<Double> collect = radarEcho.stream().flatMap(s -> {
//            return s.getRadarRange().stream().filter(c -> {
//                return c > 35;
//            });
//        }).collect(Collectors.toList());
//        System.out.println(System.currentTimeMillis() - l);
//        System.out.println(radarEcho);
//        System.out.println(collect);
//
//    }
//
//    @Test
//    public void c() {
//        //        File file = new File("C:\\Users\\sunming\\Desktop\\airport_alert.json");
//        File file = new File("C:\\Users\\sunming\\Desktop\\terminal_zone.json");
//        StringBuilder sb = new StringBuilder();
//        String content = null;
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            while ((content = reader.readLine()) != null) {
//                sb.append(content);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String jsonStr = sb.toString();
//
//        Map map2 = JSONObject.fromObject(jsonStr);
//        Set set = map2.keySet();
//        //定义迭代器，迭代输出
//        Iterator ite = set.iterator();
//        List<List<Double>> all = new ArrayList<>();
//        while (ite.hasNext()) {
//            //取出一个字符串对象
//            String key = (String) ite.next();
//            //转为json格式
//            Map map3 = JSONObject.fromObject(map2.get(key));
//            Set set1 = map3.keySet();
//            Iterator ite1 = set1.iterator();
//            List<Double> list = new ArrayList<>();
//            while (ite1.hasNext()) {
//                String key1 = (String) ite1.next();
//                Double value = (Double) map3.get(key1);
//                // list.addAll(value);
//            }
//            all.add(list);
//        }
//        StringBuilder builder = new StringBuilder();
//        builder.append("[{");
//
//        for (int i = 0; i < all.size(); i++) {
//            List<Double> list = all.get(i);
//            for (int j = 0; j < list.size(); j++) {
//                builder.append(list.get(j));
//                if (j != list.size() - 1) {
//                    builder.append(",");
//                }
//            }
//            if (i != all.size() - 1) {
//                builder.append(";");
//            }
//        }
//
//        builder.append("}]");
//        String s = builder.toString();
//        System.out.println(s);
//    }
//
//
//    @Test
//    public void d() {
//        run.runLeak();
//    }
//}
