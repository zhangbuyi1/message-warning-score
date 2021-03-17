package com.emdata.messagewarningscore.data.schedule;/**
 * Created by zhangshaohu on 2020/8/6.
 */

import com.emdata.messagewarningscore.common.common.config.MonitorConfig;
import com.emdata.messagewarningscore.common.common.utils.JudgeUtil;
import com.emdata.messagewarningscore.common.common.utils.MyFTP;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import com.emdata.messagewarningscore.data.transfer.RadarEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/8/6
 * @description:
 */
@Slf4j
@Component
public class PullFTPTask {
    @Resource
    private MonitorConfig monitorConfig;

    /**
     * 拉取metar报文与taf报文数据
     * 小飞象拉取数据拉取可能出现问题
     */
    @Async
    @Scheduled(cron = "0 0/5 * * * ? ")
    public synchronized void pullMetarSource() {
        log.info("开始拉取metar报文");
        long start = System.currentTimeMillis();
        MyFTP myFTP = new MyFTP("192.168.90.10", 21, "ykftp", "em2019", "/cac", "/data/metar");
        List<String> pull = myFTP.pull(s -> {
            String name = s.getName();
            //M120210203024610825
            String dateName = MatchUtil.get(name, "M1\\d{8}");
            if (StringUtils.isNotEmpty(dateName)) {
                String replaceM1 = dateName.replace("M1", "");
                String yyyyAndMon = replaceM1.substring(0, 6);
                String day = replaceM1.substring(6, 8);
                return yyyyAndMon + "/" + day;
            }
            return "other";
        });
        myFTP.logout();
        log.info("拉取metar报文完毕:{}毫秒 拉取文件个数：{}", System.currentTimeMillis() - start, pull.size());
    }

    public static void main(String[] args) {
        PullFTPTask pullFTPTask = new PullFTPTask();
        pullFTPTask.pullMetarSource();
    }

    /**
     * @param ftpPath
     * @param airPortCode
     */
    public void pullZsam(String ftpPath, String airPortCode) {
        RadarEnum zsam = RadarEnum.get(airPortCode);
        synchronized (zsam) {
            MyFTP myFTP = new MyFTP("192.168.90.10", 21, "ykftp", "em2019", ftpPath, monitorConfig.getRadarPath() + "/" + airPortCode);
            List<String> pull = myFTP.pull(s -> {
                String name = s.getName();
                System.out.println(name);
                Date date = zsam.getDataFun().apply(name, zsam);
                if (date != null) {
                    Calendar calendar = JudgeUtil.getCalendar(date);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    int mon = calendar.get(Calendar.MONTH);
                    String dayFormat = String.format("%02d", day);
                    String yearFormat = String.format("%04d", year);
                    String monthFormat = String.format("%02d", mon);
                    return yearFormat + monthFormat + "/" + dayFormat;
                }
                return "/other";
            });
        }
    }


    @Async
    @Scheduled(cron = "0 0/2 * * * ? ")
    public synchronized void pullRadarData() {
        log.info("开始拉取雷达数据");
        long start = System.currentTimeMillis();
        MyFTP myFTP = new MyFTP("192.168.90.10", 21, "ykftp", "em2019", "/zsss", monitorConfig.getRadarPath() + "/" + "ZSSS");
        List<String> pull = myFTP.pull(s -> {
            String name = s.getName();
            String yyMM = MatchUtil.get(name, "\\d{6}");
            String yyyy = "20" + yyMM.substring(0, 2);
            String mm = yyMM.substring(2, 4);
            String dd = yyMM.substring(4, 6);
            return yyyy + mm + "/" + dd;
        });
        myFTP.logout();
        log.info("雷达数据拉取完毕：耗时{}毫秒 拉取文件个数：{}拉取文件：{}", System.currentTimeMillis() - start, pull.size(), pull);
    }


}