package com.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.domain.Blog;
import com.blog.business.domain.Link;
import com.blog.business.domain.Tag;
import com.blog.business.service.*;
import com.blog.config.redis.RedisUtil;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页相关接口 controller
 *
 * @author yujunhong
 * @date 2021/6/1 10:47
 */
@RestController
@RequestMapping(value = "/index")
@Api(value = "001 - 首页相关接口", tags = "001 - 首页相关接口")
public class IndexController {
    @Autowired
    private TagService tagService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private WebNavbarService webNavbarService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 通过博客登记获取博客列表
     *
     * @param level       推荐等级
     * @param currentPage 当前页数
     * @param useSort     使用排序
     * @return ResultBody 公共返回实体
     * @author yujunhong
     * @date 2021/6/1 13:38
     */
    @GetMapping(value = "/getBlogByLevel")
    @ApiOperation(value = "通过博客登记获取博客列表")
    public ResultBody getBlogByLevel(@ApiParam(name = "level", value = "推荐等级") @RequestParam(name = "level", required =
            false, defaultValue = "0") Integer level,
                                     @ApiParam(name = "currentPage", value = "当前页数") @RequestParam(name = "currentPage",
                                             required = false, defaultValue = "1") Integer currentPage,
                                     @ApiParam(name = "useSort", value = "使用排序") @RequestParam(name = "useSort",
                                             required = false, defaultValue = "0") Integer useSort) {
        IPage<Blog> blogByLevel = blogService.getBlogByLevel(level, currentPage, useSort);
        return ResultBody.success(blogByLevel);
    }

    /**
     * 获取首页排行博客
     *
     * @return 获取首页排行博客
     * @author yujunhong
     * @date 2021/6/1 15:00
     */
    @ApiOperation(value = "获取首页排行博客")
    @GetMapping(value = "/getHotBlog")
    public ResultBody getHotBlog() {
        IPage<Blog> hotBlog = blogService.getHotBlog();
        return ResultBody.success(hotBlog);
    }

    /**
     * 获取首页最新博客
     *
     * @param currentPage 当前页数
     * @return 获取首页最新博客
     * @author yujunhong
     * @date 2021/6/1 15:55
     */
    @ApiOperation(value = "获取首页最新博客")
    @GetMapping(value = "/getNewBlog")
    public ResultBody getNewBlog(@ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer currentPage
    ) {
        IPage<Blog> newBlog = blogService.getNewBlog(currentPage);
        return ResultBody.success(newBlog);
    }

    /**
     * 按时间戳获取博客
     *
     * @param currentPage 当前页数
     * @return 按时间戳获取博客
     * @author yujunhong
     * @date 2021/6/1 16:15
     */
    @ApiOperation(value = "按时间戳获取博客")
    @GetMapping(value = "/getBlogByTime")
    public ResultBody getBlogByTime(@ApiParam(name = "currentPage", value = "当前页数") @RequestParam(name = "currentPage"
            , required = false, defaultValue = "1") Integer currentPage) {
        IPage<Blog> blogByTime = blogService.getBlogByTime(currentPage);
        return ResultBody.success(blogByTime);
    }

    /**
     * 获取最热标签
     *
     * @return 获取最热标签
     * @author yujunhong
     * @date 2021/6/1 16:24
     */
    @ApiOperation(value = "获取最热标签")
    @GetMapping(value = "/getHotTag")
    public ResultBody getHotTag() {
        IPage<Tag> hotTag = tagService.getHotTag();
        return ResultBody.success(hotTag);
    }

    /**
     * 获取友情连接
     *
     * @return 获取友情连接
     * @author yujunhong
     * @date 2021/6/1 16:36
     */
    @ApiOperation(value = "获取友情连接")
    @GetMapping(value = "/getLink")
    public ResultBody getLink() {
        IPage<Link> link = linkService.getLink();
        return ResultBody.success(link);
    }

    /**
     * 增加友情连接点击数
     *
     * @param uid 友情链接Id
     * @return ResultBody
     * @author yujunhong
     * @date 2021/6/1 17:01
     */
    @ApiOperation(value = "增加友情连接点击数")
    @GetMapping(value = "/addLinkCount")
    public ResultBody addLinkCount(@ApiParam(name = "uid", value = "友情链接Id") @RequestParam(name = "uid", required =
            true) String uid) {
        linkService.addLinkCount(uid);
        return ResultBody.success();
    }
}
