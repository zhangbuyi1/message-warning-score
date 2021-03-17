package com.emdata.messagewarningscore.data.radar.bo;/**
 * Created by zhangshaohu on 2021/1/26.
 */

import lombok.Builder;
import lombok.Data;

/**
 * @author: zhangshaohu
 * @date: 2021/1/26
 * @description:
 */
@Builder
@Data
public class RadarPathBO {
    /**
     * 原始名称
     */
    private String sourcePath;
    /**
     * 修改后名称
     */
    private String resetPath;
}