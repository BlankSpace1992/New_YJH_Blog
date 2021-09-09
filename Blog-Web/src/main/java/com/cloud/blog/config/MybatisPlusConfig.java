package com.cloud.blog.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
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

    /**
     * 配置自动填充
     *
     * @author yujunhong
     * @date 2021/9/9 11:02
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        return globalConfig;
    }
}
