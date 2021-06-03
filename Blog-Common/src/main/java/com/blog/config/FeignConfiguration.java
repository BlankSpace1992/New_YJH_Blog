package com.blog.config;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * openFeign配置文件
 *
 * @author yujunhong
 * @date 2021/6/3 14:43
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password123");
    }

    /**
     * feign请求拦截器
     *
     * @return feign请求拦截器
     * @author yujunhong
     * @date 2021/6/3 14:45
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignBasicAuthRequestInterceptor();
    }
}
