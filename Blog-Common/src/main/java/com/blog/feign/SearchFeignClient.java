package com.blog.feign;

import com.blog.config.FeignConfiguration;
import com.blog.exception.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yujunhong
 * @date 2021/6/22 09:30
 */
@FeignClient(name = "cloud-blog-search", configuration = FeignConfiguration.class)
public interface SearchFeignClient {
    /**
     * 通过博客uid删除ElasticSearch博客索引
     *
     * @param uid
     * @return
     */
    @PostMapping("/search/deleteElasticSearchByUid")
    ResultBody deleteElasticSearchByUid(@RequestParam(value = "uid") String uid);

    /**
     * 通过uids删除ElasticSearch博客索引
     *
     * @param uids
     * @return
     */
    @PostMapping("/search/deleteElasticSearchByUids")
    ResultBody deleteElasticSearchByUids(@RequestParam(value = "uids") String uids);

    /**
     * 初始化ElasticSearch索引
     *
     * @return
     */
    @PostMapping("/search/initElasticSearchIndex")
    ResultBody initElasticSearchIndex();

    /**
     * 通过uid来增加ElasticSearch索引
     *
     * @return
     */
    @PostMapping("/search/addElasticSearchIndexByUid")
    ResultBody addElasticSearchIndexByUid(@RequestParam(value = "uid") String uid);

}
