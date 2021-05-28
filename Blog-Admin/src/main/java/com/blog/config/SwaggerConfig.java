package com.blog.config;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yujunhong
 * @date 2021/5/28 15:21
 */
@Configuration
public class SwaggerConfig {
    /**
     * 读取配置文件中配置,设置swagger是否可以打开 一般生产环境需要关闭
     */
//    @Value(value = "${swagger.enabled}")
    private boolean swaggerEnabled;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo())
                // 是否开启swagger
                .enable(true).select()
                // 扫描所有有注解(@ApiOperation)的api
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 指定路径路径处理 -不过滤任何路径
                .paths(PathSelectors.any())
                .build()
                // 设置授权信息
                .securitySchemes(securitySchemes())
                // 设置授权全局应用
                .securityContexts(securityContexts());
    }

    /**
     * 获取swagger显示信息
     *
     * @return ApiInfo
     * @author yujunhong
     * @date 2021/5/28 15:26
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("余俊宏个人博客--Spring Boot/Spring Cloud Alibaba").description("个人博客").version("1" +
                ".0").build();
    }

    /**
     * 设置授权信息
     *
     * @return 授权信息集合
     * @author yujunhong
     * @date 2021/5/28 15:35
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> securitySchemes = new ArrayList<>();
        securitySchemes.add(new ApiKey("Authorization", "Authorization", "header"));
        return securitySchemes;
    }

    /**
     * 授权信息全局应用
     *
     * @return 授权信息
     * @author yujunhong
     * @date 2021/5/28 15:42
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build());
        return securityContexts;
    }

    /**
     * 配置默认权限
     *
     * @author yujunhong
     * @date 2021/5/28 15:45
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("Authorization", authorizationScopes));
    }
}
