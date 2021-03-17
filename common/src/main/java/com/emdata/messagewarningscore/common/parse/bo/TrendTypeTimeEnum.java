package com.emdata.messagewarningscore.common.parse.bo;

/**
 * 趋势报变化时间的指示码<br/>
 * 使用指示码“FM”、“TL”或“AT”分别紧接相应的协调世界时（UTC）小时和分钟的时间组表明预报变化发生的时段或时刻
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/14 11:30
 */
public enum TrendTypeTimeEnum {

    /**
     * 表示变化的开始时间
     */
    FM,

    /**
     * 表示变化的结束时间
     */
    TL,

    /**
     * 表示变化的开始-结束时间
     */
    FM_TL,

    /**
     * 表示变化的确切时间，则变化组的开始时间和结束时间一致
     */
    AT,

    /**
     * 表示省略FM、TL，开始-结束，就是趋势报的有效时间（0-2h）
     */
    NO

}
