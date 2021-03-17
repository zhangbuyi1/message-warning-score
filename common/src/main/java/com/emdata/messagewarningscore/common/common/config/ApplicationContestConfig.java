package com.emdata.messagewarningscore.common.common.config;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@Configuration
public class ApplicationContestConfig {
    @Bean
    public RestTemplate buildRestTemplate() {
        return new RestTemplate();
    }


}