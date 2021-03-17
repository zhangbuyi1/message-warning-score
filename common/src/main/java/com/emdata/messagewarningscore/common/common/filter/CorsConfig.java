package com.emdata.messagewarningscore.common.common.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * cors 跨域
 *
 * @author liuming
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 雷达图片前缀
     */
    public static String radarPrefix = "/data/radar";


    /**
     * @param registry
     */
    @Override

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            // 雷达源数据 图片以及原始数据
            registry.addResourceHandler(radarPrefix + "/**")
                    .addResourceLocations("file:D:\\data\\radar\\");
        } else {

            //  雷达源数据 图片以及原始数据
            registry.addResourceHandler(radarPrefix + "/**")
                    .addResourceLocations("file:/data/radar/");
        }
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 网关拦截器
        InterceptorRegistration gateReg = registry.addInterceptor(new ApiInterceptor());
        gateReg.addPathPatterns("/**");
        gateReg.excludePathPatterns("/sso/login");
        gateReg.excludePathPatterns("/users/**");
        gateReg.excludePathPatterns("/msg/**");
        gateReg.excludePathPatterns("/doc.html");
        gateReg.excludePathPatterns("/webjars/**");
        gateReg.excludePathPatterns("/swagger-resources/**");
        gateReg.excludePathPatterns("/taf_test/**");
        gateReg.excludePathPatterns("/mon/**");
        gateReg.excludePathPatterns(radarPrefix + "/**");
        gateReg.excludePathPatterns("/config/client_config");
        gateReg.excludePathPatterns("/pull/**");
    }

    /**
     * 过滤器处理跨域问题(过滤器在拦截器之前执行)
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 设置允许跨域请求的域名
        config.addAllowedOrigin("*");
        // 是否允许证书 不再默认开启
        config.setAllowCredentials(true);
        // 设置允许的方法
        config.addAllowedMethod("*");
        // 允许任何头
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }
}