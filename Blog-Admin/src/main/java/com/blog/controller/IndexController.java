package com.blog.controller;

import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.CommentService;
import com.blog.business.web.service.UserService;
import com.blog.business.web.service.WebVisitService;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.constants.EnumsStatus;
import com.blog.exception.ResultBody;
import com.blog.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/9/22 16:31
 */
@RestController
@RequestMapping(value = "/index")
@Api(value = "首页相关接口", tags = "首页相关接口")
public class IndexController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private WebVisitService webVisitService;

    /**
     * 首页初始化数据
     *
     * @return 首页初始化数据
     * @author yujunhong
     * @date 2021/9/22 16:32
     */
    @ApiOperation(value = "首页初始化数据")
    @GetMapping(value = "/init")
    public ResultBody init() {
        // 获取博客数量
        Integer blogCount = blogService.getBlogCount(EnumsStatus.ENABLE);
        // 获取评论数量
        Integer commentCount = commentService.getCommentCount(EnumsStatus.ENABLE);
        // 获取用户数量
        Integer userCount = userService.getUserCount(EnumsStatus.ENABLE);
        // 获取今日时间
        Date nowDate = DateUtils.getNowDate();
        // 获取今日开始时间
        Date startDate = DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", nowDate) + " 00:00:00");
        // 获取今日访问数量
        Integer webVisitCount = webVisitService.getWebVisitCount(startDate, nowDate);
        // 存放数据
        Map<String, Object> map = new HashMap<>(Constants.NUM_FOUR);
        map.put(BaseSysConf.BLOG_COUNT, blogCount);
        map.put(BaseSysConf.COMMENT_COUNT, commentCount);
        map.put(BaseSysConf.USER_COUNT, userCount);
        map.put(BaseSysConf.VISIT_COUNT, webVisitCount);
        return ResultBody.success(map);
    }

    /**
     * 获取最近一周用户独立IP数和访问量
     *
     * @return 最近一周用户独立IP数和访问量
     * @author yujunhong
     * @date 2021/9/22 16:50
     */
    @ApiOperation(value = "获取最近一周用户独立IP数和访问量")
    @GetMapping(value = "/getVisitByWeek")
    public ResultBody getVisitByWeek() {
        return ResultBody.success(webVisitService.getVisitByWeek());
    }

    /**
     * 获取每个标签下文章数目
     *
     * @return 每个标签下文章数目
     * @author yujunhong
     * @date 2021/9/22 17:28
     */
    @ApiOperation(value = "获取每个标签下文章数目")
    @GetMapping(value = "/getBlogCountByTag")
    public ResultBody getBlogCountByTag() {
        return ResultBody.success(blogService.getBlogCountByTag());
    }

    /**
     * 获取每个分类下文章数量
     *
     * @return 每个分类下文章数量
     * @author yujunhong
     * @date 2021/9/23 10:10
     */
    @ApiOperation(value = "获取每个分类下文章数量")
    @GetMapping(value = "/getBlogCountByBlogSort")
    public ResultBody getBlogCountByBlogSort() {
        return ResultBody.success(blogService.getBlogCountByBlogSort());
    }

    /**
     * 获取一年内的文章贡献数
     *
     * @return 一年内的文章贡献数
     * @author yujunhong
     * @date 2021/9/23 10:16
     */
    @ApiOperation(value = "获取一年内的文章贡献数")
    @GetMapping(value = "/getBlogContributeCount")
    public ResultBody getBlogContributeCount() {
        return ResultBody.success(blogService.getBlogContributeCount());
    }
}
