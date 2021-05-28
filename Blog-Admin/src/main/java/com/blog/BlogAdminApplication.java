package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

/**
 * @author yujunhong
 * @date 2021/5/28 14:46
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class BlogAdminApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(BlogAdminApplication.class);
    }
}
