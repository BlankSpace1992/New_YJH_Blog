package com.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.*;
import com.blog.business.web.service.*;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 博客查询
     *
     * @param currentPage 当前页数
     * @param pageSize    每页显示数目
     * @param request     请求
     * @return ResultBody
     * @author yujunhong
     * @date 2021/6/2 15:27
     */
    @GetMapping("/getBlogBySearch")
    public ResultBody getBlogBySearch(HttpServletRequest request,
                                      @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        // TODO: 2021/6/2 暂时未完成
        return ResultBody.success();
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
    public ResultBody addLinkCount(@ApiParam(name = "uid", value = "友情链接Id") @RequestParam(name = "uid") String uid) {
        linkService.addLinkCount(uid);
        return ResultBody.success();
    }

    /**
     * 获取网站配置
     *
     * @return 网站配置信息
     * @author yujunhong
     * @date 2021/6/2 13:56
     */
    @GetMapping(value = "/getWebConfig")
    @ApiOperation(value = "获取网站配置")
    public ResultBody getWebConfig() {
        WebConfig webConfigByShowList = webConfigService.getWebConfigByShowList();
        return ResultBody.success(webConfigByShowList);
    }

    /**
     * 获取网站导航栏
     *
     * @return 获取网站导航栏
     * @author yujunhong
     * @date 2021/6/2 14:26
     */
    @GetMapping(value = "/getWebNavigation")
    @ApiOperation(value = "获取网站导航栏")
    public ResultBody getWebNavigation(@RequestParam(name = "isShow") String isShow) {
        List<WebNavbar> allList = webNavbarService.getAllList();
        return ResultBody.success(allList);
    }

    /**
     * 记录访问记录
     *
     * @return 记录访问记录
     * @author yujunhong
     * @date 2021/6/2 15:22
     */
    @ApiOperation(value = "记录访问记录")
    @GetMapping(value = "/recorderVisitPage")
    public ResultBody recorderVisitPage(@ApiParam(name = "pageName", value = "页面名称") @RequestParam(name = "pageName") String pageName) {
        return ResultBody.success();
    }
}
