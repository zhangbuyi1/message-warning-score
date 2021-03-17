package com.emdata.messagewarningscore.common.parse.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 趋势报文对象，继承与metar报文对象，多了变化组
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/10/10 10:48
 */
@Data()
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TrendElement extends MetarElement {

    /**
     * 变化组
     */
    private List<ChangeGroup> trendCroups;

}
