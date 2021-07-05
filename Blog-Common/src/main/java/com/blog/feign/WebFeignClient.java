package com.blog.feign;

import com.blog.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yujunhong
 * @date 2021/6/22 09:30
 */
@FeignClient(name = "cloud-blog-web", configuration = FeignConfiguration.class)
public interface WebFeignClient {

    /**
     * 获取系统配置信息
     *
     * @param token token值
     * @return 系统配置信息
     * @author yujunhong
     * @date 2021/6/22 9:32
     */
    @GetMapping(value = "/oauth/getSystemConfig")
    String getSystemConfig(@RequestParam("token") String token);


    /**
     * 通过UID 获取博客信息
     *
     * @param uid 博客uid
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/22 9:34
     */
    @GetMapping(value = "/content/getBlogByUid")
    String getBlogByUid(@RequestParam(value = "uid") String uid);

    /**
     * 获取标签页相同的博客信息
     *
     * @param tagUid      标签uid
     * @param currentPage 当前页数
     * @param pageSize    显示行数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/22 9:42
     */
    @GetMapping(value = "/content/getSameBlogByTagUid")
    String getSameBlogByTagUid(@RequestParam(value = "tagUid") String tagUid,
                               @RequestParam(value = "currentPage", defaultValue = "1") Long currentPage,
                               @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize);

    /**
     * 通过博客uid 获取相同博客
     *
     * @param blogUid     博客uid
     * @param currentPage 当前页数
     * @param pageSize    显示行数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/22 9:46
     */
    @GetMapping(value = "/content/getSameBlogByBlogUid")
    String getSameBlogByBlogUid(@RequestParam(value = "blogUid") String blogUid,
                                @RequestParam(value = "currentPage", defaultValue = "1") Long currentPage,
                                @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize);

    /**
     * 获取博客列表
     *
     * @param currentPage 当前页数
     * @param pageSize    显示行数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/22 9:48
     */
    @GetMapping(value = "/index/getBlogBySearch")
    String getBlogBySearch(@RequestParam(value = "currentPage", defaultValue = "1") Long currentPage,
                           @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize);
}
