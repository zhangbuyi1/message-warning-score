package com.emdata.messagewarningscore.data.parse.bo;

import lombok.Data;

import java.util.Date;

/**
 * @description: 地区和制作时间实体类
 * @date: 2020/12/8
 * @author: sunming
 */
@Data
public class AreaAndTimeBO {

    private String title;

    private String area;

    private String releaseTime;

    @Data
    public static class StartAndEndTimeBO {

        // yyyy-MM-dd HH:mm:ss
        private Date releaseTime;
        // yyyy-MM-dd HH:mm:ss
        private Date startTime;
        // yyyy-MM-dd HH:mm:ss
        private Date endTime;
    }
}
