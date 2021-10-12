package com.cloud.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yujunhong
 * @date 2021/9/10 11:06
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前系统信息
        String property = System.getProperty("os.name");
        // windows系统
        if (property.toLowerCase().startsWith("win")) {
            registry.addResourceHandler("/blog/**").addResourceLocations("file:D:/mogu_blog/data/blog/");
        }
        // Linux或者mac
        else {
            registry.addResourceHandler("/blog/**").addResourceLocations("file:/tmp/mogu_blog/data/blog/");
        }
    }
}
