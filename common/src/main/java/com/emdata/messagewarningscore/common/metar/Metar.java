package com.emdata.messagewarningscore.common.metar;/**
 * Created by zhangshaohu on 2020/12/29.
 */

import com.emdata.messagewarningscore.common.metar.entity.*;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author: zhangshaohu
 * @date: 2020/12/29
 * @description:
 */
@Data
public class Metar {
    String pattern = "NOSIG|BECMG|TEMPO|FM";

    public static void main(String[] args) {
        Metar metar = new Metar("METAR ZSPD 110330Z 16007MPS 7000 -RA FEW007 OVC033 10/09 Q1024 NOSIG=");
        System.out.println(metar);
    }

    /**
     * 机场地点
     */
    private Airport airport;
    /**
     * 云
     */
    private Cloud cloud;
    /**
     * 能见度
     */
    private Vis vis;
    /**
     * 天气现象
     */
    private WeatherDO weatherDO;
    /**
     * 风
     */
    private WindDO windDO;
    /**
     * 跑道数据
     */
    private Rvr rvr;
    /**
     * 当前报文发布时间
     */
    private Date metarTime;


    public Metar(String message, Date metarTime) {
        message = getBaseMetar(message);
        this.airport = new Airport(message);
        this.cloud = new Cloud(message);
        this.vis = new Vis(message);
        this.weatherDO = new WeatherDO(message);
        this.windDO = new WindDO(message);
        this.metarTime = metarTime;
    }

    public Metar(String message) {
        message = getBaseMetar(message);
        this.airport = new Airport(message);
        this.cloud = new Cloud(message);
        this.vis = new Vis(message);
        this.weatherDO = new WeatherDO(message);
        this.windDO = new WindDO(message);
        this.rvr=new Rvr(message);
    }


    private String getBaseMetar(String message) {
        String s = MatchUtil.get(message, pattern);
        if (StringUtils.isNotEmpty(s)) {
            String substring = message.substring(0, message.indexOf(s));
            return substring;
        }
        return message;

    }
}