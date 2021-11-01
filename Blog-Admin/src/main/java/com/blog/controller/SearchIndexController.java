package com.blog.controller;

import com.blog.exception.ResultBody;
import com.blog.feign.SearchFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/10/29 14:41
 */
@RestController
@RequestMapping("/search")
@Api(value = "索引维护相关接口", tags = {"索引维护相关接口"})
public class SearchIndexController {
    @Autowired
    private SearchFeignClient searchFeignClient;

    /**
     * 初始化ElasticSearch索引
     *
     * @author yujunhong
     * @date 2021/10/29 14:52
     */
    @ApiOperation(value = "初始化ElasticSearch索引", notes = "初始化solr索引")
    @PostMapping("/initElasticIndex")
    public ResultBody initElasticIndex() {
        return searchFeignClient.initElasticSearchIndex();
    }
}
