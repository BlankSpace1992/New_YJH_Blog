package com.blog.feign;

import com.blog.config.FeignConfiguration;
import com.blog.exception.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author yujunhong
 * @date 2021/6/3 14:16
 */
@FeignClient(name = "cloud-blog-admin", configuration = FeignConfiguration.class)
public interface AdminFeignClient {

    /**
     * 获取配置信息
     *
     * @return 获取配置信息
     * @author yujunhong
     * @date 2021/6/3 14:47
     */
    @GetMapping(value = "/systemConfig/getSystemConfig")
    ResultBody getSystemConfig();
}
