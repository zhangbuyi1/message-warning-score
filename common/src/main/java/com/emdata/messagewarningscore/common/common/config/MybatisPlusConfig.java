package com.emdata.messagewarningscore.common.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by sunming on 2019/6/20.
 */
@EnableTransactionManagement
@Configuration
@MapperScan({"com.emdata.messagewarningscore.management.dao","com.emdata.messagewarningscore.core.dao"})
public class MybatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
