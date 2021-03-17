package com.emdata.messagewarningscore.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by sunming on 2019/8/5.
 */
@Data
@TableName("role_user")
public class RoleUserDO extends PublicDO {

    @TableId(value = "id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "角色id", example = "1")
    private Integer roleId;

    @ApiModelProperty(value = "用户id", example = "1")
    private Integer userId;

    @ApiModelProperty(value = "记录状态：1正常，9已删除", example = "1")
    private Integer state;
}
