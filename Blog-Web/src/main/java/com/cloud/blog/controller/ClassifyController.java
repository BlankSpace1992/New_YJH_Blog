package com.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.BlogSort;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.BlogSortService;
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

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/9/2 16:35
 */
@RestController
@RequestMapping(value = "/classify")
@Api(value = "分类相关接口", tags = "分类相关接口")
public class ClassifyController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogSortService blogSortService;


    /**
     * 获取分类信息
     *
     * @return 分类信息
     * @author yujunhong
     * @date 2021/9/2 16:37
     */
    @ApiOperation(value = "获取分类信息")
    @GetMapping(value = "/getBlogSortList")
    public ResultBody getBlogSortList() {
        List<BlogSort> blogListByClassify = blogSortService.getBlogListByClassify();
        return ResultBody.success(blogListByClassify);
    }

    /**
     * 通过分类uid获取文章
     *
     * @param blogSortUid 分类UID
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 文章信息
     * @author yujunhong
     * @date 2021/9/2 16:44
     */
    @GetMapping(value = "/getArticleByBlogSortUid")
    @ApiOperation(value = "通过分类uid获取文章")
    public ResultBody getArticleByBlogSortUid(@ApiParam(name = "blogSortUid", value = "分类UID", required = false) @RequestParam(name = "blogSortUid", required = false) String blogSortUid,
                                              @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                              @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(blogSortUid)) {
            return ResultBody.error(BaseSysConf.ERROR, "传入BlogSortUid不能为空");
        }
        IPage<Blog> listByBlogSortUid = blogService.searchBlogBySort(blogSortUid, currentPage, pageSize);
        return ResultBody.success(listByBlogSortUid);
    }
}
