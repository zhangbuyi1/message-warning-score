package com.emdata.messagewarningscore.common.parse.util;

import com.emdata.messagewarningscore.common.parse.element.CloudGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @description: CloudChangeUtil
 * @date: 2020/10/13 9:59
 * @author: sunming
 */
public class CloudChangeUtil {

    private static final String CB = "CB";
    private static final String TCU = "TCU";
    /**
     * 雲量 2分云
     */
    public final static String MESSAGE_CLOUD_FEW = "FEW";
    /**
     * 雲量 4分云
     */
    public final static String MESSAGE_CLOUD_SCT = "SCT";
    /**
     * 雲量 6分云
     */
    public final static String MESSAGE_CLOUD_BKN = "BKN";
    /**
     * 雲量8分云
     */
    public final static String MESSAGE_CLOUD_OVC = "OVC";

    /**
     * 雲量8分云
     */
    public final static String MESSAGE_CLOUD_VV = "VV";

    /**
     * 当云是NSC是  云高为1600
     */
    public static final String METAR_DEFAULT_NSC_INESS = "NSC";
    /**
     * nsc 雲高
     */
    public static final Integer METAR_DEFAULT_NSC_HEIGHT = 1600;

    /**
     * 最低雲高
     */
    public static final Integer TREND_LOW_CLOUD = 450;

    private static int[] cloudHeightSetion = {30, 60, 150, 300};

    /**
     * 判断云组是否满足变化组条件
     *
     * @param baseCloud   基本组云
     * @param changeCloud 变化组云
     * @return 是否满足
     */
    public static boolean cloud(List<CloudGroup> baseCloud, List<CloudGroup> changeCloud) {
        boolean cloudFirst = cloudFirst(baseCloud, changeCloud);
        if (cloudFirst) {
            return cloudFirst;
        }
        // 如果云状不为空，则为CB或者TCU
        Optional<CloudGroup> base = baseCloud.stream().filter(s -> {
            return StringUtils.isNotBlank(s.getCloudShape());
        }).findFirst();
        Optional<CloudGroup> change = changeCloud.stream().filter(s -> {
            return StringUtils.isNotBlank(s.getCloudShape());
        }).findFirst();
        // 如果CB、TCU云从有到有,校验云量
        if (base.isPresent() && change.isPresent()) {
            return jugdeCloudAmount(baseCloud, changeCloud);
        }
        return false;
    }

    /**
     * 检测云是否满足变化组第一步
     *
     * @param baseCloud   基本的云组
     * @param changeCloud 变化组的云组
     * @return
     */
    public static boolean cloudFirst(List<CloudGroup> baseCloud, List<CloudGroup> changeCloud) {
        Optional<CloudGroup> baseCb = baseCloud.stream().filter(s -> {
            return StringUtils.isNotBlank(s.getCloudShape()) && s.getCloudShape().equals(CB);
        }).findFirst();
        Optional<CloudGroup> changeCb = changeCloud.stream().filter(s -> {
            return StringUtils.isNotBlank(s.getCloudShape()) && s.getCloudShape().equals(CB);
        }).findFirst();
        Optional<CloudGroup> baseTcu = baseCloud.stream().filter(s -> {
            return StringUtils.isNotBlank(s.getCloudShape()) && s.getCloudShape().equals(TCU);
        }).findFirst();
        Optional<CloudGroup> changeTcu = changeCloud.stream().filter(s -> {
            return StringUtils.isNotBlank(s.getCloudShape()) && s.getCloudShape().equals(TCU);
        }).findFirst();
        // CB云从有到无
        if (baseCb.isPresent() && !changeCb.isPresent()) {
            return true;
        }
        // CB云从无到有
        if (!baseCb.isPresent() && changeCb.isPresent()) {
            return true;
        }
        // TCU云从有到无
        if (baseTcu.isPresent() && !changeTcu.isPresent()) {
            return true;
        }
        // TCU云从无到有
        if (!baseTcu.isPresent() && changeTcu.isPresent()) {
            return true;
        }
        return jugdeCloud(baseCloud, changeCloud);
    }

    /**
     * 校测云是否满足变化组
     *
     * @param baseCloudBOS
     * @param cloudChangeBOS
     * @return
     */
    public static boolean jugdeCloud(List<CloudGroup> baseCloudBOS, List<CloudGroup> cloudChangeBOS) {
        baseCloudBOS.sort(Comparator.comparing(CloudGroup::getCloudHeight));
        cloudChangeBOS.sort(Comparator.comparing(CloudGroup::getCloudHeight));
        //  获得最低云层
        Optional<CloudGroup> baselowCloud = baseCloudBOS.stream().filter(c -> {
            return c.getCloudHeight() < TREND_LOW_CLOUD && (
                    c.getCloudinessType().contains(MESSAGE_CLOUD_BKN) ||
                            c.getCloudinessType().contains(MESSAGE_CLOUD_OVC) ||
                            c.getCloudinessType().contains(MESSAGE_CLOUD_VV));
        }).findFirst();
        Optional<CloudGroup> changelowCloud = cloudChangeBOS.stream().filter(c -> {
            return c.getCloudHeight() < TREND_LOW_CLOUD && (
                    c.getCloudinessType().contains(MESSAGE_CLOUD_BKN) ||
                            c.getCloudinessType().contains(MESSAGE_CLOUD_OVC) ||
                            c.getCloudinessType().contains(MESSAGE_CLOUD_VV));
        }).findFirst();
        // 变化组云高
        if (cloudChange(baselowCloud, changelowCloud, CloudChangeUtil::cloudHeight)) {
            return true;
        }
        // 变化组云量
        if (cloudChange(baselowCloud, changelowCloud, CloudChangeUtil::cloudIness)) {
            return true;
        }
        // 积雨云的变化与天气现象有关 只有出现天气现象才会出现积雨云的变化
       /* // 变化组积雨云
        if (cloudCB(baseCloudBOS, cloudChangeBOS)) {
            return true;
        }*/
        return false;
    }

