package com.emdata.messagewarningscore.data.service2.impl.adaptee;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Desc 解析预警文本适配器（类型：机场、区域、终端...）
 * @Author lihongjiang
 * @Date 2020/12/24 10:33
 **/
@Component
public class ParseAlertTextBases {

    // 机场预警根目录
    private static final String AIRPORT_ROOT = "/airport/";
    // 区域预警根目录
    private static final String AREA_ROOT = "/area/";
    // 终端预警根目录
    private static final String TERMINAL_ROOT = "/terminal/";
    // MDSI预警根目录
    private static final String MDSI_ROOT = "/mdsi/";

    /**
     * 通过参数，获取文件
     *
     **/
    public List<String> getFilesByParam(String type, String startTime, String endTime, String fileName) {
        // 将文件转换成正常的存储内容
        String filePath = "D:\\em-data\\眼控系统\\气象\\机场警报解析\\机场警报-2020\\" + startTime + "\\" + fileName + ".doc";
        if ("1".equals(type)) {
            filePath = AIRPORT_ROOT;
        }else if("2".equals(type)){
            filePath = AREA_ROOT;
        }else if("3".equals(type)){
            filePath = TERMINAL_ROOT;
        }else if("4".equals(type)){
            filePath = MDSI_ROOT;
        }
        List<String> fileUrls = Lists.newArrayList();
        if (StringUtils.isNotBlank(fileName)) {
            // 文件名不为空，说明只转换一个文件
            fileUrls.add(filePath + startTime + fileName + ".doc");
        }else{
            // 文件名不为空，说明转换一个/多个文件夹中所有的文件....................需要手工实现.....................
            List<String> fileList = null;
            for (String file : fileList) {
                // 拼接文件地址年+月+文件名
                fileUrls.add(filePath + fileName + ".doc");
            }
        }
        return fileUrls;
    }

    /**
     * 保存文件记录表
     **/
    public void saveFileRecords() {
        // 通过参数获取文件

        // 将文件转换成正常的存储内容

        //

    }

    /**
     * 更新文件记录表
     **/
    public void updateFileRecords(Integer status) {
        // 通过参数获取文件

        // 将文件转换成正常的存储内容

        //

    }

}
