package com.blog.config.rabbit_mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

/**
 * @author yujunhong
 * @date 2021/9/10 16:57
 */
@Component
public class SendMailUtils {
    @Value(value = "${spring.mail.username}")
    public String senderName;

    @Resource
    private JavaMailSenderImpl mailSender;

    /**
     * 发送邮件
     *
     * @param subject  主题
     * @param receiver 接收者
     * @param text     文本
     * @author yujunhong
     * @date 2021/9/10 17:08
     */
    public void sendEmail(String subject,String receiver,String text) {
        try {
            //创建一个复杂的消息邮件
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            //multipart:true
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setSubject(subject);

            helper.setText(text, true);
            //邮件接收人
            helper.setTo(receiver);

            //邮件发送者
            helper.setFrom(senderName);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
