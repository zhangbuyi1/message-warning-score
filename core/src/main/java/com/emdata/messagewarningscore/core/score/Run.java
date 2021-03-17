package com.emdata.messagewarningscore.core.score;/**
 * Created by zhangshaohu on 2021/1/4.
 */


import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.common.utils.JudgeUtil;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.common.service.IAlertContentService;
import com.emdata.messagewarningscore.common.service.ISeriousService;
import com.emdata.messagewarningscore.core.score.handler.DiscriminatingHandlerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description: 评分类
 */
@Service
public class Run {
    @Resource
    private ISeriousService seriousService;
    @Resource
    private IAlertContentService alertContentService;
    @Resource(name = "DiscriminatingHandler")
    private DiscriminatingHandlerService discriminatingHandlerService;


    /**
     * 空报 命中判断
     */
    public void run() {
        /**
         * 处理机场警报
         */
        List<AlertContentResolveDO> alertContentResolveDOS = alertContentService.selectIsScoreAlertContent(WarningTypeEnum.AIRPORT_WARNING, Calendar.HOUR_OF_DAY, 2);
        // 处理机场警报空报
        discriminatingHandlerService.runSaveHitOrEmptyScore(alertContentResolveDOS, WarningTypeEnum.AIRPORT_WARNING, NatureEnum.EMPTY);
        // 处理机场警报命中
        discriminatingHandlerService.runSaveHitOrEmptyScore(alertContentResolveDOS, WarningTypeEnum.AIRPORT_WARNING, NatureEnum.HIT);
        /**
         * 处理mdrs
         */
        List<AlertContentResolveDO> mdrs = alertContentService.selectIsScoreAlertContent(WarningTypeEnum.MDRS_WARNING, Calendar.HOUR_OF_DAY, 2);
        // 处理Mdrs命中
        discriminatingHandlerService.runSaveHitOrEmptyScore(mdrs, WarningTypeEnum.MDRS_WARNING, NatureEnum.HIT);
        // 处理Mdrs空报
        discriminatingHandlerService.runSaveHitOrEmptyScore(mdrs, WarningTypeEnum.MDRS_WARNING, NatureEnum.EMPTY);
        /**
         * 处理终端区
         */
        List<AlertContentResolveDO> ts = alertContentService.selectIsScoreAlertContent(WarningTypeEnum.TERMINAL_WARNING, Calendar.HOUR_OF_DAY, 2);
        // 处理终端区空报
        discriminatingHandlerService.runSaveHitOrEmptyScore(ts, WarningTypeEnum.TERMINAL_WARNING, NatureEnum.EMPTY);
        //处理终端区命中
        discriminatingHandlerService.runSaveHitOrEmptyScore(ts, WarningTypeEnum.TERMINAL_WARNING, NatureEnum.HIT);
    }

    /**
     * 漏报判断
     */
    public void runLeak() {
        List<SeriousDO> airport = seriousService.selectIsScore(Calendar.HOUR_OF_DAY, 2);
        // 获得雷暴对流
        List<SeriousDO> mergeTsAndRa = getMergeTsAndRa(airport);
        // 获得除开雷暴对流其他
        List<SeriousDO> otherSerious = getOtherSerious(airport, mergeTsAndRa);
        // 合并俩个
        otherSerious.addAll(mergeTsAndRa);
        // 机场警报
        discriminatingHandlerService.runSaveLeakScore(WarningTypeEnum.AIRPORT_WARNING, otherSerious, NatureEnum.LEAK);
        // mdrs警报
        discriminatingHandlerService.runSaveLeakScore(WarningTypeEnum.MDRS_WARNING, otherSerious, NatureEnum.LEAK);
        discriminatingHandlerService.runSaveLeakScore(WarningTypeEnum.MDRS_WARNING, otherSerious, NatureEnum.NOT_SCORE);
        // 终端区警报
        discriminatingHandlerService.runSaveLeakScore(WarningTypeEnum.TERMINAL_WARNING, mergeTsAndRa, NatureEnum.LEAK);
    }

