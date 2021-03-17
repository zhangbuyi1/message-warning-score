package com.emdata.messagewarningscore.data.schedule;

import com.emdata.messagewarningscore.common.common.config.ImplConfig;
import com.emdata.messagewarningscore.data.service.AlertContentResolveService;
import com.emdata.messagewarningscore.data.service.AlertOriginalContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @description: 定时任务
 * @date: 2020/12/10
 * @author: sunming
 */
@Service
public class ScheduleTasks {

    @Autowired
    private AlertOriginalContentService alertOriginalContentService;

    @Autowired
    private AlertContentResolveService alertContentResolveService;

    @Autowired
    private ImplConfig implConfig;

    /**
     * 解析警报原始文本（机场、终端区、区域、MDSI）
     */
    public void parseOriginalText(String folder, String name) {
        alertOriginalContentService.parseOriginalText(folder, name);
    }

    /**
     * 解析正文内容（机场、终端区、区域、MDSI）
     **/
    public void parseTextContent(String fileName){
        alertContentResolveService.parseTextContent(fileName);
    }


    /**
     * 接入mdrs文本进行解析
     */
    public void accessMdrsText() {
        String airportCode = "ZSSS";
        String filePath = "D:\\PWSMR9E07.121.doc";
        File file = new File(filePath);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //mdrsShImpl
        String implName = implConfig.getMdrsImpl().get(airportCode);
//        parseWarningText.parse(inputStream, implName, "");
    }

}
