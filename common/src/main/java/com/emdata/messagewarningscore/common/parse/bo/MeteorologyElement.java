package com.emdata.messagewarningscore.common.parse.bo;

import com.emdata.messagewarningscore.common.parse.element.CloudGroup;
import com.emdata.messagewarningscore.common.parse.element.Weather;
import com.emdata.messagewarningscore.common.parse.element.WindGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 气象要素，风、云、能、天
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/10/10 11:29
 */
@Data
public class MeteorologyElement {

    /**
     * 预测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+0")
    private Date time;

    /**
     * 风组
     */
    private WindGroup windGroup;

    /**
     * 能见度
     */
    private Double visibility;

    /**
     * 天气现象，最多预报三种天气现象或天气现象的组合
     */
    private List<Weather> weathers;

    /**
     * 云组
     */
    private List<CloudGroup> cloudGroups;

}
