package com.emdata.messagewarningscore.management.service.bo;

import lombok.Data;

import java.util.Date;

/**
 * @author: sunming
 * @date: 2020/1/9
 * @description:
 */
@Data
public class LogBO {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 操作类型
     */
    private Integer type;

    /**
     * 地区名
     */
    private String contents;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 一级菜单id
     */
    private Integer menuIdLevelOne;

    /**
     * 二级菜单id
     */
    private Integer menuIdLevelTwo;

    /**
     * 三级菜单id
     */
    private Integer menuIdLevelThree;

    /**
     * 一级菜单name
     */
    private String menuNameLevelOne;

    /**
     * 一级菜单name
     */
    private String menuNameLevelTwo;

    /**
     * 一级菜单name
     */
    private String menuNameLevelThree;


}
