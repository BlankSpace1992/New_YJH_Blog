package com.blog.picture.config;

import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
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
     * 相当于顶部的：
     * {@code @MapperScan("com.baomidou.springboot.mapper*")}
     * 这里可以扩展，比如使用配置文件来配置扫描Mapper的路径
     *
     * @return MapperScannerConfigurer
     * @author yujunhong
     * @date 2021/6/3 11:36
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage("com.blog.business.picture");
        return scannerConfigurer;
    }

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
