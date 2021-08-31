package com.blog.search.controller;

import com.alibaba.fastjson.JSON;
import com.blog.business.search.domain.ElasticSearchVO;
import com.blog.business.search.mapper.BlogSearchMapper;
import com.blog.business.search.service.ElasticSearchService;
import com.blog.business.web.domain.Blog;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.exception.ResultBody;
import com.blog.feign.WebFeignClient;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/8/31 10:42
 */
@RestController
@RequestMapping(value = "/search")
@Api(value = "ElasticSearch搜索管理模块", tags = "ElasticSearch搜索管理模块")
public class ElasticSearchController {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private BlogSearchMapper blogSearchMapper;
    @Autowired
    private WebFeignClient webFeignClient;

    /**
     * 通过ElasticSearch搜索博客
     *
     * @param keywords    key值
     * @param pageSize    页行数
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 13:49
     */
    @ApiOperation(value = "通过ElasticSearch搜索博客")
    @GetMapping("/elasticSearchBlog")
    public ResultBody searchBlog(@RequestParam(required = false) String keywords,
                                 @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer
                                         currentPage,
                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer
                                         pageSize) {
        if (StringUtils.isEmpty(keywords)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        Map<String, Object> search = elasticSearchService.search(keywords, currentPage, pageSize);
        return ResultBody.success(search);
    }

    /**
     * 通过uids删除ElasticSearch博客索引--批量删除
     *
     * @param uids 博客主键-多个
     * @return ResultBody
     * @author yujunhong
     * @date 2021/8/31 13:52
     */
    @ApiOperation(value = "通过uids删除ElasticSearch博客索引--批量删除")
    @PostMapping(value = "/deleteElasticSearchByIds")
    public ResultBody deleteElasticSearchByIds(@RequestParam String uids) {
        List<String> uidList = StringUtils.stringToList(uids, BaseSysConf.FILE_SEGMENTATION);
        for (String uid : uidList) {
            blogSearchMapper.deleteById(uid);
        }
        return ResultBody.success();
    }

    /**
     * ElasticSearch通过博客Uid添加索引
     *
     * @param uid 博客主键
     * @return ResultBody
     * @author yujunhong
     * @date 2021/8/31 13:55
     */
    @ApiOperation(value = "ElasticSearch通过博客Uid添加索引")
    @PostMapping(value = "/addElasticSearchIndexByUid")
    public ResultBody addElasticSearchIndexByUid(@RequestParam String uid) {
        // 获取对应博客信息
        Blog blog = JSON.parseObject(JSON.toJSONString(webFeignClient.getBlogByUid(uid).getResult()), Blog.class);
        if (StringUtils.isNull(blog)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.INSERT_FAIL);
        }
        ElasticSearchVO elasticSearchVO = elasticSearchService.buildElasticSearchVO(blog);
        blogSearchMapper.save(elasticSearchVO);
        return ResultBody.success();
    }

    /**
     * ElasticSearch初始化索引
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/8/31 14:04
     */
    @ApiOperation(value = "ElasticSearch初始化索引")
    @PostMapping(value = "/initElasticSearchIndex")
    public ResultBody initElasticSearchIndex() {
        elasticsearchTemplate.deleteIndex(ElasticSearchVO.class);
        elasticsearchTemplate.createIndex(ElasticSearchVO.class);
        elasticsearchTemplate.putMapping(ElasticSearchVO.class);
        // 页数
        Long page = 1L;
        // 行数
        Long row = 10L;
        // 总数
        int size = 0;
        do {
            // 查询blog信息
            List<Blog> blogList =
                    JSON.parseArray(JSON.toJSONString(webFeignClient.getBlogBySearch(page, row).getResult()),
                            Blog.class);
            size = blogList.size();

            List<ElasticSearchVO> elasticSearchVOList = blogList.stream()
                    .map(elasticSearchService::buildElasticSearchVO).collect(Collectors.toList());

            //存入索引库
            blogSearchMapper.saveAll(elasticSearchVOList);
            // 翻页
            page++;
        } while (size == 15);
        return ResultBody.success();
    }
}
