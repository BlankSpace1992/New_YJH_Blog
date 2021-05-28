package com.cloud.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author yujunhong
 * @date 2021/5/26 15:24
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CloudBlogWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudBlogWebApplication.class);
    }
}
