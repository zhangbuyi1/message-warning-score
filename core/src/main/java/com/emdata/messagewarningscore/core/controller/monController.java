package com.emdata.messagewarningscore.core.controller;/**
 * Created by zhangshaohu on 2020/12/31.
 */

import com.alibaba.fastjson.JSON;
import com.emdata.messagewarningscore.common.accuracy.entity.ScoreThreshold;
import com.emdata.messagewarningscore.common.cache.LocalCacheClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: zhangshaohu
 * @date: 2020/12/31
 * @description:
 */
@RestController
public class monController {
    @GetMapping("/mon")
    public String mon() {
//        Map<String, List<ScoreThreshold>> thresholdsCache = LocalCacheClient.thresholdsCache;
//        return JSON.toJSONString(thresholdsCache);
        return null;
    }
}