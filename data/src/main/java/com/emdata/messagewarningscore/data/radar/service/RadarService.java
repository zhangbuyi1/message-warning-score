package com.emdata.messagewarningscore.data.radar.service;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.emdata.messagewarningscore.data.http.annotation.ApiServer;
import com.emdata.messagewarningscore.data.radar.bo.RadarBO;
import com.emdata.messagewarningscore.data.radar.vo.RadarVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@ApiServer(url = "RADAR_SERVER")
public interface RadarService {
    /**
     * 发送post请求 得到雷达数据
     *
     * @param radarVO
     * @return
     */
    @PostMapping(value = "/radar/read_source")
    RadarBO analysisRadar(@RequestBody RadarVO radarVO);

    @GetMapping(value = "/radar/read_source_exist")
    String radarSourceExist();
}