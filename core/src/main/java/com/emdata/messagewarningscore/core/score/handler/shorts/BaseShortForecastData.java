package com.emdata.messagewarningscore.core.score.handler.shorts;/**
 * Created by zhangshaohu on 2021/1/27.
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.WeatherType;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.core.score.handler.ShortForecastData;
import com.emdata.messagewarningscore.data.service.IMetarSourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/27
 * @description:
 */
@Service("BaseShortForecastData")
public class BaseShortForecastData implements ShortForecastData {
    @Resource
    private IMetarSourceService metarSourceService;

    @Override
    public double shortScoreHandler(ForcastData forcastData) {
        ForcastWeather shortScore = isShortScore(forcastData);
        String code = shortScore.getCode();
        /**
         * 开始时间
         */
        Date startTime = forcastData.getStartTime();
        Date endTime = forcastData.getEndTime();
        String airportCode = forcastData.getAirportCode();
        List<Metar> metars = metarSourceService.getMetarSourceByTime(airportCode, startTime, endTime);
        if (code.equals(WeatherType.CLOUD.getCode())) {
            return shortScore(metars, s -> {
                Integer cloudHeight = s.getCloud().getLowCloud().getCloudHeight();
                if (cloudHeight <= shortScore.getMax() && cloudHeight >= shortScore.getMin()) {
                    return true;
                }
                return false;
            }, startTime, endTime);
        }
        if (code.equals(WeatherType.VIS.getCode())) {
            return shortScore(metars, s -> {
                if (s.getVis().getVis() <= shortScore.getMax() && s.getVis().getVis() >= shortScore.getMin()) {
                    return true;
                }
                return false;
            }, startTime, endTime);

        }
        if (code.equals(WeatherType.RVR.getCode())) {
            return shortScore(metars, s -> {
                if (s.getRvr().getMinRvrVis() <= shortScore.getMax() && s.getRvr().getMinRvrVis() >= shortScore.getMin()) {
                    return true;
                }
                return false;
            }, startTime, endTime);
        }
        return 0;
    }

    public <T extends Metar> double shortScore(List<T> metars, Predicate<T> predicate, Date startTime, Date endTime) {
        List<T> collect = metars.stream().filter(s -> {
            return predicate.test(s);
        }).collect(Collectors.toList());
        /**
         * 如果实况出现则得到百分之15的分
         */
        if (CollectionUtils.isNotEmpty(collect)) {
            if (isShortRule(collect, metars, startTime, endTime)) {
                return 100;
            } else {
                return 50;
            }
        }
        return 0;
    }


    /**
     * 是否满足短时规则
     *
     * @param filter 所有出现的实况报文
     * @param <T>
     * @return
     */
    public <T extends Metar> boolean isShortRule(List<T> filter, List<T> all, Date startTime, Date endTime) {
        // 判断出现的时间是否是总时长的一半
        long[] allTiem = {0};
        // 持续时长
        long[] maxTime = {0};
        // 是否超过一小时
        boolean[] isExceed = {false};
        all.stream().map(s -> {
            return s.getMetarTime();
        }).sorted(Date::compareTo).reduce((start, next) -> {
            Optional<T> first = filter.stream().filter(s -> {
                return s.getMetarTime().getTime() == start.getTime();
            }).findFirst();
            if (first.isPresent()) {
                long num = next.getTime() - start.getTime();
                allTiem[0] += num;
                maxTime[0] += num;
                // 判断每次出现时长是否超过一小时
                if (maxTime[0] > 1000 * 60 * 60) {
                    isExceed[0] = true;
                }
            } else {
                // 持续断开
                maxTime[0] = 0;
            }
            return next;
        });
        // 默认如果最后一个小时出现 那么出现的时长就是最后一个小时到总时段的结束时间
        filter.stream().max(Comparator.comparing(s -> {
            return s.getMetarTime();
        })).ifPresent(s -> {
            all.stream().max(Comparator.comparing(a -> {
                return a.getMetarTime();
            })).ifPresent(a -> {
                if (a.getMetarTime().getTime() == s.getMetarTime().getTime()) {
                    allTiem[0] += endTime.getTime() - s.getMetarTime().getTime();
                }
            });
        });
        // 判断出现的时间是否是总时长的一半
        if ((endTime.getTime() - startTime.getTime()) / 2 > allTiem[0]) {
            // 判断每次出现时长是否超过一小时
            if (isExceed[0]) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public ForcastWeather isShortScore(ForcastData forcastData) {
        // 如果当前报的是低云低能见度  并且短时有数据  则是可参与短时评分
        if (forcastData.getEvaluationWeather().equals(EvaluationWeather.LOW_VIS_LOWCLOUD)) {
            Optional<ForcastWeather> first = forcastData.getMainWeather().stream().filter(s -> {
                Integer shortMax = s.getShortMax();
                Integer shortMin = s.getShortMin();
                if (shortMax != null || shortMin != null) {
                    return true;
                }
                return false;
            }).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return null;
    }
}