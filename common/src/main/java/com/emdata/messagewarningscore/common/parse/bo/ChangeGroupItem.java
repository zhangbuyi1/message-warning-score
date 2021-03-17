package com.emdata.messagewarningscore.common.parse.bo;

import lombok.Data;

import java.util.Date;

/**
 * 变化组部分
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/14 11:22
 */
@Data
public class ChangeGroupItem<T> {

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
     * 变化的要素
     */
    private T changeItem;

}
