package com.blog.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yujunhong
 * @date 2021/5/28 17:20
 */
@ConfigurationProperties(prefix = "audience")
@Component
@Data
public class Audience {
    /**
     * 用户id
     */
    private String clientId;
    /**
     * base64 密钥
     */
    private String base64Secret;

    /**
     * 名称
     */
    private String name;

    /**
     * 过期时间  秒
     */
    private String expiresSecond;
}
