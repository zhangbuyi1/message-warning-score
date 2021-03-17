package com.emdata.messagewarningscore.core.score.handler.extras;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.emdata.messagewarningscore.common.accuracy.config.RaSnConfig;
import com.emdata.messagewarningscore.common.accuracy.config.TSConfig;
import com.emdata.messagewarningscore.common.accuracy.config.ThresholdConfig;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.accuracy.enums.WeatherType;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.core.score.handler.ExtrasSocoreService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description:
 */
@Service("ExtrasSocore")
public class ExtrasSocore implements ExtrasSocoreService {

    /**
     * 雷暴伴随天气：大风（≥15m/s（包括阵风））、强降水（+TSRA、+RA、+SHRA）、冰雹（GR 或 GS）、龙卷（FC）、低云低能见度等天气。
     * 若预报了雷暴的某一类或几类伴随天气，预报时段内实
     * 际至少出现一类所预报的天气，则该份预报成功质量得分加
     * 5 分，若预报的伴随天气在预报时段外前后 2h 内均未出现，
     * 则该份预报成功质量得分扣 2 分。评定依据为本场天气报告
     *
     * @param forcastData 预测数据
     * @return
     */
    @Override
    public Double run(ForcastData forcastData, List<Metar> metarSourceByTime) {
        final Double[] tsAccompanyScore = {0.0};
        TSConfig tsConfig = ThresholdConfig.getTsConfig(forcastData.getAirportCode());
        RaSnConfig raSnConfig = ThresholdConfig.getRaSnConfig(forcastData.getAirportCode());
        // 雷暴附加分
        Optional.ofNullable(forcastData.getEvaluationWeather()).filter(s -> {
            return s.equals(EvaluationWeather.TS);
        }).ifPresent(s -> {
            // 雷暴附加分
            tsAccompanyScore[0] = accompanyScore(tsConfig, metarSourceByTime, forcastData, this::forecastTsExist, this::liveTsExist);
        });
        // 降雪冻降水附加分
        Optional.ofNullable(forcastData.getEvaluationWeather()).filter(s -> {
            return s.equals(EvaluationWeather.RN_FZRA);
        }).ifPresent(s -> {
            tsAccompanyScore[0] = accompanyScore(raSnConfig, metarSourceByTime, forcastData, this::forecastRaSnExist, this::liveRaSnExist);
        });
        return tsAccompanyScore[0];
    }

    /**
     * 若预报了雷暴的某一类或几类伴随天气，预报时段内实
     * 际至少出现一类所预报的天气，则该份预报成功质量得分加
     * 5 分，
     *
     * @param config
     * @param metarSourceByTime
     * @param forcastData
     * @return
     */
    private <T> Double accompanyScore(T config, List<Metar> metarSourceByTime, ForcastData forcastData,
                                      BiFunction<T, ForcastWeather, Boolean> forecastIsExist,
                                      BiFunction<T, Metar, Boolean> liveIsExist) {
        final Double[] accompanyScore = {0.0};
        // 预测天气
        List<ForcastWeather> mainWeather = forcastData.getMainWeather();
        List<ForcastWeather> accompanyWeathers = forcastData.getAccompanyWeathers();
        accompanyWeathers.addAll(mainWeather);
        /**
         * 若预报了雷暴的某一类或几类伴随天气，预报时段内实
         * 际至少出现一类所预报的天气，则该份预报成功质量得分加
         * 5 分，
         */
        accompanyWeathers.stream().filter(s -> {
            return forecastIsExist.apply(config, s);
        }).findFirst().ifPresent(s -> {
            metarSourceByTime.stream().filter(m -> {
                return forcastData.getStartTime().getTime() <= m.getMetarTime().getTime() && m.getMetarTime().getTime() <= forcastData.getEndTime().getTime();
            }).filter(m -> {
                return liveIsExist.apply(config, m);
            }).findFirst().ifPresent(m -> {
                accompanyScore[0] = 5.0;
            });
        });
        /**
         * 若预报的伴随天气在预报时段外前后 2h 内均未出现，
         * 则该份预报成功质量得分扣 2 分。
         * 评定依据为本场天气报告。
         */
        if (accompanyScore[0].equals(0.0)) {
            metarSourceByTime.stream().filter(m -> {
                return liveIsExist.apply(config, m);
            }).findFirst().ifPresent(c -> {
                accompanyScore[0] = -2.0;
            });
        }
        return accompanyScore[0];
    }

