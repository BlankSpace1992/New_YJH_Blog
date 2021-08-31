package com.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.service.BlogService;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/8/31 14:25
 */
@RestController
@RequestMapping(value = "/search")
@Api(value = "搜索相关接口", tags = "搜索相关接口")
public class SearchController {
    @Autowired
    private BlogService blogService;

    /**
     * 搜索博客，如需ElasticSearch 需要启动 blog-search
     *
     * @param keywords    关键字
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:26
     */
    @ApiOperation(value = "搜索博客，如需ElasticSearch 需要启动 blog-search")
    @GetMapping(value = "/searchBlog")
    public ResultBody searchBlog(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam(required
            = true) String keywords,
                                 @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                 @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name
                                         = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(keywords)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        Map<String, Object> map = blogService.searchBlog(keywords, currentPage, pageSize);
        return ResultBody.success(map);
    }

    /**
     * 根据标签获取相关的博客
     *
     * @param tagUid      标签id
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @author yujunhong
     * @date 2021/8/31 15:28
     */
    @ApiOperation(value = "根据标签获取相关的博客")
    @GetMapping(value = "/searchBlogByTag")
    public ResultBody searchBlogByTag(@ApiParam(name = "tagUid", value = "博客标签UID", required = true) @RequestParam(name = "tagUid", required = true) String tagUid,
                                      @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.TAG_IS_EMPTY);
        }
        IPage<Blog> blogPage = blogService.searchBlogByTag(tagUid, currentPage, pageSize);
        return ResultBody.success(blogPage);
    }

    /**
     * 根据标签获取相关的博客
     *
     * @param blogSortUid 博客分类UID
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @author yujunhong
     * @date 2021/8/31 15:28
     */
    @ApiOperation(value = "根据标签获取相关的博客")
    @GetMapping(value = "/searchBlogBySort")
    public ResultBody searchBlogBySort(@ApiParam(name = "blogSortUid", value = "博客分类UID", required = true) @RequestParam(name = "blogSortUid", required = true) String blogSortUid,
                                       @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                       @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(blogSortUid)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.SORT_IS_EMPTY);
        }
        IPage<Blog> blogPage = blogService.searchBlogBySort(blogSortUid, currentPage, pageSize);
        return ResultBody.success(blogPage);
    }

    /**
     * 根据标签获取相关的博客
     *
     * @param author 作者名称
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @author yujunhong
     * @date 2021/8/31 15:28
     */
    @ApiOperation(value = "根据标签获取相关的博客")
    @GetMapping(value = "/searchBlogByAuthor")
    public ResultBody searchBlogByAuthor(@ApiParam(name = "author", value = "作者名称", required = true) @RequestParam(name = "author", required = true) String author,
                                       @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                       @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(author)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.AUTHOR_IS_EMPTY);
        }
        IPage<Blog> blogPage = blogService.searchBlogByAuthor(author, currentPage, pageSize);
        return ResultBody.success(blogPage);
    }
}
