package com.blog.picture.config;

import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-plus插件配置
 *
 * @author yujunhong
 * @date 2021/6/3 11:34
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

    @Bean
    public H2KeyGenerator getH2KeyGenerator() {
        return new H2KeyGenerator();
    }
}
