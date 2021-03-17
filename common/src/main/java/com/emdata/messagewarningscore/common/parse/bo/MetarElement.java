package com.emdata.messagewarningscore.common.parse.bo;

import com.emdata.messagewarningscore.common.parse.element.CloudGroup;
import com.emdata.messagewarningscore.common.parse.element.Weather;
import com.emdata.messagewarningscore.common.parse.element.WindGroup;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * metar报文对象
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/10 13:50
 */
@Data
public class MetarElement {

    /**
     * 报头
     */
    protected String header;

    /**
     * 国际民航组织（ICAO）规定的机场代码
     */
    protected String airportCode;

    /**
     * 发布时间
     */
    protected Date releaseTime;

    /**
     * 风组
     */
    protected WindGroup windGroup;

    /**
     * 能见度
     */
    protected Double visibility;

    /**
     * 天气现象，最多预报三种天气现象或天气现象的组合
     */
    protected List<Weather> weathers;

    /**
     * 云组
     */
    protected List<CloudGroup> cloudGroups;

    /**
     * 温度
     */
    protected Double temperature;

    /**
     * 露点温度
     */
    protected Double dewPointTemperature;

    /**
     * 气压
     */
    protected Double airPressure;

}