    /**
     * 预测是否出现伴随天气现象
     *
     * @param tsConfig       雷暴配置文件
     * @param forcastWeather 预测天气现象
     * @return
     */
    private Boolean forecastTsExist(TSConfig tsConfig, ForcastWeather forcastWeather) {
        final Boolean[] forecastExist = {false};

        Optional.ofNullable(forcastWeather).ifPresent(s -> {
            // 大风（≥15m/s（包括阵风））
            Optional.ofNullable(s.getCode()).filter(c -> {
                return c.equals(WeatherType.WIND.getCode()) && Optional.ofNullable(forcastWeather.getMin()).orElse(0) >= tsConfig.getAccompanyWindSpeed();
            }).ifPresent(c -> {
                forecastExist[0] = true;
            });
            //、强降水（+TSRA、+RA、+SHRA）、冰雹（GR 或 GS）、龙卷（FC）、
            Optional.ofNullable(s.getCode()).filter(c -> {
                return tsConfig.getAccompanyWeather().contains(c);
            }).ifPresent(c -> {
                forecastExist[0] = true;
            });
            // 低能见度
            Optional.ofNullable(s.getCode()).filter(c -> {
                return c.equals(WeatherType.VIS.getCode()) && Optional.ofNullable(forcastWeather.getMin()).orElse(9999) < tsConfig.getAccompanyVis();
            }).ifPresent(c -> {
                forecastExist[0] = true;
            });
            // 低云
            Optional.ofNullable(s.getCode()).filter(c -> {
                return c.equals(WeatherType.CLOUD.getCode()) && Optional.ofNullable(forcastWeather.getMin()).orElse(9999) < tsConfig.getAccompanyCloudHeight();
            }).ifPresent(c -> {
                forecastExist[0] = true;
            });
        });
        return forecastExist[0];
    }

    /**
     * 实况
     *
     * @param raSnConfig
     * @param live
     * @return
     */
    private Boolean liveRaSnExist(RaSnConfig raSnConfig, Metar live) {
        final Boolean[] liveExist = {false};
        Optional.ofNullable(live).filter(s -> {
            Arrays.stream(live.getWeatherDO().getWeather().split(" ")).filter(c -> {
                return raSnConfig.getAccompanyWeather().contains(c);
            }).findFirst().ifPresent(m -> {
                        liveExist[0] = true;
                    }
            );
            return liveExist[0];
        });
        return liveExist[0];
    }

    /**
     * 预测
     *
     * @param raSnConfig
     * @param forcastWeather
     * @return
     */
    private Boolean forecastRaSnExist(RaSnConfig raSnConfig, ForcastWeather forcastWeather) {
        final Boolean[] forecastExist = {false};
        Optional.ofNullable(forcastWeather).filter(s -> {
            return raSnConfig.getAccompanyWeather().contains(Optional.ofNullable(s.getCode()).orElse("NA"));
        }).ifPresent(s -> {
            forecastExist[0] = true;
        });
        return forecastExist[0];
    }

    /**
     * 预报时段内实
     * 际至少出现一类所预报的天气，则该份预报成功质量得分加
     * 5 分
     *
     * @param tsConfig
     * @param metar
     * @return
     */
    private Boolean liveTsExist(TSConfig tsConfig, Metar metar) {
        final Boolean[] liveExist = {false};
        /**
         * 、强降水（+TSRA、+RA、+SHRA）、冰雹（GR 或 GS）、龙卷（FC）、
         *
         */
        Arrays.stream(metar.getWeatherDO().getWeather().split(" ")).filter(s -> {
            return tsConfig.getAccompanyWeather().contains(s);
        }).findFirst().ifPresent(s -> {
            liveExist[0] = true;
        });
        /**
         * 大风（≥15m/s（包括阵风））
         */
        Optional.ofNullable(metar.getWindDO().getWindSpeed()).filter(s -> {
            return s >= tsConfig.getAccompanyWindSpeed();
        }).ifPresent(s -> {
            liveExist[0] = true;
        });
        /**
         * 低能见度
         */
        Optional.ofNullable(metar.getVis()).filter(s -> {
            return s.getVis() < tsConfig.getAccompanyVis();
        }).ifPresent(s -> {
            liveExist[0] = true;
        });
        /**
         * 低云
         */
        Optional.ofNullable(metar.getCloud()).filter(s -> {
            return s.getLowCloud().getCloudHeight() < tsConfig.getAccompanyCloudHeight();
        }).ifPresent(s -> {
            liveExist[0] = true;
        });
        return liveExist[0];
    }
}