package com.emdata.messagewarningscore.common.common.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by sunming on 2019/6/17.
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.emdata.messagewarningscore"))
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder().title("预警评分系统")
                .description("预警评分系统").version("1.0.0").build();
    }
}
