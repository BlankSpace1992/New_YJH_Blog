package com.blog.rabbitmq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author yujunhong
 * @date 2021/9/10 16:26
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableRabbit
@EnableOpenApi
@EnableFeignClients("com.blog.feign")
@ComponentScan(basePackages = {"com.blog.config","com.blog.rabbitmq.config","com.blog.rabbitmq.listener"})
public class BlogRabbitMqApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogRabbitMqApplication.class,args);
    }
}
