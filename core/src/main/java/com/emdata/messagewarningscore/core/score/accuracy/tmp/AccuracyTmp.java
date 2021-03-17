package com.emdata.messagewarningscore.core.score.accuracy.tmp;

import com.emdata.messagewarningscore.core.score.accuracy.Accuracy;
import com.emdata.messagewarningscore.common.accuracy.entity.AccuracyEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description: 机场 终端区
 */
@Service("AccuracyTmp")
public class AccuracyTmp extends AccuracyEntity implements Accuracy {
    public AccuracyTmp(Date startForcastTime, Date endForcastTime, Date startLiveTime, Date endLiveTime, Date sendTime, String warningNature) {
        super(startForcastTime, endForcastTime, startLiveTime, endLiveTime, sendTime, warningNature);
    }

    public AccuracyTmp() {

    }

    /**
     * 提前量L
     * 在重要天气发生之前发布的预警，仅评定开始提前量；
     * 在重要天气发生之后发布的预警（除首份），仅评定结束提前量。
     * 首份预警始终仅评定开始提前量，不论在重要天气发生之前或之后发布。
     *
     * @return 提前量
     */
    @Override
    public Double leadTime() {
        if ("update".equals(this.warningNature) && this.startLiveTime.getTime() < this.sendTime.getTime()) {
            return endLeadTime();
        } else {
            return startLeadTime();
        }
    }


    /**
     * 开始提前量
     * 是指预警的发布时间与重要天气
     * 实际开始时间的差值（单位：分钟，min）。发布时间早于
     * 实况开始时间为正。
     * L=实况开始时间-预警发布时间
     *
     * @return 开始提前量
     */
    private Double startLeadTime() {
        // 实况开始时间
        Date startLiveTime = this.startLiveTime;
        // 发布时间
        Date sendTime = this.sendTime;
        long diff = startLiveTime.getTime() - sendTime.getTime();
        Double minute = diff / 1000.0 / 60;
        return minute;
    }

    /**
     * 结束提前量
     * 是指预警的发布时间与重要天气
     * 实际结束时间的差值（单位：分钟，min）。发布时间早于
     * 实况结束时间为正。
     * L=实况结束时间-预警发布时间
     *
     * @return 结束提前量
     */
    private Double endLeadTime() {
        // 发布时间
        Date sendTime = this.sendTime;
        // 实况结束时间
        Date endLiveTime = this.endLiveTime;
        long diff = endLiveTime.getTime() - sendTime.getTime();
        Double minute = diff / 1000.0 / 60;
        return minute;
    }


    /**
     * 偏差量D
     * 指预计重要天气的开始和终止的时间与实际天气开始和终止的时间二者差值（分钟数，min）的绝对值平均。
     * 分为天气发生前和天气发生后
     *
     * @return 偏差量
     */
    @Override
    public Double deviation() {
        if (this.sendTime.getTime() < this.startLiveTime.getTime()) {
            return beforeWeather();
        } else if (this.sendTime.getTime() > this.startLiveTime.getTime()) {
            return afterWeather();
        }
        return null;
    }

    /**
     * 天气发生前
     * D=（|实况开始时间-预报开始时间|+|实况结束时间-预报结束时间|）/2
     *
     * @return 偏差量
     */
    private Double beforeWeather() {
        Double a = Math.abs(this.startLiveTime.getTime() - this.startForcastTime.getTime()) / 1000.0 / 60;
        Double b = Math.abs(this.endLiveTime.getTime() - this.endForcastTime.getTime()) / 1000.0 / 60;
        return (a + b) / 2;
    }

    /**
     * 天气发生后
     * D=|实况结束时间-预报结束时间|
     *
     * @return 偏差量
     */
    private Double afterWeather() {
        return Math.abs(this.endLiveTime.getTime() - this.endForcastTime.getTime()) / 1000.0 / 60;
    }

