package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2020/10/12.
 */


import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/10/12
 * @description:
 */

public class Cloud extends Index implements Cloneable {
    private final Integer start = 7;
    private final Integer end = 8;

    public static void main(String[] args) {
        Cloud cloud = new Cloud("FEW030 FEW030CB");
        System.out.println(cloud.toString());
    }

    @Override
    public Cloud clone() {
        try {
            Cloud clone = (Cloud) super.clone();
            clone.setChildClouds(clone.getChildClouds().stream().map(s -> {
                return s.clone();
            }).collect(Collectors.toList()));
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void rmCloudCB() {
        this.childClouds = this.childClouds.stream().filter(s -> {
            return !s.getCloudCb().equals(1);
        }).collect(Collectors.toList());
    }

    public List<ChildCloud> getChildClouds() {
        return childClouds;
    }

    public void setChildClouds(List<ChildCloud> childClouds) {
        this.childClouds = childClouds;
    }

    /**
     * 当前是否是垂直能见度
     */
    private boolean isVv;

    private String weather;
    private List<ChildCloud> childClouds;

    public Cloud() {

    }


    /**
     * 云的構造器
     *
     * @param message
     */
    public Cloud(String message) {
        message = addSpace(message);
        this.childClouds = ChildCloud.buildCloudDO(message);
        indexActual(message, toCloudStrings(this.childClouds));
        this.startIndex = start;
        this.endIndex = end;
    }


    public String toCloudStrings(List<ChildCloud> childClouds) {
        if (CollectionUtils.isEmpty(childClouds)) {
            return "";
        }
        String cloudStrings = childClouds.stream().map(s -> {
            return s.toCloudString() + " ";
        }).reduce((s1, s2) -> {
            return s1 + s2;
        }).get();
        return cloudStrings.trim();
    }

    @Override
    public String toString() {
        // 过滤出不是单一的云
        List<ChildCloud> collect = this.childClouds.stream().filter(s -> {
            return !s.isSingleCloud();
        }).collect(Collectors.toList());
        if (collect.size() != this.childClouds.size()) {
            if (!CollectionUtils.isEmpty(collect)) {
                return toCloudStrings(collect);
            } else {
                return toCloudStrings(this.childClouds);
            }
        }
        return toCloudStrings(this.childClouds);
    }

    /**
     * 返回最低层云
     *
     * @return
     */
    public ChildCloud getLowCloud() {
        Optional<ChildCloud> min = childClouds.stream().min(Comparator.comparing(ChildCloud::getCloudHeight));
        if (min.isPresent()) {
            return min.get();
        }
        return null;
    }

    public void addChildCloud(ChildCloud childCloud) {
        this.childClouds.add(childCloud);
    }



    /**
     * 返回BKN与OVC 的最低层云
     * 如果没有则返回3000 米的BKN 云
     *
     * @param isNotNull 是否必须返回
     * @return
     */
    public ChildCloud getBKNAndOVClowCloud(boolean isNotNull) {
        Optional<ChildCloud> min = childClouds.stream().filter(s -> {
            return s.getCloudIness8() >= 5;
        }).min(Comparator.comparing(ChildCloud::getCloudHeight));
        if (min.isPresent()) {
            return min.get();
        }
        if (isNotNull) {
            ChildCloud childCloud = new ChildCloud();
            childCloud.setCloudCb(0);
            childCloud.setCloudHeight(3000);
            childCloud.setCloudIness("BKN");
            childCloud.setCloudIness8(6);
            return childCloud;
        }
        return null;
    }

    /**
     * 是否有积雨云
     *
     * @return
     */
    public boolean isCloudCB() {
        Optional<ChildCloud> first = childClouds.stream().filter(s -> {
            return s.isCBCloud();
        }).findFirst();
        return first.isPresent();
    }

    /**
     * 是否有浓集运
     *
     * @return
     */
    public boolean isCloudTCU() {
        Optional<ChildCloud> first = childClouds.stream().filter(s -> {
            return s.isTCUCloud();
        }).findFirst();
        return first.isPresent();
    }


    /**
     * 是否是垂直能见度
     *
     * @return
     */
    public boolean isVv() {
        Optional<ChildCloud> first = childClouds.stream().filter(s -> {
            return s.isVvCloud();
        }).findFirst();
        return first.isPresent();
    }

    /**
     * 返回垂直能见度
     *
     * @return
     */
    public Integer getVvVis() {
        Optional<ChildCloud> first = childClouds.stream().filter(s -> {
            return s.isVvCloud();
        }).findFirst();
        return first.get().getCloudHeight();
    }
}