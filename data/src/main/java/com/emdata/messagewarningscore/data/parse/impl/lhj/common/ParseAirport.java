package com.emdata.messagewarningscore.data.parse.impl.lhj.common;

import org.springframework.stereotype.Service;

/**
 * @Desc ParseAirportTiServiceImpl
 * @Author lihongjiang
 * @Date 2020/12/23 17:52
 **/
@Service
public class ParseAirport {

    /**
     * 解析文本内容
     *
     * 解析方案：
     * 1、前端传入文件。
     * 2、java获取文本内容。
     * 3、java获取数据库配置的模板，并将模板进行分割，存入list。
     * 4、java将文本内容与模板的list内容进行匹配，通过后进入对应的解析类。
     * 5、java解析内容，返回结果。
     *
     * 模板分割方案：
     * 1、入库前，通过下画线/括号/...进行分割编辑。
     * 2、数据库名称：天气预警文本模板表。
     * 3、数据库字段：主键id, 模板类型(1机场 2区域 3终端 4...), 天气类型(A机场 1雷暴2能见度3低云4降雪5风  B区域... C终端...), 模板内容, 备用字段, 模板状态(0无效 1有效), 创建时间, 更新时间,
     *
     * 问题：
     * 1、是否存在重复模板，比如雷暴和风的模板可以共用，该怎么办？代码该如何处理？
     * 2、模板解析失败，怎么进行二次解析？
     * 3、如果警报员操作失误，编辑了模板，导致解析失败，该如何处理？
     * 4、文件是单一上传，还是批量上传？数据是从气象局复制回公司？多级文件夹，需要调用接口，待定文件夹范围进行数据转换。
     * 5、
     *
     * // 天气类型 风/雷暴/低云/能见度/降雪冻降水
     *
     * @param
     * @return
     **/

}
