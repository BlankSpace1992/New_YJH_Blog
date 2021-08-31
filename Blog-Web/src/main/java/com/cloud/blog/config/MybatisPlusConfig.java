package com.cloud.blog.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yujunhong
 * @date 2021/8/31 16:21
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * mybatis-plus分页插件
     * 文档：http://mp.baomidou.com
     *
     * @return PaginationInterceptor
     * @author yujunhong
     * @date 2021/6/3 11:37
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

/*    *//**
     * 打印 sql
     *//*
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        //格式化sql语句
        Properties properties = new Properties();
        properties.setProperty("format", "true");
        performanceInterceptor.setProperties(properties);
        return performanceInterceptor;
    }*/
}
