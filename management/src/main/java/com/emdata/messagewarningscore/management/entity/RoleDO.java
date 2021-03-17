package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author changfeng
 * 
 * @description 
 * @date 2019/12/16
 */
@Data
@TableName("role")
public class RoleDO extends PublicDO {

    @TableId(value = "id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "记录状态：1正常，9已删除", example = "1")
    private Integer state;
}
