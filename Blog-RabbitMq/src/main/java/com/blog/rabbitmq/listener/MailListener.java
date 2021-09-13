package com.blog.rabbitmq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.blog.config.rabbit_mq.SendMailUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/9/10 16:56
 */
@Component
public class MailListener {
    @Autowired
    private SendMailUtils sendMailUtils;

    @RabbitListener(queues = "blog.email")
    public void sendMail(String result) {
        Map<String, String> map = JSON.parseObject(result, new TypeReference<Map<String, String>>() {
        });
        if (map != null) {
            sendMailUtils.sendEmail(
                    map.get("subject"),
                    map.get("receiver"),
                    map.get("text")
            );
        }
    }
}
