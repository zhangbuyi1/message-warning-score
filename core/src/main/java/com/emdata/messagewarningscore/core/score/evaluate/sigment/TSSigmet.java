package com.emdata.messagewarningscore.core.score.evaluate.sigment;/**
 * Created by zhangshaohu on 2020/12/21.
 */


import com.emdata.messagewarningscore.common.accuracy.config.TSConfig;
import com.emdata.messagewarningscore.core.score.accuracy.Accuracy;
import com.emdata.messagewarningscore.core.score.accuracy.tmp.AccuracyTmp;
import com.emdata.messagewarningscore.core.score.evaluate.Sigmet;
import com.emdata.messagewarningscore.core.score.evaluate.entity.Accompany;

//import com.emdata.messagewarningscore.core.score.config.entity.TSConfig;


/**
 * @author: zhangshaohu
 * @date: 2020/12/21
 * @description:
 */
public abstract class TSSigmet implements Sigmet {

    /**
     * 判断准确率类的引用
     */
    private AccuracyTmp accuracyTmp;

    /**
     * 雷暴对流配置文件
     */
    private TSConfig tsConfig;

    /**
     * 实况天气与预测天气
     */
    private Accompany accompany;


    public TSSigmet(AccuracyTmp accuracyTmp, TSConfig tsConfig, Accuracy accuracy) {
        this.accuracyTmp = accuracyTmp;
        this.tsConfig = tsConfig;
    }

    /**
     * 附属得分
     *
     * @return
     */
    @Override
    public Double attachScore() {
        return attachScore(tsConfig);
    }

    /**
     * 附属得分方法
     *
     * @param tsConfig
     * @return
     */
    private Double attachScore(TSConfig tsConfig) {


        return null;
    }

    /**
     * 提前量
     *
     * @return
     */
    @Override
    public Double leadTime() {
        return accuracyTmp.leadTime();
    }

    /**
     * 偏差量
     *
     * @return
     */
    @Override
    public Double deviation() {
        return accuracyTmp.deviation();
    }

    /**
     * 重合率
     *
     * @return
     */
    @Override
    public Double coincidenceRate() {
        return accuracyTmp.coincidenceRate();
    }

    /**
     * 是否满足终端区起报阈值
     *
     * @return
     */
    @Override
    public boolean startingTerminalReportThreshold() {
        return false;
    }

    /**
     * 是否满足重要天气与MDRF起报阈值
     *
     * @return
     */
    @Override
    public boolean startingAlertReportThreshold() {


        return false;
    }


}