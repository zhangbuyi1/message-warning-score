package com.emdata.messagewarningscore.common.service.bo;/**
 * Created by zhangshaohu on 2021/1/14.
 */

import com.emdata.messagewarningscore.common.dao.entity.CrPictureDO;
import com.emdata.messagewarningscore.common.dao.entity.RadarInfoDO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/14
 * @description:
 */
@Data
public class CrPictureBO {
    /**
     * 雷达图片
     */
    private List<CrPictureDO> crPictureDOS;
    /**
     * 雷达基础数据
     */
    private RadarInfoDO radarInfoDO;
}