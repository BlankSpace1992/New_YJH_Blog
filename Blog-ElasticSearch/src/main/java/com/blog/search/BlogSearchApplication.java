package com.blog.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author yujunhong
 * @date 2021/10/29 10:35
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class})
@EnableTransactionManagement
@EnableOpenApi
@EnableDiscoveryClient
@EnableFeignClients("com.blog.feign")
public class BlogSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogSearchApplication.class, args);
    }
}
