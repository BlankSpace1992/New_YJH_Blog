package com.cloud.blog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yujunhong
 * @date 2021/6/1 13:55
 */
@Configuration
@MapperScan(value = "com.blog.business.mapper")
public class BlogWebApplicationConfig {
}
