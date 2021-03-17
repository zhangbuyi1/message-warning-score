package com.emdata.messagewarningscore.management.controller.vo;

import com.emdata.messagewarningscore.management.entity.UserDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by changfeng on 2019/8/5.
 */
@Data
public class UserVO extends UserDO {

    @ApiModelProperty(value = "角色拼接")
    private String roleStr;
}