    /**
     * @param live    实况所有重要天气
     * @param tsAndRa 雷暴与中度以上降水的重要天气
     * @return
     */
    private List<SeriousDO> getOtherSerious(List<SeriousDO> live, List<SeriousDO> tsAndRa) {
        Optional<SeriousDO> endTime = tsAndRa.stream().max(Comparator.comparing(s -> {
            return s.getEndTime();
        }));
        Optional<SeriousDO> startTime = tsAndRa.stream().min(Comparator.comparing(s -> {
            return s.getStartTime();
        }));
        List<SeriousDO> collect = live.stream().filter(s -> {
            if (s.getSeriousWeather().equals(EvaluationWeather.LOW_VIS_LOWCLOUD) || s.getSeriousWeather().equals(EvaluationWeather.RN_FZRA)) {
                return true;
            } else {
                if (startTime.get().getStartTime().getTime() > s.getStartTime().getTime() && endTime.get().getEndTime().getTime() < s.getEndTime().getTime()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 返回雷暴与对流的结合
     *
     * @param live 实况报文
     * @return
     */
    private List<SeriousDO> getMergeTsAndRa(List<SeriousDO> live) {
        // 把降雨加雷暴的报文与大于35dbz的时段合并为对流
        List<SeriousDO> ts = live.stream().filter(s -> {
            return s.getSeriousWeather().equals(EvaluationWeather.TS);
        }).collect(Collectors.toList());

        /**
         * 将雷暴对流的时间与中到大雨合并
         */
        List<SeriousDO> mergeTsAndRa = ts.stream().map(s -> {
            List<SeriousDO> seriousDOS = getSeriousDOS(live, JudgeUtil.add(s.getStartTime(), Calendar.HOUR_OF_DAY, -2), JudgeUtil.add(s.getEndTime(), Calendar.HOUR_OF_DAY, 2));
            return mergeTs(live, s, seriousDOS);
        }).collect(Collectors.toList());
        return mergeTsAndRa;
    }

    /**
     * 这里有一个阈值 小雨与雷暴相差多少小时可以合并 现在默认写死2小时
     *
     * @param all
     * @param ts
     * @return
     */
    private SeriousDO mergeTs(List<SeriousDO> all, SeriousDO ts, List<SeriousDO> isTsRa) {
        Optional<SeriousDO> start = isTsRa.stream().sorted(Comparator.comparing(s -> {
            return s.getStartTime();
        })).reduce((s1, s2) -> {
            if (s1.getStartTime().getTime() < s2.getStartTime().getTime()) {
                return s1;
            } else {
                return s2;
            }
        });
        Optional<SeriousDO> end = isTsRa.stream().sorted(Comparator.comparing(s -> {
            return s.getEndTime();
        })).reduce((s1, s2) -> {
            if (s1.getEndTime().getTime() > s2.getEndTime().getTime()) {
                return s1;
            } else {
                return s2;
            }
        });
        start.ifPresent(s -> {
            ts.setStartTime(s.getStartTime());
        });
        end.ifPresent(s -> {
            ts.setEndTime(s.getEndTime());
        });
        return ts;
    }

    private List<SeriousDO> getSeriousDOS(List<SeriousDO> all, Date startTime, Date endTime) {
        // 得到所有时刻降雨
        return all.stream().filter(s -> {
            if (s.getSeriousWeather().equals(EvaluationWeather.RA)) {
                // 如果有降雨 有35dbz以上的雷达回波 他们的时间间隔小于来小时 那么就就使用合集时间 算雷达回波
                if (isTimeSection(startTime, endTime, s.getStartTime(), s.getEndTime())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 判断两个时间段是否交叉
     *
     * @param startTime     第一个时间段的开始时间
     * @param endTime       第一个时间段的结束时间
     * @param startNextTime 第二个时间段的开始时间
     * @param endNextTime   第二个时间段的结束时间
     * @return
     */
    private boolean isTimeSection(Date startTime, Date endTime, Date startNextTime, Date endNextTime) {
        return isTimeSection(startTime, endTime, startNextTime) || isTimeSection(startTime, endTime, endNextTime);
    }

    /**
     * 判断一个时间是否在当前时间段内
     *
     * @param startTime 时间段开始时间
     * @param endTime   时间段结束时间
     * @param time      需要验证的时间
     * @return
     */
    private boolean isTimeSection(Date startTime, Date endTime, Date time) {
        if (startTime.getTime() <= time.getTime() && endTime.getTime() >= time.getTime()) {
            return true;
        }
        return false;
    }
}