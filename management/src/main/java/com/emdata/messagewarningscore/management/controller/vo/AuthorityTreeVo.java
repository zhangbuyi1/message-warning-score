package com.emdata.messagewarningscore.management.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 部门查询返回数据实体类
 * 
 * @author changfeng
 * @date 2019/06/22
 */
@Data
public class AuthorityTreeVo extends BaseQuery{

    @ApiModelProperty(value = "id", example = "1")
    private Integer id;

    private String name;

    @ApiModelProperty(value = "level", example = "1")
    private Integer level;

    private String url;

    @ApiModelProperty(value = "parentId", example = "1")
    private Integer parentId;

    private List<AuthorityTreeVo> children;

}
