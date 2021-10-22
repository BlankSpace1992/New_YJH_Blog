package com.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.utils.WebUtils;
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

/**
 * @author yujunhong
 * @date 2021/8/9 14:14
 */
@RestController
@RequestMapping(value = "/content")
@Api(tags = "博客相关详情接口", value = "博客相关详情接口")
public class BlogContentController {
    @Autowired
    private WebUtils webUtils;
    @Autowired
    private BlogService blogService;

    /**
     * 通过点击博客获取详情信息
     *
     * @param uid 博客UID
     * @param oid 博客OID
     * @return 博客内容
     * @author yujunhong
     * @date 2021/8/9 14:40
     */
    @ApiOperation(value = "通过点击博客获取详情信息")
    @GetMapping(value = "/getBlogContentByUid")
    public ResultBody getBlogContentByUid(@ApiParam(name = "uid", value = "博客UID")
                                          @RequestParam(name = "uid", required = false) String uid,
                                          @ApiParam(name = "oid", value = "博客OID")
                                          @RequestParam(name = "oid", required = false, defaultValue = "0") Integer oid) {
        Blog blog = blogService.getBlogContentByUid(uid, oid);
        return ResultBody.success(blog);
    }

    /**
     * 根据blogId获取相关博客
     *
     * @param blogUid 博客id
     * @return 相关博客信息
     * @author yujunhong
     * @date 2021/8/12 14:14
     */
    @ApiOperation(value = "根据blogId获取相关博客")
    @GetMapping(value = "/getSameBlogByBlogUid")
    public ResultBody getSameBlogByBlogUid(@ApiParam(name = "blogUid", value = "博客标签UID", required = true) @RequestParam(name = "blogUid") String blogUid) {
        if (StringUtils.isEmpty(blogUid)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.PARAM_INCORRECT);
        }
        IPage<Blog> sameBlogByBlogUid = blogService.getSameBlogByBlogUid(blogUid);
        return ResultBody.success(sameBlogByBlogUid);
    }


    /**
     * 通过Uid给博客点赞
     *
     * @param uid 博客UID
     * @return 通过Uid给博客点赞
     * @author yujunhong
     * @date 2021/10/22 16:00
     */
    @ApiOperation(value = "通过Uid给博客点赞")
    @GetMapping("/praiseBlogByUid")
    public ResultBody praiseBlogByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name =
            "uid", required = false) String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return blogService.praiseBlogByUid(uid);
    }

    /**
     * 通过Uid获取博客点赞数
     *
     * @param uid 博客UID
     * @return 通过Uid获取博客点赞数
     * @author yujunhong
     * @date 2021/10/22 16:22
     */
    @ApiOperation(value = "通过Uid获取博客点赞数")
    @GetMapping("/getBlogPraiseCountByUid")
    public ResultBody getBlogPraiseCountByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        return ResultBody.success(blogService.getBlogPraiseCountByUid(uid));
    }
}
