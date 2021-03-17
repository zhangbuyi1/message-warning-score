package com.emdata.messagewarningscore.data.http.config;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.emdata.messagewarningscore.data.http.config.source.Server;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@Component
@ConfigurationProperties(prefix = "server.rule")
@Order(0)
@Data
public class ServerRuleConfig {
    private Map<String, List<Server>> radar;
}