package com.cloud.blog.controller;

import com.blog.business.web.domain.Blog;
import com.blog.business.web.service.BlogService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * @author yujunhong
 * @date 2021/9/1 16:56
 */
@RestController
@RequestMapping(value = "/sort")
@Api(value = "博客归档相关接口", tags = "博客归档相关接口")
public class SortController {
    @Autowired
    private BlogService blogService;

    /**
     * 归档数据查询
     *
     * @return 归档数据
     * @author yujunhong
     * @date 2021/9/1 17:00
     */
    @ApiOperation(value = "归档数据查询")
    @GetMapping(value = "/getSortList")
    public ResultBody getSortList() {
        Set<String> blogTimeSortList = blogService.getBlogTimeSortList();
        return ResultBody.success(blogTimeSortList);
    }


    /**
     * 通过月份获取文章
     *
     * @param monthDate 归档月份
     * @return 月份获取文章
     * @author yujunhong
     * @date 2021/9/1 17:15
     */
    @ApiOperation(value = "通过月份获取文章")
    @GetMapping(value = "/getArticleByMonth")
    public ResultBody getArticleByMonth(@ApiParam(name = "monthDate", value = "归档的日期", required = false) @RequestParam(name = "monthDate", required = false) String monthDate) {
        List<Blog> articleByMonth = blogService.getArticleByMonth(monthDate);
        return ResultBody.success(articleByMonth);
    }
}
