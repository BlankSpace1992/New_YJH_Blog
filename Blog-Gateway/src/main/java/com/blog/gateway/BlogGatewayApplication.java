package com.blog.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author yujunhong
 * @date 2021/6/1 17:17
 */
@SpringBootApplication
@EnableDiscoveryClient
public class BlogGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogGatewayApplication.class);
    }
}
