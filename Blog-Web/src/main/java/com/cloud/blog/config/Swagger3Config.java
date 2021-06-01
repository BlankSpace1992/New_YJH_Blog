package com.cloud.blog.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger配置文件
 *
 * @author yujunhong
 * @date 2021/6/1 10:38
 */
@Configuration
public class Swagger3Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();

    }

    /**
     * 标题信息
     *
     * @author yujunhong
     * @date 2021/6/1 10:39
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("余俊宏个人博客--Spring Boot/Spring Cloud Alibaba")
                .description("个人博客")
                .version("1.0")
                .build();
    }
}
