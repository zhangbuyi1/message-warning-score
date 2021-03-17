package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2020/10/12.
 */

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhangshaohu
 * @date: 2020/10/12
 * @description: 能见度
 */
public class Vis extends Index implements Cloneable {


    /**
     * ===============================================测试方法================================================================================
     */
    public static void main(String[] args) {
        Vis vis = new Vis("METAR ZSCN 301500Z 30002MPS \\\\ 28/27 Q1008 NOSIG=");
        System.out.println(vis);
    }

    @Override
    public Vis clone() {
        try {
            return (Vis) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ===============================================默认参数================================================================================
     */
    private final Integer start = 5;
    private final Integer end = 6;
    /**
     * ==============================================正则表达式================================================================================
     */
    private String pattern = "(\\s\\d{4}$)|(\\s\\d{4}\\s)|(CAVOK)|(NSW)";
    /**
     * 垂直能见度
     */
    private String vvPattern = "\\sVV\\d{3}\\s";
    private final Map<String, String> composeMap = new HashMap<String, String>() {{
        put("CAVOK", "9999");
    }};

    /**
     * ==============================================属性================================================================================
     */
    // 基本能见度
    private Integer vis = null;


    public void setVis(Integer vis) {
        this.vis = vis;
    }


    public Integer getVis() {
        return vis;
    }


    public Vis() {
        this.startIndex = start;
        this.endIndex = end;
    }

    /**
     * 能见度之前必须是风基本组 或者是变化组的时间区间
     *
     * @param message
     */
    public Vis(String message) {
        message = addSpace(message);
        String s = match(message, pattern);
        String visVal = getVisVal(s);
        if (StringUtils.isEmpty(visVal)) {
            this.vis = 9999;
        }else {
            this.vis = Integer.parseInt(visVal);
        }
        indexActual(message, s);
        this.startIndex = start;
        this.endIndex = end;
    }


    /**
     * @param vis
     */
    public Vis(Integer vis) {
        this.vis = vis;
        this.startIndex = start;
        this.endIndex = end;
    }


    /**
     * 返回能见度值
     *
     * @param vis
     * @return
     */
    private String getVisVal(String vis) {
        String s = composeMap.get(vis);
        if (StringUtils.isEmpty(s)) {
            return vis;
        } else {
            return s;
        }
    }


    @Override
    public String toString() {
        if (composeMap.containsKey(vis)) {
            return "";
        }
        String trim = String.format("%04d", vis).replace(" ", "0").trim();
        return trim;
    }
}