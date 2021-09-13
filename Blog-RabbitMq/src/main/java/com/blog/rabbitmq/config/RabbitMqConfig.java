package com.blog.rabbitmq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yujunhong
 * @date 2021/9/10 16:31
 */
@Configuration
public class RabbitMqConfig {

    public static final String BLOG_QUEUE_NAME = "blog.email";
    public static final String EXCHANGE_DIRECT = "blog.exchange";
    public static final String ROUTING_KEY_EMAIL = "blog.email";


    /**
     * 声名交换机
     *
     * @return 交换机
     * @author yujunhong
     * @date 2021/9/10 16:45
     */
    @Bean(value = EXCHANGE_DIRECT)
    public Exchange exchangeDirect() {
        // 声明路由交换机，durable:在rabbitmq重启后，交换机还在
        return ExchangeBuilder.directExchange(EXCHANGE_DIRECT).durable(true).build();
    }

    /**
     * 声名Email队列
     *
     * @return 队列
     * @author yujunhong
     * @date 2021/9/10 16:48
     */
    @Bean(value = BLOG_QUEUE_NAME)
    public Queue blogEmail() {
        return new Queue(BLOG_QUEUE_NAME);
    }

    /**
     * Email 队列绑定交换机并指定bindingKey
     *
     * @param queue    队列
     * @param exchange 交换机
     * @return 绑定类
     * @author yujunhong
     * @date 2021/9/10 16:50
     */
    @Bean
    public Binding bindingQueueForEmail(@Qualifier(BLOG_QUEUE_NAME) Queue queue,
                                        @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_EMAIL).noargs();
    }


    /**
     * 添加这个类进行序列化解析
     * 会自动识别
     * @param objectMapper json序列化实现类
     * @return mq 消息序列化工具
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
