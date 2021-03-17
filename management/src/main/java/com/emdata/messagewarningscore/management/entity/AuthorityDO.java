package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author changfeng
 * @description
 * @date 2020/2/21
 */
@Data
@TableName("authority")
public class AuthorityDO extends PublicDO implements Serializable {

    private static final long serialVersionUID = 224576295646999547L;

    @TableId(value = "id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "权限地址")
    private String url;

    @ApiModelProperty(value = "权限级别", example = "1")
    private Integer level;

    @ApiModelProperty(value = "父级权限id.为空表示为根节点", example = "1")
    private Integer parentId;

    @ApiModelProperty(value = "记录状态：1正常，9已删除", example = "1")
    private Integer state;
}
