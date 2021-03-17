package com.emdata.messagewarningscore.core.score.accuracy.tmp;

import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.core.score.accuracy.MdrsAccuracy;
import com.emdata.messagewarningscore.common.accuracy.entity.AccuracyEntity;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description:
 */
@Service("MdrsAccuracyTmp")
public class MdrsAccuracyTmp extends AccuracyEntity implements MdrsAccuracy {

    public void setAccuracyEntity(AccuracyEntity accuracyEntity) {
        BeanUtils.copyProperties(accuracyEntity, this);

    }

    /**
     * mdrs开始偏差量
     * |实况开始时间-预报开始时间|
     *
     * @return
     */
    @Override
    public Double mdrsStartDeviation() {
        Date startLiveTime = this.startLiveTime;
        Date startForcastTime = this.startForcastTime;
        long diff = startLiveTime.getTime() - startForcastTime.getTime();
        Double hour = diff / 1000.0 / 60 / 60;
        return hour;
    }

    /**
     * mdrs结束偏差量
     * |实况结束时间-预报结束时间|
     *
     * @return
     */
    @Override
    public Double mdrsEndDeviation() {
        Date endLiveTime = this.endLiveTime;
        Date endForcastTime = this.endForcastTime;
        long diff = endLiveTime.getTime() - endForcastTime.getTime();
        Double hour = diff / 1000.0 / 60 / 60;
        return hour;
    }

    /**
     * 命中得分
     * 出现漏报、空报得 0 分
     *
     * @return
     */
    @Override
    public Double mdrsHitScore() {
        // 开始偏差量
        Double startDeviation = mdrsStartDeviation();
        // 结束偏差量
        Double endDeviation = mdrsEndDeviation();
        if (startDeviation <= 1) {
            if (endDeviation <= 1) {
                return 100d;
            } else if (endDeviation > 1 && endDeviation <= 2) {
                return 95d;
            } else if (endDeviation > 2 && endDeviation <= 3) {
                return 90d;
            } else if (endDeviation > 3 && endDeviation <= 4) {
                return 80d;
            } else if (endDeviation > 4) {
                return 70d;
            }
        } else if (startDeviation > 1 && startDeviation <= 2) {
            if (endDeviation <= 1) {
                return 95d;
            } else if (endDeviation > 1 && endDeviation <= 2) {
                return 90d;
            } else if (endDeviation > 2 && endDeviation <= 3) {
                return 80d;
            } else if (endDeviation > 3 && endDeviation <= 4) {
                return 70d;
            } else if (endDeviation > 4) {
                return 60d;
            }
        } else if (startDeviation > 2 && startDeviation <= 3) {
            if (endDeviation <= 1) {
                return 90d;
            } else if (endDeviation > 1 && endDeviation <= 2) {
                return 80d;
            } else if (endDeviation > 2 && endDeviation <= 3) {
                return 70d;
            } else if (endDeviation > 3 && endDeviation <= 4) {
                return 60d;
            } else if (endDeviation > 4) {
                return 50d;
            }
        } else if (startDeviation > 3 && startDeviation <= 4) {
            if (endDeviation <= 1) {
                return 80d;
            } else if (endDeviation > 1 && endDeviation <= 2) {
                return 70d;
            } else if (endDeviation > 2 && endDeviation <= 3) {
                return 60d;
            } else if (endDeviation > 3 && endDeviation <= 4) {
                return 50d;
            } else if (endDeviation > 4) {
                return 40d;
            }
        } else if (startDeviation > 4) {
            if (endDeviation <= 1) {
                return 70d;
            } else if (endDeviation > 1 && endDeviation <= 2) {
                return 60d;
            } else if (endDeviation > 2 && endDeviation <= 3) {
                return 50d;
            } else if (endDeviation > 3 && endDeviation <= 4) {
                return 40d;
            } else if (endDeviation > 4) {
                return 30d;
            }
        }
        return 0d;
    }


    /**
     * mdrs主观得分
     *
     * @return
     */
    @Override
    public Double mdrsSubjectScore() {
        return null;
    }

    /**
     * mdrs客观得分
     * 命中得分×概率权重
     *
     * @return
     */
    @Override
    public Double mdrsObjectiveScore() {
        double objectiveScore = mdrsHitScore() * mdrsProWeight();
        return objectiveScore;
    }

    /**
     * 概率权重
     * 当预报的重要天气发生概率为≥70%的，概率权重为1；
     * 当预报的重要天气发生概率为 30%-40%的，概率权重为 0.7。
     *
     * @return 概率权重
     */
    @Override
    public Double mdrsProWeight() {
        if (this.predictProbility.contains("70%")) {
            return 1d;
        } else if (this.predictProbility.contains("30%-40%")) {
            return 0.7;
        }
        return 0d;
    }

    /**
     * mdrs总分
     *
     * @return
     */
    @Override
    public Double mdrsAllScore() {
        double allScore = (mdrsSubjectScore() + mdrsObjectiveScore()) / 2;
        return allScore;
    }
}