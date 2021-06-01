package com.blog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yujunhong
 * @date 2021/5/31 15:22
 */
@Configuration
@MapperScan(value = "com.blog.business.mapper")
public class ApplicationConfig {
}