    /**
     * 重合率
     * 指实际重要天气出现的时段和预计出现时段的交集与两者的并集之比
     *
     * @return 重合率
     */
    @Override
    public Double coincidenceRate() {
        List<Date> dates = new ArrayList<>();
        dates.add(this.startForcastTime);
        dates.add(this.endForcastTime);
        dates.add(this.startLiveTime);
        dates.add(this.endLiveTime);
        Collections.sort(dates);
        // 并集
        double union = Math.abs(dates.get(dates.size() - 1).getTime() - dates.get(0).getTime()) / 1000.0 / 60 + 1;
        // 交集
        double mix = Math.abs(dates.get(dates.size() - 2).getTime() - dates.get(1).getTime()) / 1000.0 / 60 + 1;
        // 保留两位小数
        BigDecimal bigDecimal = new BigDecimal(mix / union).setScale(4, RoundingMode.UP);
        return bigDecimal.doubleValue();
    }

    /**
     * 提前量L的得分ls
     * 其中 L 为提前量，取值范围[-30,120]，单位为：min。
     * L>120min 时的 Ls 与 L=120min 时相同。
     *
     * @return 分数
     */
    @Override
    public Double getLs() {
        int leadI = 0;
        // 得到提前量
        Double lead = leadTime();
        if (lead >= -30 && lead <= 120) {
            leadI = lead.intValue();
        } else if (lead > 120) {
            leadI = 120;
        }
        double ls = 100 / (1 + 23.1316 * Math.pow(Math.E, -0.0803 * leadI));
        // 保留两位小数
        BigDecimal bigDecimal = new BigDecimal(ls).setScale(2, RoundingMode.UP);
        return bigDecimal.doubleValue();
    }

    /**
     * 提前量的权重系数wl
     * L 为提前量，取值范围[-30,120]，单位为：min
     *
     * @return 提前量的权重系数
     */
    @Override
    public Double getWl() {
        Double wl = null;
        Double leadTime = leadTime();
        int leadI = leadTime.intValue();
        if (leadI >= -30 && leadI < 45) {
            wl = (Math.cos((leadI + 31) / 24.1916) + 1) / 2 * 0.09 + 0.45;
        } else if (leadI >= 45 && leadI <= 120) {
            wl = (Math.cos((leadI + 31) / 24.1916) + 1) / 2 * 0.01 + 0.45;
        }
        return wl;
    }

    /**
     * 偏差量得分ds
     * 其中 D 为偏差量，取值范围[0,120]，单位为：min。偏差量得分情况如图4，D>120min时的Ds与D=120min时相同
     *
     * @return 得分ds
     */
    @Override
    public Double getDs() {
        Double deviation = deviation();
        int devI = 0;
        if (deviation >= 0 && deviation <= 120) {
            devI = deviation.intValue();
        } else if (deviation > 120) {
            devI = 120;
        }
        double ds = Math.pow(0.65, Math.pow(devI / 30.0, 2)) * 100;
        // 保留两位小数
        BigDecimal bigDecimal = new BigDecimal(ds).setScale(2, RoundingMode.UP);
        return bigDecimal.doubleValue();
    }

    /**
     * 提前量与偏差量的综合质量得分LDQ
     * LDQ = ls*wl+ds*(1-wl)
     *
     * @return 综合质量得分LDQ
     */
    @Override
    public Double getLdq() {
//        Double ls = getLs();
//        Double wl = getWl();
//        Double ds = getDs();
//        double ldq = ls * wl + ds * (1 - wl);
//        BigDecimal bigDecimal = new BigDecimal(ldq).setScale(2, RoundingMode.UP);
//        return bigDecimal.doubleValue();
        return null;
    }

    /**
     * 预报成功质量得分
     * 预报成功质量得分=0.9*LDQ+0.1*重合率 C 得分+附加分
     *
     * @return
     */
    @Override
    public Double getForeSuccessQualityScore(Double foreScore) {
        Double ldq = getLdq();
        Double coincidenceRate = coincidenceRate();
        double score = ldq * 0.9 + 0.1 * coincidenceRate + foreScore;
        BigDecimal bigDecimal = new BigDecimal(score).setScale(2, RoundingMode.UP);
        return bigDecimal.doubleValue();
    }
}