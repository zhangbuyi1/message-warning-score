package com.emdata.messagewarningscore.data.http.config;/**
 * Created by zhangshaohu on 2021/1/19.
 */


import com.emdata.messagewarningscore.data.radar.service.RadarService;
import com.emdata.messagewarningscore.data.http.porxy.HttpPorxy;
import com.emdata.messagewarningscore.data.http.porxy.RestTemplateProxy;
import com.emdata.messagewarningscore.data.http.rule.impl.PollingRule;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@Component
public class ApplicationContest {
    @Bean
    public HttpPorxy buildPory() {
        return new RestTemplateProxy();
    }

    @Bean
    public FactoryBean<RadarService> buildBean(HttpPorxy httpPorxy, PollingRule pollingRule) {
        return new FactoryBean<RadarService>() {
            @Nullable
            @Override
            public RadarService getObject() throws Exception {
                return (RadarService) httpPorxy.createPorxy(getObjectType(),pollingRule);
            }
            @Nullable
            @Override
            public Class<?> getObjectType() {
                return RadarService.class;
            }
        };
    }


    @Bean
    public PollingRule buildPollingRule(ServerRuleConfig config) {
        return new PollingRule(config.getRadar());
    }
}