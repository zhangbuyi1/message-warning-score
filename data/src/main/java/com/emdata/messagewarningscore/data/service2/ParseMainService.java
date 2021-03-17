package com.emdata.messagewarningscore.data.service2;

import com.emdata.messagewarningscore.common.result.ResultData;

import java.io.FileInputStream;

/**
 * @Desc 解析预警文本
 * @Author lihongjiang
 * @Date 2020/12/24 10:31
 **/
public interface ParseMainService {

    /**
     * 解析预警文本适配器
     *
     * 1、通过类型和文件夹地址，区分调用哪种类型的接口，若类型为空，则调用所有接口
     *
     * @param type 类型123...；当为空时，执行全部。
     * @param startTime 年月范围开始；大于等于开始年月；为空时，执行全部年月；时间范围不能一个传一个空。
     * @param endTime 年月范围结束；小于等于结束年月；为空时，执行全部年月；结束年月必须大于等开始年月。
     * @param fileName 文件名；为空时，执行某个类型的年有范围内所有文件。不为空时，以上3个条件必须都传。
     **/
    ResultData parseTextContent(String type, String startTime, String endTime, String fileName);


    ResultData parseTextContent(String type, FileInputStream fileInput);

}
