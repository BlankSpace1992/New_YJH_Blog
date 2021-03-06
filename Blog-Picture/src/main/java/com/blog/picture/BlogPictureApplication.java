package com.blog.picture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author yujunhong
 * @date 2021/6/311:28
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableFeignClients("com.blog.feign")
@EnableOpenApi
@ComponentScan(basePackages = {"com.blog.picture.controller", "com.blog.utils", "com.blog.business.picture", "com" +
        ".blog.picture.config", "com.blog.config.redis"})
public class BlogPictureApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogPictureApplication.class);
    }
}
