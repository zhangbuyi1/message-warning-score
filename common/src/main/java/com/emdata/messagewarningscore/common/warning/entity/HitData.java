package com.emdata.messagewarningscore.common.warning.entity;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.emdata.messagewarningscore.common.accuracy.entity.AccuracyEntity;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastData;
import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description:
 */
@Data
public class HitData implements Cloneable {
    @Override
    public HitData clone() {
        try {
            return (HitData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer alertContentResolveId;

    /**
     * 命中状态
     */
    private NatureEnum natureEnum;
    /**
     * 预测数据
     */
    private ForcastData forcastData;
    /**
     * 命中对应的实况数据
     */
    private List<SeriousDO> seriousDOS;
    /**
     * 存放计算提前量等数据
     */
    private AccuracyEntity AccuracyEntity;
    /**
     * 附加分
     */
    private Double foreScore;

    public static HitData buildHit(ForcastData forcastData, List<SeriousDO> seriousDOS) {
        HitData hitData = new HitData();
        hitData.setNatureEnum(NatureEnum.HIT);
        hitData.setForcastData(forcastData);
        hitData.setSeriousDOS(seriousDOS);
        return hitData;
    }

    public static HitData buildPartHit(ForcastData forcastData, List<SeriousDO> seriousDOS) {
        HitData hitData = new HitData();
        hitData.setNatureEnum(NatureEnum.PART_TRUE);
        hitData.setForcastData(forcastData);
        hitData.setSeriousDOS(seriousDOS);
        return hitData;
    }

    public static HitData buildEmpty(ForcastData forcastData) {
        HitData hitData = new HitData();
        hitData.setNatureEnum(NatureEnum.EMPTY);
        hitData.setForcastData(forcastData);
        hitData.setSeriousDOS(null);
        return hitData;
    }
}