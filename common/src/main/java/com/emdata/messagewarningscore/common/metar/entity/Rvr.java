package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description:
 */
public class Rvr extends Index {

    private static final String pattern = "(R\\d{2}([A-Z](/|／)|(/|／)))((\\d{4}|(P\\d{4}))((\\sV\\s\\d{4}\\s[A-Z])|([A-Z])|(\\s)|(^)))";
    /**
     * 跑道数据
     */
    private List<RvrAn> rvrAns = new ArrayList<>();

    /**
     * 返回最小能见度跑道数据
     *
     * @return
     */
    public Integer getMinRvrVis() {
        Optional<RvrAn> min = rvrAns.stream().min(Comparator.comparing(s -> {
            return s.minVis;
        }));
        if (min.isPresent()) {
            return min.get().minVis;
        } else {
            // 默认值9999 暂时写死
            return 9999;
        }

    }

    class RvrAn {
        /**
         * 跑道名称
         */
        private String runway;
        /**
         * 跑道能见度
         */
        private Integer minVis;
        /**
         * 最大能见度
         */
        private Integer maxVis;
        /**
         * 能见度标识  P代表大于minVis
         */
        private String minStartCode = "";
        /**
         * 能见度表示 P代表大于maxVis P2000
         */
        private String maxStartCode = "";
        /**
         * 最小能见度结束code如 2000M
         */
        private String minEndCode = "";
        /**
         * 最大能见度结束code 如 2000M
         */
        private String maxEndCode = "";

        /**
         * 当前构造器先写简单的  目测跑道名称大多数都为 二位数加或者不加字母 能见度为4位数字  因为不解析能见度Code 码 所以就
         *
         * @param anRvr
         */
        public RvrAn(String anRvr) {
            // 处理跑道
            String runway = MatchUtil.get(anRvr, "(R\\d{2}([A-Z](/|／)|(/|／)))").replace("/", "").replace("／", "");
            this.runway = runway;
            // 处理能见度
            List<String> list1 = MatchUtil.getList(anRvr, "(P|^|/)(\\d{4})(([A-Z])|^|\\s)", new ArrayList<>());
            // 如果等于1 说明只有一个能见度
            if (list1.size() == 1) {
                String min = list1.get(0);
                vis(min, true);
                // 处理能见度标识符
            }
            // 如果等于2 说明有俩个能加度
            if (list1.size() == 2) {
                Optional<String> minO = list1.stream().min(String::compareTo);
                String min = minO.get();
                vis(min, true);
                Optional<String> maxO = list1.stream().max(String::compareTo);
                String max = maxO.get();
                vis(max, false);
            }
        }

        /**
         * \
         * 解析能见度
         *
         * @param vis
         * @param isMin
         */
        private void vis(String vis, Boolean isMin) {
            if (isMin) {
                this.minVis = Integer.parseInt(MatchUtil.get(vis, "\\d{4}"));
                if (vis.contains("P")) {
                    this.minStartCode = "P";
                }
                // 处理能见度描述符 跟在能见度后面的一个字母
                String s = MatchUtil.get(vis, "\\d{4}[A-Z]");
                if (!StringUtil.isNullOrEmpty(s)) {
                    String code = MatchUtil.get(s, "[A-Z]");
                    this.minEndCode = code;
                }
            } else {
                this.maxVis = Integer.parseInt(MatchUtil.get(vis, "\\d{4}"));
                if (vis.contains("P")) {
                    this.maxStartCode = "P";
                }
                // 处理能见度描述符 跟在能见度后面的一个字母
                String s = MatchUtil.get(vis, "\\d{4}[A-Z]");
                if (!StringUtil.isNullOrEmpty(s)) {
                    String code = MatchUtil.get(s, "[A-Z]");
                    this.maxEndCode = code;
                }
            }

        }

        @Override
        public String toString() {
            return this.runway + "/" + this.minStartCode + this.minVis + this.minEndCode + (this.maxVis == null ? "" : ("V" + this.maxStartCode + this.maxVis + this.maxEndCode));
        }
    }

    /**
     * @param metar 实况报文
     */
    public Rvr(String metar) {
        List<String> list = MatchUtil.getList(metar, pattern, new ArrayList<>());
        if (CollectionUtils.isNotEmpty(list)) {
            List<RvrAn> collect = list.stream().map(s -> {
                return new RvrAn(s);
            }).collect(Collectors.toList());
            this.rvrAns = collect;
        }
    }

    public static void main(String[] args) {
        List<String> list = MatchUtil.getList("SPECI ZSNJ 152338Z 07006MPS 030V090 1000 R06/2000 R07/P1800D +TSRA BR BKN007 OVC023 SCT050CB 20/20 Q1006 BECMG TL0100 1500 -TSRA=", pattern, new ArrayList<>());
        Rvr rvr = new Rvr("SPECI ZSNJ 152338Z 07006MPS 030V090 1000 R06/2000 R07/P1800D +TSRA BR BKN007 OVC023 SCT050CB 20/20 Q1006 BECMG TL0100 1500 -TSRA=");
        System.out.println(rvr);
        Integer minRvrVis = rvr.getMinRvrVis();
        System.out.println(minRvrVis);
    }

    @Override
    public String toString() {
        String reduce = this.rvrAns.stream().map(s -> {
            return s.toString();
        }).reduce("", (s1, s2) -> {
            return s1 + " " + s2;
        });
        return reduce.trim();
    }
}