    public static boolean cloudChange(Optional<CloudGroup> baselowCloud, Optional<CloudGroup> changelowCloud, BiFunction<CloudGroup, CloudGroup, Boolean> cloudChange) {
        // 如果基本有450米以下的云高
        if (baselowCloud.isPresent()) {
            // 变化组也有450米以下的云高
            if (changelowCloud.isPresent()) {
                // 需要判断最低云高是否发生区间变化
                if (cloudChange.apply(baselowCloud.get(), changelowCloud.get())) {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            if (changelowCloud.isPresent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 云高变化组
     *
     * @param baselowCloud
     * @param changelowCloud
     * @return
     */
    public static boolean cloudHeight(CloudGroup baselowCloud, CloudGroup changelowCloud) {
        /**
         *  * 1、当预报BKN 或OVC 云量的最低云层的云高抬升并达到或经过下列一个或多个阈值，或者降低并经过下列一个或多个阈值时：
         a) 30 米、60 米、150 米或300 米；
         b) 450 米（在有大量的按目视飞行规则的飞行时）。
         */
        if (juCloudSetion(baselowCloud.getCloudHeight(), changelowCloud.getCloudHeight())) {
            return true;
        }
        return false;
    }

    /**
     * 判断云高是否ok
     *
     * @param cloudHeightbase
     * @param cloudHeightchange
     * @return
     */
    public static boolean juCloudSetion(Integer cloudHeightbase, Integer cloudHeightchange) {

        for (int i = 0; i < cloudHeightSetion.length; i++) {
            if (i + 1 < cloudHeightSetion.length) {
                if (i == 0) {
                    int start = 0;
                    int end = cloudHeightSetion[i];
                    if (cloudHeightbase >= start && cloudHeightbase < end) {
                        if (cloudHeightchange >= start && cloudHeightchange < end) {
                            return false;
                        }
                    }
                }
                int start = cloudHeightSetion[i];
                int end = cloudHeightSetion[i + 1];
                if (cloudHeightbase >= start && cloudHeightbase < end) {
                    if (cloudHeightchange >= start && cloudHeightchange < end) {
                        return false;
                    }
                }
            }
        }
        if (cloudHeightbase >= cloudHeightSetion[cloudHeightSetion.length - 1] && cloudHeightchange >= cloudHeightSetion[cloudHeightSetion.length - 1]) {
            return false;

        }
        return true;
    }

    /**
     * 云量变化组
     *
     * @param baselowCloud
     * @param changelowCloud
     * @return
     */
    public static boolean cloudIness(CloudGroup baselowCloud, CloudGroup changelowCloud) {
        String baselowCloudIness = baselowCloud.getCloudinessType();
        String changelowCloudCloudIness = changelowCloud.getCloudinessType();
        /**
         当预报低于450 米的云层或云块的量的变化满足下列条件之一时：
         a) 从SCT 或更少到BKN、OVC；
         b) 从BKN、OVC 到SCT 或更少。
         当预报积雨云发展或消失时。
         */
        if (baselowCloudIness.contains(MESSAGE_CLOUD_BKN) || baselowCloudIness.contains(MESSAGE_CLOUD_OVC)) {
            if (changelowCloudCloudIness.contains(MESSAGE_CLOUD_FEW) || changelowCloudCloudIness.contains(MESSAGE_CLOUD_SCT)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (changelowCloudCloudIness.contains(MESSAGE_CLOUD_BKN) || changelowCloudCloudIness.contains(MESSAGE_CLOUD_OVC)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 校验云量变化组
     *
     * @param baseCloudGroups   基本组云集合
     * @param changeCloudGroups 变化组云集合
     * @return 是否满足变化组
     */
    public static boolean jugdeCloudAmount(List<CloudGroup> baseCloudGroups, List<CloudGroup> changeCloudGroups) {
        baseCloudGroups.sort(Comparator.comparing(CloudGroup::getCloudHeight));
        changeCloudGroups.sort(Comparator.comparing(CloudGroup::getCloudHeight));
        //  获得最低云层
        Optional<CloudGroup> baselowCloud = baseCloudGroups.stream().filter(c -> {
            return (c.getCloudinessType().contains(MESSAGE_CLOUD_BKN) ||
                    c.getCloudinessType().contains(MESSAGE_CLOUD_OVC) ||
                    c.getCloudinessType().contains(MESSAGE_CLOUD_VV));
        }).findFirst();
        Optional<CloudGroup> changelowCloud = changeCloudGroups.stream().filter(c -> {
            return (c.getCloudinessType().contains(MESSAGE_CLOUD_BKN) ||
                    c.getCloudinessType().contains(MESSAGE_CLOUD_OVC) ||
                    c.getCloudinessType().contains(MESSAGE_CLOUD_VV));
        }).findFirst();

        // 变化组云量
        if (cloudChange(baselowCloud, changelowCloud, CloudChangeUtil::cloudIness)) {
            return true;
        }

        return false;
    }
}
