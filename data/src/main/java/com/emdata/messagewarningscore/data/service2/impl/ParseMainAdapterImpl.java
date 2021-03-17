package com.emdata.messagewarningscore.data.service2.impl;

import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.data.service2.ParseTextContentService;
import com.emdata.messagewarningscore.data.service2.ParseMainService;
import com.emdata.messagewarningscore.data.service2.impl.adaptee.ParseAlertTextBases;
import com.emdata.messagewarningscore.data.service2.impl.type.IAirportTextAdapterImpl;
import com.emdata.messagewarningscore.data.service2.impl.type.IAreaTextServiceImpl;
import com.emdata.messagewarningscore.data.service2.impl.type.IMdsiTextServiceImpl;
import com.emdata.messagewarningscore.data.service2.impl.type.ITerminalTextServiceImpl;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @Desc 解析预警文本适配器（类型：机场、区域、终端...）
 * @Author lihongjiang
 * @Date 2020/12/24 10:33
 **/
@Service
public class ParseMainAdapterImpl extends ParseAlertTextBases implements ParseMainService {

    /**
     * 解析预警文本（适配器）
     *
     * 1、通过类型和文件夹地址，区分调用哪种类型的接口，若类型为空，则调用所有接口
     *
     * @param type 类型123...；当为空时，执行全部。
     * @param startTime 年月范围开始；大于等于开始年月；为空时，执行全部年月；时间范围不能一个传一个空。
     * @param endTime 年月范围结束；小于等于结束年月；为空时，执行全部年月；结束年月必须大于等开始年月。
     * @param fileName 文件名；为空时，执行某个类型的年有范围内所有文件。不为空时，以上3个条件必须都传。
     **/
    @Override
    public ResultData parseTextContent(String type, String startTime, String endTime, String fileName) {
        // 解析接口 1机场 2区域 3终端 4MDSI
        ParseTextContentService parseTextContentService = null;
        if ("1".equals(type)) {
            parseTextContentService = new IAirportTextAdapterImpl();
        }else if("2".equals(type)){
            parseTextContentService = new IAreaTextServiceImpl();
        }else if("3".equals(type)){
            parseTextContentService = new ITerminalTextServiceImpl();
        }else if("4".equals(type)){
            parseTextContentService = new IMdsiTextServiceImpl();
        }
        // 接口未成功加载，则不需要执行。
        if (null == parseTextContentService) {
            return ResultData.error("未加载到接口的实现类，请检查类型参数!");
        }
        // 通过参数，获取所有文件
        List<String> files = super.getFilesByParam(type, startTime, endTime, fileName);
        if (null == files) {
            return ResultData.error("未查询到文件!");
        }
        // 遍历解析所有文件
        for (String fileStr : files) {
            // 文件转文件输入流
            File file = new File(fileStr);
            FileInputStream fileInput = null;
            try {
                fileInput = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 将文件信息存入记录表，状态为：0待解析。
            super.saveFileRecords();
            // 记录表解析状态 0待解析 1解析成功 2解析失败
            int status = 2;
            // 将文件转换成正常的格式
            ResultData<AlertOriginalTextDO> resultData = parseTextContentService.fileFormatConver(fileInput);
            if (null != resultData && resultData.isSuccess()) {
                AlertOriginalTextDO airportAlert = resultData.getData();
                // 解析文件内容，并保存解析后的内容
                ResultData<List<AlertContentResolveDO>> listResultData = parseTextContentService.parseTextContent(airportAlert);
                if (null != listResultData && listResultData.isSuccess()) {
                    List<AlertContentResolveDO> alertContents = listResultData.getData();
                    // 保存数据
                    parseTextContentService.saveTextContent(alertContents);
                    // 设置记录表记录状态为：1解析成功
                    status = 1;
                }
            }
            // 更新记录表的状态为：成功/失败。（方便其它线程处理未解析成功的数据）
            super.updateFileRecords(status);
        }
        return ResultData.success();
    }

    @Override
    public ResultData parseTextContent(String type, FileInputStream fileInput) {
        // 解析接口 1机场 2区域 3终端 4MDSI
        ParseTextContentService parseTextContentService = null;
        if ("1".equals(type)) {
            parseTextContentService = new IAirportTextAdapterImpl();
        }else if("2".equals(type)){
            parseTextContentService = new IAreaTextServiceImpl();
        }else if("3".equals(type)){
            parseTextContentService = new ITerminalTextServiceImpl();
        }else if("4".equals(type)){
            parseTextContentService = new IMdsiTextServiceImpl();
        }
        // 接口未成功加载，则不需要执行。
        if (null == parseTextContentService) {
            return ResultData.error("未加载到接口的实现类，请检查类型参数!");
        }
        // 记录表解析状态 0待解析 1解析成功 2解析失败
        int status = 2;
        // 将文件转换成正常的格式
        ResultData<AlertOriginalTextDO> resultData = parseTextContentService.fileFormatConver(fileInput);
        if (null != resultData && resultData.isSuccess()) {
            AlertOriginalTextDO airportAlert = resultData.getData();
            // 解析文件内容，并保存解析后的内容
            ResultData<List<AlertContentResolveDO>> listResultData = parseTextContentService.parseTextContent(airportAlert);
            if (null != listResultData && listResultData.isSuccess()) {
                List<AlertContentResolveDO> alertContents = listResultData.getData();
                // 保存数据
                parseTextContentService.saveTextContent(alertContents);
                // 设置记录表记录状态为：1解析成功
                status = 1;
            }
        }
        // 更新记录表的状态为：成功/失败。（方便其它线程处理未解析成功的数据）
        super.updateFileRecords(status);
        return ResultData.success();
    }
}
