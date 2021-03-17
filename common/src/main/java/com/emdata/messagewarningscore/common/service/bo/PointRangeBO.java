package com.emdata.messagewarningscore.common.service.bo;/**
 * Created by zhangshaohu on 2021/1/14.
 */

import com.emdata.messagewarningscore.common.dao.entity.AirPointDO;
import com.emdata.messagewarningscore.common.enums.RangeTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/14
 * @description:
 */
@Data
public class PointRangeBO {
    /**
     * 类型
     */
    private RangeTypeEnum rangeTypeEnum;
    /**
     * 所有的点
     */
    private List<AirPointDO> all;
    /**
     * 名称
     */
    private String name;
    /**
     * 宽度
     */
    private Double wide;
    private Integer locationInfoId;

}