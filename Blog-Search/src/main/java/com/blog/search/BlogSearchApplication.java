package com.blog.search;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author yujunhong
 * @date 2021/8/30 17:03
 */
@SpringBootApplication(scanBasePackages = "com.blog.business.search.service")
@EnableDiscoveryClient
@MapperScan(value = "com.blog.business.search.mapper")
@EnableFeignClients("com.blog.feign")
@EnableOpenApi
public class BlogSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogSearchApplication.class);
    }
}
