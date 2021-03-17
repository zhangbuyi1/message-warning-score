package com.emdata.messagewarningscore.common.parse.bo;

/**
 * 趋势报变化指示码<br/>
 * 趋势预报应当以变化指示码“BECMG”或“TEMPO”标明
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/14 11:30
 */
public enum TrendTypeCodeEnum {

    /**
     * 趋势预报使用指示码“BECMG”来描述气象要素以规则或不规则的速度，达到或经过特定值的预期变化
     */
    BECMG,

    /**
     * 指示码“TEMPO”来描述气象要素达到或经过特定值的预期短暂波动
     */
    TEMPO

}
