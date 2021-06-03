package com.blog.picture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author yujunhong
 * @date 2021/6/3 11:28
 */
@SpringBootApplication
@EnableDiscoveryClient
public class BlogPictureApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogPictureApplication.class);
    }
}
