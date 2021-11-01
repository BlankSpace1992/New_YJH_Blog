package com.blog.search.controller;

import com.alibaba.fastjson.JSON;
import com.blog.business.web.domain.Blog;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.exception.ResultBody;
import com.blog.feign.WebFeignClient;
import com.blog.search.utils.ElasticSearchUtils;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/10/29 14:28
 */
@RestController
@RequestMapping(value = "/search")
@Api(value = "ElasticSearch相关接口", tags = {"ElasticSearch相关接口"})
public class ElasticSearchController {

    @Autowired
    private WebFeignClient webFeignClient;

    @Autowired
    private ElasticSearchUtils elasticSearchUtils;

    /**
     * ElasticSearch初始化索引
     *
     * @author yujunhong
     * @date 2021/10/29 14:29
     */
    @ApiOperation(value = "ElasticSearch初始化索引")
    @PostMapping("/initElasticSearchIndex")
    public ResultBody initElasticSearchIndex() {
        Long page = 1L;
        Long row = 10L;
        // 查询blog信息
        String result = (String) webFeignClient.getBlogBySearch(page, row).getResult();
        List<Blog> blogs = JSON.parseArray(result, Blog.class);
        for (Blog blog : blogs) {
            // 内容设置为0
            blog.setContent(StringUtils.EMPTY);
            elasticSearchUtils.addData(blog, blog.getUid());
        }
        return ResultBody.success();
    }

    /**
     * 通过ElasticSearch搜索博客
     *
     * @param keywords    关键词
     * @param currentPage 当前页数
     * @param pageSize    页行数
     * @return 通过ElasticSearch搜索博客
     * @author yujunhong
     * @date 2021/10/29 14:33
     */
    @ApiOperation(value = "通过ElasticSearch搜索博客")
    @GetMapping("/elasticSearchBlog")
    public ResultBody elasticSearchBlog(
            @RequestParam(required = false) String keywords,
            @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer
                    currentPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer
                    pageSize) {
        if (StringUtils.isEmpty(keywords)) {
            return ResultBody.error(BaseMessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        // 创建查询builder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 由于分词问题,可能查询不出任何数据,关键字段应加上keyword
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", keywords));
        return ResultBody.success(elasticSearchUtils.searchDataPage(currentPage, pageSize, boolQueryBuilder));
    }

    /**
     * 通过uids删除ElasticSearch博客索引
     *
     * @param uids 博客id集合
     * @return 通过uids删除ElasticSearch博客索引
     * @author yujunhong
     * @date 2021/10/29 14:35
     */
    @ApiOperation(value = "通过uids删除ElasticSearch博客索引", notes = "通过uids删除ElasticSearch博客索引", response = String.class)
    @PostMapping("/deleteElasticSearchByUids")
    public ResultBody deleteElasticSearchByUids(@RequestParam(required = true) String uids) {
        List<String> uidList = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, uids);
        for (String uid : uidList) {
            elasticSearchUtils.deleteDataById(uid);
        }
        return ResultBody.success();
    }

    /**
     * 通过博客uid删除ElasticSearch博客索引
     *
     * @param uid 博客id
     * @return 通过博客uid删除ElasticSearch博客索引
     * @author yujunhong
     * @date 2021/10/29 14:36
     */
    @ApiOperation(value = "通过博客uid删除ElasticSearch博客索引", notes = "通过uid删除博客", response = String.class)
    @PostMapping("/deleteElasticSearchByUid")
    public ResultBody deleteElasticSearchByUid(@RequestParam(required = true) String uid) {
        elasticSearchUtils.deleteDataById(uid);
        return ResultBody.success();
    }

    /**
     * ElasticSearch通过博客Uid添加索引
     *
     * @param uid 博客uid
     * @return ElasticSearch通过博客Uid添加索引
     * @author yujunhong
     * @date 2021/10/29 14:37
     */
    @ApiOperation(value = "ElasticSearch通过博客Uid添加索引", notes = "添加博客", response = String.class)
    @PostMapping("/addElasticSearchIndexByUid")
    public ResultBody addElasticSearchIndexByUid(@RequestParam(required = true) String uid) {
        String result = (String) webFeignClient.getBlogByUid(uid).getResult();
        Blog blogEntity = JSON.parseObject(result, Blog.class);
        if (StringUtils.isNull(blogEntity)) {
            return ResultBody.error(BaseMessageConf.INSERT_FAIL);
        }
        elasticSearchUtils.addData(blogEntity, uid);
        return ResultBody.success();
    }
}
