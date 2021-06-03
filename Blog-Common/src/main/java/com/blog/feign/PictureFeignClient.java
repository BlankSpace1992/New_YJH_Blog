package com.blog.feign;

import com.blog.config.FeignConfiguration;
import com.blog.fallback.PictureFeignFallBack;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yujunhong
 * @date 2021/6/3 14:49
 */
@FeignClient(name = "cloud-blog-picture", configuration = FeignConfiguration.class, fallback =
        PictureFeignFallBack.class)
public interface PictureFeignClient {

    /** 获取文件得信息接口
      * @author yujunhong
      * @date 2021/6/3 14:50
      * @param
      * @return
      */

}
