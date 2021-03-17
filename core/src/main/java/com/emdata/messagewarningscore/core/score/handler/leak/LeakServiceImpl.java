package com.emdata.messagewarningscore.core.score.handler.leak;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.emdata.messagewarningscore.common.accuracy.enums.NatureEnum;
import com.emdata.messagewarningscore.common.common.utils.SpringUtil;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.enums.WarningTypeEnum;
import com.emdata.messagewarningscore.core.score.handler.LeakService;
import com.emdata.messagewarningscore.core.score.qualitative.Qualitative;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description: 机场警报漏报处理
 */
@Service("LeakServiceImpl")
public class LeakServiceImpl implements LeakService {
    private static Map<WarningTypeEnum, String> objectStringMap = new HashMap<WarningTypeEnum, String>() {{
        put(WarningTypeEnum.AIRPORT_WARNING, "AirportQualitativeImpl");
        put(WarningTypeEnum.MDRS_WARNING, "MdrsQualitativeImpl");
        put(WarningTypeEnum.TERMINAL_WARNING, "TerminalQualitativeImpl");
    }};


    @Override
    public List<SeriousDO> run(WarningTypeEnum warningTypeEnum, List<SeriousDO> seriousDOS) {
        Qualitative qualitative = (Qualitative) SpringUtil.getBean(objectStringMap.get(warningTypeEnum));
        List<SeriousDO> collect = seriousDOS.stream().filter(s -> {
            NatureEnum natureEnum = qualitative.runLeak(s);
            return natureEnum.equals(NatureEnum.NOT_SCORE) || natureEnum.equals(NatureEnum.LEAK);
        }).collect(Collectors.toList());
        return collect;
    }
}