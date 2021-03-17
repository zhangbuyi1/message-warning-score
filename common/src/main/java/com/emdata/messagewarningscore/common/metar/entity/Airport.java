package com.emdata.messagewarningscore.common.metar.entity;/**
 * Created by zhangshaohu on 2020/10/12.
 */

import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2020/10/12
 * @description:
 */
@Data
public class Airport extends Index {
    /**
     * ======================================测试方法===========================================================================
     */
    public static void main(String[] args) {
        Airport airport = new Airport("TAF ZSNT 290850Z 2912/3012 13001MPS 8000 -SHRA FEW023 TX31/3006Z TN24/2921Z BECMG 3005/3006 -TSRA FEW023 FEW030CB=");
    }

    /**
     * ======================================默认属性===========================================================================
     */
    private final Integer start = 1;

    private final Integer end = 2;
    /**
     * ======================================可修改属性===========================================================================
     */
    private String pattern = "Z[A-Z]{3}";

    private String airport = "";

    /**
     * ======================================构造器===========================================================================
     */
    public Airport() {


    }

    public Airport(String message) {
        this.airport = match(message, pattern);
        indexActual(message, this.airport);
    }

    public Airport(String message, Integer startIndex, Integer endIndex) {
        this.airport = matchAll(message, pattern, new StringBuilder());
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        indexActual(message, this.airport);
    }

    @Override
    public String toString() {
        return airport;
    }

}