package com.emdata.messagewarningscore.management.controller.vo;

import lombok.Data;

/**
 * @author changfeng
 * @description 菜单关系vo
 * @date 2020/2/26
 */
@Data
public class MenuRelationVo {

private Integer menuId;

private String menuName;

private MenuRelationVo childVo;
}