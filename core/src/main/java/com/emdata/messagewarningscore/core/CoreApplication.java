package com.emdata.messagewarningscore.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan({"com.emdata.messagewarningscore.common", "com.emdata.messagewarningscore.core", "com.emdata.messagewarningscore.data", "com.emdata.messagewarningscore.management"})
@EnableCaching
@EnableScheduling
@MapperScan(basePackages = {"com.emdata.messagewarningscore.common.dao"})
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}