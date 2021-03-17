package com.emdata.messagewarningscore.data.radar.bo;
/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import com.emdata.messagewarningscore.data.transfer.RadarEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@Slf4j

public class TimeRangeRadar {


    /**
     * 所有文件
     */
    private List<TimeAnRangeRadar> timeAnRangeRadars = new ArrayList<>();
    // 多少时间内的雷达数据可以生成一张图片
    /**
     * 时间单位
     */
    private int filed = Calendar.MINUTE;
    /**
     * 数值
     */
    private int num = 5;

    public List<TimeAnRangeRadar> getTimeAnRangeRadars() {
        return timeAnRangeRadars;
    }

    @Data
    public class TimeAnRangeRadar {
        private Boolean first;
        /**
         * 地址
         */
        private String url;
        /**
         * 时间
         */
        private Date urlTime;

        private Date thisDate;
        /**
         * code ZSSS
         */
        private String code;

        public TimeAnRangeRadar() {

        }

        public String getName() {
            File file = new File(url);
            return file.getName();
        }

        // 原始文件
        public String getHttpUrl(String host, Integer port) {
            return "http://" + host + ":" + port + this.url;
        }

        public TimeAnRangeRadar(String url, Date urlTime, String code) {
            this.thisDate = new Date();
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date(0));
            instance.set(filed, num);
            this.url = url;
            this.urlTime = urlTime;
            this.first = urlTime.getTime() % instance.getTimeInMillis() == 0;
            this.code = code;
        }

    }

    /**
     * 消费当前
     *
     * @return
     */
    public synchronized List<List<TimeAnRangeRadar>> consumerAn() {
        Map<String, List<TimeAnRangeRadar>> stringListMapGroup = this.timeAnRangeRadars.stream().collect(Collectors.groupingBy(s -> {
            return s.code;
        }));
        return stringListMapGroup.entrySet().stream().map(s -> {
            List<TimeAnRangeRadar> value = s.getValue();
            if (value.size() >= 2) {
                List<TimeAnRangeRadar> collect = value.stream().sorted(Comparator.comparing(c -> {
                    return c.getThisDate();
                })).limit(value.size() - 1).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(collect)) {
                    this.timeAnRangeRadars.removeAll(collect);
                    return collect;
                }
            }
            return null;
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());

    }

    /**
     * 消费当前
     * 加一个锁 避免多线程 多次消费
     *
     * @return
     */
    public synchronized List<List<TimeAnRangeRadar>> consumer() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(0));
        instance.set(filed, num);
        long timeInMillis = instance.getTimeInMillis();
        Map<String, List<TimeAnRangeRadar>> collect = this.timeAnRangeRadars.stream().collect(Collectors.groupingBy(s -> {
            return s.code;
        }));
        for (Map.Entry<String, List<TimeAnRangeRadar>> stringListEntry : collect.entrySet()) {
            List<List<TimeAnRangeRadar>> collect1 = getLists(timeInMillis, stringListEntry.getValue());
            if (collect1 != null) {
                return collect1;
            }
        }
        return null;
    }

    private List<List<TimeAnRangeRadar>> getLists(long timeInMillis, List<TimeAnRangeRadar> timeAnRange) {
        //  得到头
        List<TimeAnRangeRadar> timeAnRangeRadars = timeAnRange.stream().filter(s -> {
            return s.first;
        }).collect(Collectors.toList());
        long thisTimeMillis = System.currentTimeMillis();
        // 获取范围内的数据
        List<List<TimeAnRangeRadar>> collect = timeAnRangeRadars.stream().map(s -> {
            // 查找范围内所有图片
            List<TimeAnRangeRadar> collect1 = timeAnRange.stream().filter(t -> {
                return t.urlTime.getTime() >= s.urlTime.getTime() && t.urlTime.getTime() - s.urlTime.getTime() < timeInMillis;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect1) && collect1.size() >= 2) {
                return collect1;
            }
            return null;
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());

        // 如何已经收取到规定时间内的所有数据  当前使用 下一个开始时间来临 证明上一个时间段结束
        List<List<TimeAnRangeRadar>> sort = collect.stream().sorted(Comparator.comparing(s -> {
            return s.get(0).urlTime;
        })).collect(Collectors.toList());
        // 如果当前数据大于2个
        if (sort.size() >= 2) {
            List<List<TimeAnRangeRadar>> collect1 = sort.stream().limit(sort.size() - 1).collect(Collectors.toList());
            for (List<TimeAnRangeRadar> anRangeRadars : collect1) {
                for (TimeAnRangeRadar anRangeRadar : anRangeRadars) {
                    this.timeAnRangeRadars.remove(anRangeRadar);
                }
            }
            return collect1;
        }
        return null;
    }

    /**
     * 构造器
     *
     * @param filed
     * @param num
     */
    public TimeRangeRadar(int filed, int num) {
        this.filed = filed;
        this.num = num;
    }

    /**
     * 一个url的构造器
     *
     * @param url
     */
    public TimeRangeRadar(String url) {
        TimeAnRangeRadar timeAnRangeRadar = extractTimeAnRangeRadar(url);
        if (timeAnRangeRadar != null) {
            this.timeAnRangeRadars.add(timeAnRangeRadar);
        } else {
            log.error("当前文件不符合规则：{}", url);
        }
    }

    /**
     * 一群url的构造器
     *
     * @param url
     */
    public TimeRangeRadar(List<String> url, int filed, int num) {
        this.timeAnRangeRadars.addAll(url.stream().map(s -> {
            return extractTimeAnRangeRadar(s);
        }).collect(Collectors.toList()));
        this.filed = filed;
        this.num = num;
    }

    /**
     * 添加一个文件
     *
     * @param url
     */
    public void add(String url) {
        // 超过3天的数据  自动清除
        this.timeAnRangeRadars.add(extractTimeAnRangeRadar(url));
    }

    /**
     * 清理废弃数据
     */
    public void filterScrapData() {
        long thiscTime = System.currentTimeMillis();
        this.timeAnRangeRadars = this.timeAnRangeRadars.stream().filter(s -> {
            return thiscTime - s.getUrlTime().getTime() < 1000 * 60 * 60 * 24 * 10;
        }).collect(Collectors.toList());
    }

    /**
     * 根据url构建一个子对象
     *
     * @param url
     * @return
     */
    public TimeAnRangeRadar extractTimeAnRangeRadar(String url) {
        Optional<RadarEnum> first = Arrays.stream(RadarEnum.values()).filter(c -> {
            String airportCode = c.getAirportCode();
            if (StringUtils.isNotBlank(MatchUtil.get(url, airportCode))) {
                return true;
            }
            return false;
        }).findFirst();
        if (first.isPresent()) {
            RadarEnum radarEnum = first.get();
            File file = new File(url);
            Date apply = radarEnum.getDataFun().apply(file.getName(), radarEnum);
            if (apply != null) {
                return new TimeAnRangeRadar(url, apply, radarEnum.getAirportCode());
            }
        }
        return null;

    }

}