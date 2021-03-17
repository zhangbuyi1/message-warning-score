package com.emdata.messagewarningscore.common.parse.bo;

import com.emdata.messagewarningscore.common.parse.element.CloudGroup;
import com.emdata.messagewarningscore.common.parse.element.Weather;
import com.emdata.messagewarningscore.common.parse.element.WindGroup;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 变化组部分
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/14 11:22
 */
@Data
public class ChangeGroup {

    /**
     * 变化指示码“BECMG”或“TEMPO”
     */
    private TrendTypeCodeEnum trendTypeCode;

    /**
     * 时间指示码，使用指示码“FM”、“TL”或“AT”分别紧接相应的协调世界时（UTC）小时和分钟的时间组表明预报变化发生的时段或时刻
     */
    private TrendTypeTimeEnum trendTypeTime;

    /**
     * 变化开始时间
     */
    private Date changeStartTime;

    /**
     * 变化结束时间
     */
    private Date changeEndTime;

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
