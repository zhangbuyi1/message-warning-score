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
@TableName("user")
public class UserDO extends PublicDO {

    @TableId(value = "id", type= IdType.AUTO)
    @ApiModelProperty(value = "主键id", example = "1")
    private Integer id;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "性别，1男2女", example = "1")
    private Integer sex;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "电话号码")
    private String telephone;

    @ApiModelProperty(value = "邮箱号")
    private String email;

    @ApiModelProperty(value = "盐")
    private String salt;

    @ApiModelProperty(value = "记录状态：1正常，9已删除", example = "1")
    private Integer state;

}
