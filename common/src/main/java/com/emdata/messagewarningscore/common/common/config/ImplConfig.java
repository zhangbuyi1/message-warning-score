package com.emdata.messagewarningscore.common.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @date: 2020/12/10
 * @author: sunming
 */
@Component
@ConfigurationProperties(prefix = "warning")
@Data
public class ImplConfig {
    private Map<String, String> airportImpl;
    private Map<String, String> areaImpl;
    private Map<String, String> terminalImpl;
    private Map<String, String> mdrsImpl;
}
