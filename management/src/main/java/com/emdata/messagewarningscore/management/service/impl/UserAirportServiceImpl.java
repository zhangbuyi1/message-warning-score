package com.emdata.messagewarningscore.management.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emdata.messagewarningscore.common.dao.UserAirportMapper;
import com.emdata.messagewarningscore.common.dao.entity.UserAirportDO;
import com.emdata.messagewarningscore.management.service.IUserAirportService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @date: 2020/12/17
 * @author: sunming
 */
@Service
public class UserAirportServiceImpl extends ServiceImpl<UserAirportMapper, UserAirportDO> implements IUserAirportService {
}
