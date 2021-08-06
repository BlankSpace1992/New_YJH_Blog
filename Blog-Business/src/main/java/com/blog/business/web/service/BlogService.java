package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.Blog;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface BlogService extends IService<Blog> {

    /**
     * 通过推荐等级获取博客列表
     *
     * @param level       推荐等级
     * @param currentPage 当前页数
     * @param useSort     使用排序
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 13:51
     */
    IPage<Blog> getBlogByLevel(Integer level, Integer currentPage, Integer useSort);

    /**
     * 通过推荐等级获取博客列表
     *
     * @param page  分页数据
     * @param level 等级
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 13:51
     */
    IPage<Blog> getBlogByLevel(IPage<Blog> page, Integer level);

    /**
     * 获取首页排行博客
     *
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 15:01
     */
    IPage<Blog> getHotBlog();

    /**
     * 获取最新博客
     *
     * @param currentPage 当前页数
     * @return 获取最新博客
     * @author yujunhong
     * @date 2021/6/1 15:59
     */
    IPage<Blog> getNewBlog(Integer currentPage);

    /**
     * 按时间戳获取博客
     *
     * @param currentPage 当前页数
     * @return 按时间戳获取博客
     * @author yujunhong
     * @date 2021/6/1 16:17
     */
    IPage<Blog> getBlogByTime(Integer currentPage);

    /**
     * 给博客列表设置分类,标签,图片
     *
     * @param
     * @return
     * @author yujunhong
     * @date 2021/8/2 15:03
     */
    List<Blog> setTagAndSortAndPictureByBlogList(List<Blog> list);
}
