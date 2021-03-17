package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2020/9/29.
 */

import lombok.Data;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/9/29
 * @description: 云对象
 */
@Data
public class ChildCloud implements Cloneable {
    //===========================================云组测试main方法===================================================================================
    public static void main(String[] args) {
        String testMessage = "TAF ZSPD 310500Z 3105/0105 10002MPS 9000 -RA VV/// TX13/0101Z TN11/3115Z BECMG 3108/3109 OVC011 BECMG 3122/3123 SCT011 BKN023=";
        // 需要将报文拆成基本组与变化组保证云组只有一组
    }

    @Override
    public ChildCloud clone() {
        try {
            return (ChildCloud) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
//=======================================报文云组用到的通用属性=============================================================================

    /**
     * 云的格式校验正则
     */
    public final static String cloudPatt = "((VV|BKN|FEW|OVC|SCT)(\\d{3})(CB|TCU))|((VV|BKN|FEW|OVC|SCT)(\\d{3}))|(CAVOK)|(NSC)|(VV///)";
    /**
     * 基本组默认云
     */
    public static final String defaultCloud = "NSC";
    /**
     * CAVOK 表示3000米以下没有重要的云
     */
    public static final String CLOUD_CAVOK = "CAVOK";
    /**
     * NSC 代表1600米以下没有重要的云
     */
    public static final String CLOUD_NSC = "NSC";
    /**
     * NSC 代表2000米以下没有重要的云
     */
    public static final String CLOUD_SKC = "SKC";
    /**
     * 浓积云 云状
     */
    public static final String CLOUD_TCU = "TCU";
    /**
     * 垂直能见度
     */
    public static final String CLOUD_VV = "VV";
    /**
     * 积雨云 云状
     */
    public static final String CLOUD_CB = "CB";
    /**
     * 默认云状
     */
    public static final Integer DEFALU_CLOUD_LIKE = 0;
    /**
     * 报文云高与实际云高 30:1的比例
     */
    public static final Integer CLOUD_HEIGHT_PROPORTION = 30;

    /**
     * 默认的VV 值为90米
     */
    public static final Integer DEFAULT_VV_HEIGHT = 90;
    /**
     * VV 不确定能见度
     */
    public static final String VV_UNCERTAIN_HEIGHT = "///";
    /**
     * 默认的云高
     */
    public static final Integer DEFAULT_CLOUD_HEIGHT = 900;

    public static final Map<String, Integer> CLOUD_MAP = new HashMap<String, Integer>() {{
        put("CAVOK", 3000);
        put("NSC", 1600);
        put("SKC", 2000);
        put("TCU", 2);
        put("CB", 1);
    }};

    /**
     * 云量 8分量映射
     */
    public static final Map<String, Integer> CLOUD_INESS_MAP = new HashMap<String, Integer>() {
        {
            put("FEW", 2);
            put("SCT", 4);
            put("BKN", 6);
            put("OVC", 8);
            put("VV", 9);
            /**
             * 除此之外 其余云量都映射为0
             */
        }
    };

    //====================================属性值===========================================================================

    /**
     * 8分量  FEW(1-2) SCT(3-4) BKN(5-7) OVC(8)
     */
    private Integer cloudIness8;

    /**
     * 云量
     */
    private String cloudIness;
    /**
     * 云高
     */
    private Integer cloudHeight;

    /**
     * 云状（0--无  1--CB）
     */
    private Integer cloudCb;


    /**
     * 云高是否使用了map映射
     */
    private boolean isMap = false;
    /**
     *==============================================构造器方法====================================================================
     *
     */
    /**
     * 有参构造器
     *
     * @param cloudString 报文云
     */
    public ChildCloud(String cloudString) {
        goCloud(cloudString);
    }

    private final void goCloud(String cloudString) {
        if (cloudString.contains(CLOUD_VV)) {
            this.cloudIness = CLOUD_VV;
            String vv = cloudString.replace(CLOUD_VV, "").substring(0, 3);
            try {
                this.cloudHeight = Integer.parseInt(vv) * CLOUD_HEIGHT_PROPORTION;
            } catch (Exception e) {
                // 云高出现异常
                this.cloudHeight = DEFAULT_VV_HEIGHT;
                this.isMap = true;
            }
        } else if (cloudString.contains(CLOUD_NSC)) {
            this.cloudIness = CLOUD_NSC;
            this.cloudHeight = CLOUD_MAP.get(CLOUD_NSC);
        } else if (cloudString.contains(CLOUD_CAVOK)) {
            cloudHeight = CLOUD_MAP.get(CLOUD_CAVOK);
            cloudIness = CLOUD_CAVOK;
        } else if (cloudString.contains(CLOUD_SKC)) {
            this.cloudHeight = CLOUD_MAP.get(CLOUD_SKC);
            this.cloudIness = CLOUD_SKC;
        } else {
            this.cloudIness = cloudString.substring(0, 3);
            try {
                this.cloudHeight = Integer.parseInt(cloudString.substring(3, 6)) * 30;
            } catch (Exception e) {
                this.cloudHeight = DEFAULT_CLOUD_HEIGHT;
            }
        }
        if (cloudString.contains(CLOUD_CB)) {
            this.cloudCb = CLOUD_MAP.get(CLOUD_CB);
        } else if (cloudString.contains(CLOUD_TCU)) {
            this.cloudCb = CLOUD_MAP.get(CLOUD_TCU);
        } else {
            this.cloudCb = DEFALU_CLOUD_LIKE;
        }
        //构建云量
        Integer cloudIness8 = CLOUD_INESS_MAP.get(this.cloudIness);
        if (cloudIness8 != null) {
            this.cloudIness8 = cloudIness8;
        } else {
            this.cloudIness8 = 0;
        }
    }

    /**
     * 无参构造器
     */
    public ChildCloud() {
    }

    /**
     * 当前云是否是单一云
     *
     * @return
     */
    public boolean isSingleCloud() {
        return this.cloudIness.equals("NSC") || this.cloudIness.equals("CAVOK");
    }

    //===================================================将当前对象转为字符串方法========================================================================
    public String toCloudString() {
        StringBuilder stringBuilder = new StringBuilder();
        String cloudIness = this.getCloudIness();
        if (cloudIness.contains(CLOUD_CAVOK) || cloudIness.contains(CLOUD_NSC)) {
            stringBuilder.append(this.getCloudIness() + " ");
        } else {
            stringBuilder.append(this.getCloudIness());
            String cloudHeight = "";
            if (isMap) {
                cloudHeight = VV_UNCERTAIN_HEIGHT;
            } else {
                int h = this.getCloudHeight() / CLOUD_HEIGHT_PROPORTION;
                cloudHeight = String.format("%3d", h).replace(" ", "0");
            }
            stringBuilder.append(cloudHeight);
            if (this.getCloudCb().equals(CLOUD_MAP.get(CLOUD_CB))) {
                stringBuilder.append(CLOUD_CB + " ");
            } else if (this.getCloudCb().equals(CLOUD_MAP.get(CLOUD_TCU))) {
                stringBuilder.append(CLOUD_TCU + " ");
            } else {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString().trim();
    }
//==================================================通过报文构建一个云组对象方法========================================================================

    /**
     * @param message 报文基本组 或者报文某一个变化组
     * @return
     */
    public static String getCloudString(String message) {
        String cloudString = getCloudString(message, cloudPatt);
        return cloudString;
    }

    /**
     * @param message
     * @param cloudPatt
     * @return
     */
    public static String getCloudString(String message, String cloudPatt) {
        List<String> cloud = new ArrayList<>();
        getCloudString(message, cloudPatt, cloud);
        Optional<String> reduce = cloud.stream().reduce(((s, s2) -> {
            return s + " " + s2;
        }));
        if (reduce.isPresent()) {
            return reduce.get().trim();
        }
        return null;
    }

    /**
     * @param message 报文基本组 或者报文某一个变化组
     * @return
     */
    public static List<ChildCloud> buildCloudDO(String message) {
        return buildCloudDO(message, cloudPatt);
    }

    /**
     * @param message   报文基本组 或者报文某一个变化组
     * @param cloudPatt 云组格式
     * @return
     */
    public static List<ChildCloud> buildCloudDO(String message, String cloudPatt) {
        List<String> cloud = new ArrayList<>();
        getCloudString(message, cloudPatt, cloud);
        List<ChildCloud> cloudDOS = cloud.stream().map(s -> {
            return new ChildCloud(s);
        }).collect(Collectors.toList());

        return cloudDOS;
    }

    /**
     * 从字符串中取出云
     *
     * @param message   报文基本组 或者报文某一个变化组
     * @param cloudPatt
     * @return
     */
    private static void getCloudString(String message, String cloudPatt, List<String> cloudS) {
        Pattern compile = Pattern.compile(cloudPatt);
        Matcher matcher = compile.matcher(message);
        if (matcher.find()) {
            String group = matcher.group();
            cloudS.add(group);
            String replace = message.replace(group+" ", " ");
            getCloudString(replace, cloudPatt, cloudS);
        } else {
            return;
        }
    }

    /**
     * 判断当前云是否是BKN或者OVC 的云
     *
     * @return
     */
    public boolean isBKNOrOVC() {
        return this.cloudIness8 > 5;
    }

    public boolean isLT450BKNOrOVC() {
        return this.cloudHeight < 450 && isBKNOrOVC();
    }

    /**
     * 是否是积雨云
     *
     * @return
     */
    public boolean isCBCloud() {
        return this.cloudCb == 1;
    }

    /**
     * 是否是浓积云
     *
     * @return
     */
    public boolean isTCUCloud() {
        return this.cloudCb == 2;
    }

    /**
     * 判断是否是垂直能见度
     *
     * @return
     */
    public boolean isVvCloud() {
        return this.cloudIness.equals(CLOUD_VV);
    }


}