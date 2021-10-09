package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.util.TimeZone;

/**
 * @author yujunhong
 * @date 2021/5/28 14:46
 */
@SpringBootApplication(scanBasePackages = {"com.blog.config", "com.blog.security","com.blog.controller", "com.blog.business.admin", "com.blog" +
        ".business.utils", "com.blog.business.web","com.blog.business.picture","com.blog.utils"})
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients("com.blog.feign")
@EnableOpenApi
public class BlogAdminApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(BlogAdminApplication.class);
    }
}
