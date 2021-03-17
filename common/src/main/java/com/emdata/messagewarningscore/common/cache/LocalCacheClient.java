package com.emdata.messagewarningscore.common.cache;

import com.emdata.messagewarningscore.common.accuracy.config.RaConfig;
import com.emdata.messagewarningscore.common.accuracy.config.RaSnConfig;
import com.emdata.messagewarningscore.common.accuracy.config.TSConfig;
import com.emdata.messagewarningscore.common.accuracy.entity.ScoreThreshold;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.common.utils.CollectionUtil;
import com.emdata.messagewarningscore.common.common.utils.Java8DateUtils;
import com.emdata.messagewarningscore.common.dao.entity.MetarSourceDO;
import com.emdata.messagewarningscore.common.metar.Metar;
import com.emdata.messagewarningscore.common.metar.entity.ChildCloud;
import com.emdata.messagewarningscore.common.metar.entity.Rvr;
import com.emdata.messagewarningscore.common.metar.entity.Vis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 缓存
 * @date: 2020/12/29
 * @author: sunming
 */
@Slf4j
public class LocalCacheClient {

    /**
     * 存放机场代码的缓存list
     */
    public static List<String> cacheList = new ArrayList<>();

}
