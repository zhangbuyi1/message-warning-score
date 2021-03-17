package com.emdata.messagewarningscore.data.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.common.constant.WarningConstants;
import com.emdata.messagewarningscore.common.common.utils.SpringUtil;
import com.emdata.messagewarningscore.common.dao.AlertContentResolveMapper;
import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;
import com.emdata.messagewarningscore.common.dao.entity.AlertOriginalTextDO;
import com.emdata.messagewarningscore.common.dao.entity.LocationInfoDO;
import com.emdata.messagewarningscore.common.result.ResultData;
import com.emdata.messagewarningscore.common.service.ILocationInfoService;
import com.emdata.messagewarningscore.data.parse.impl.ParseAirportTextServiceImpl;
import com.emdata.messagewarningscore.data.parse.impl.ParseAreaTextImpl;
import com.emdata.messagewarningscore.data.parse.impl.ParseMdrsTextImpl;
import com.emdata.messagewarningscore.data.parse.impl.ParseTerminalTextServiceImpl;
import com.emdata.messagewarningscore.data.parse.interfase.ParseTextService;
import com.emdata.messagewarningscore.data.service.AlertContentResolveService;
import com.emdata.messagewarningscore.data.service.AlertOriginalContentService;
import com.emdata.messagewarningscore.data.service.IAirportAreaTerminalService;
import com.emdata.messagewarningscore.data.service.IMdrsTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc AlertContentResolveServiceImpl 警报内容解析
 * @Author lihongjiang
 * @Date 2021/1/7 10:01
 **/
@Slf4j
@Service
public class AlertContentResolveServiceImpl extends ServiceImpl<AlertContentResolveMapper, AlertContentResolveDO> implements AlertContentResolveService {

    @Autowired
    private AlertContentResolveService alertContentResolveService;

    @Autowired
    private AlertOriginalContentService alertOriginalContentService;

    @Autowired
    private ILocationInfoService iLocationInfoService;

    @Autowired
    private IAirportAreaTerminalService airportAreaTerminalService;

    @Autowired
    private IMdrsTextService mdrsTextService;

    /**
     * 相同表结构的放在一个map里
     */
    public static Map<String, Class> airportAreaTerminalMap = new HashMap<String, Class>() {
        {
            put(WarningConstants.AIRPORT_KEY, ParseAirportTextServiceImpl.class);
            put(WarningConstants.TERMINAL_KEY, ParseTerminalTextServiceImpl.class);
            put(WarningConstants.AREA_KEY, ParseAreaTextImpl.class);
        }
    };

    public static Map<String, Class> mdrsMap = new HashMap<String, Class>() {
        {
            put(WarningConstants.MDRS_TEXT, ParseMdrsTextImpl.class);
        }
    };

    @Override
    public ResultData parseTextContent(String fileName) {
        // 查询位置id
        List<LocationInfoDO> locationInfos = iLocationInfoService.list();
        if (null == locationInfos) {
            return ResultData.error("未配置位置信息!");
        }
        // 查询未解析的原始警报（机场、终端区、区域）
        LambdaQueryWrapper<AlertOriginalTextDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ne(true, AlertOriginalTextDO::getAlertType, "4");
        lambdaQueryWrapper.ne(true, AlertOriginalTextDO::getStatus, "1");
        lambdaQueryWrapper.ne(true, AlertOriginalTextDO::getStatus, "3");
        if (StringUtils.isNotBlank(fileName)) {
            lambdaQueryWrapper.like(AlertOriginalTextDO::getAlertFileName, fileName);
        }
        List<AlertOriginalTextDO> alertOriginalTexts = alertOriginalContentService.list(lambdaQueryWrapper);
        if (null == alertOriginalTexts || alertOriginalTexts.size()<1) {
            return ResultData.error("无待解析的警报信息!");
        }
        for (AlertOriginalTextDO alertOriginalText : alertOriginalTexts) {
            // 依次解析原始文本，入库并返回解析状态。
            Integer integer = parseImportantParam(alertOriginalText, locationInfos);
            // 更新原始文本状态为解析成功
            alertOriginalText.setStatus(integer);
            alertOriginalContentService.updateById(alertOriginalText);
        }


//        // 如果警报是mdrs
//        if (mdrsMap.containsKey(warningImplType)) {
//            Class aClass = mdrsMap.get(warningImplType);
//            ParseMdrsInterface mdrsInterface = (ParseMdrsInterface) SpringUtil.getBean(aClass);
//            List<MdrsTextDO> mdrsTextDOS = mdrsInterface.parseMdrsText(in);
//            // 入库
////            mdrsTextService.saveBatch(mdrsTextDOS);
//        }

        return ResultData.success();
    }

    /**
     * 解析文本正文的重要参数
     **/
    public Integer parseImportantParam(AlertOriginalTextDO alertOriginalText, List<LocationInfoDO> locationInfos){
        int originalStatus = 2;
        // 获取文件名中的警报类型
        String alertFileName = alertOriginalText.getAlertFileName();
        String type = alertFileName.split("\\.")[1];
        // 如果警报是机场，区域或者终端区
        ParseTextService parseTextService = null;
        if (airportAreaTerminalMap.containsKey(type)) {
            Class aClass = airportAreaTerminalMap.get(type);
            parseTextService = (ParseTextService) SpringUtil.getBean(aClass);
        }
        if (null == parseTextService) {
            return originalStatus;
        }
        // 解析警报的原始正文，获取正文中的重要参数，并保存。
        ResultData<List<AlertContentResolveDO>> listResultData = parseTextService.getImportantParam(alertOriginalText);
        if (null != listResultData && listResultData.isSuccess()) {
            List<AlertContentResolveDO> alertContentList = listResultData.getData();
            log.info("转换文档内容为:{}", alertContentList);
            // 转换后的文本中有空数据时，从原始文本中添加。
            if (null != alertContentList && alertContentList.size()>0) {
                for (AlertContentResolveDO alertContentResolve : alertContentList) {
                    // 设置警报原始文本id
                    alertContentResolve.setAlertOriginalTextId(alertOriginalText.getId());
                    // 设置位置id
                    for (LocationInfoDO locationInfo : locationInfos) {
                        if (locationInfo.getName().contains(alertContentResolve.getAffectedArea())) {
                            alertContentResolve.setLocationInfoId(locationInfo.getId());
                            break;
                        }
                    }
                    // 机场信息

                    // 文件名
                    alertContentResolve.setAlertFileName(alertFileName);
                    // 发布抬头
                    alertContentResolve.setReleaseTitle(alertOriginalText.getReleaseTitle());
                    // 发布时间
                    alertContentResolve.setReleaseTime(alertOriginalText.getReleaseTime());
                    // 范围
                    String predictRange = alertContentResolve.getPredictRange();
                    if (StringUtils.isNotBlank(predictRange) && predictRange.length()>100) {
                        alertContentResolve.setPredictRange(predictRange.substring(0, 99));
                    }
                    // 子天气类型
                    String subWeatherType = alertContentResolve.getSubWeatherType();
                    if (StringUtils.isNotBlank(subWeatherType) && subWeatherType.length()>50) {
                        alertContentResolve.setSubWeatherType(subWeatherType.substring(0, 49));
                    }
                }
                // 入库
                boolean b = alertContentResolveService.saveBatch(alertContentList);
                if (b) {
                    originalStatus = 1;
                }
            }
        }
        return originalStatus;
    }

}
