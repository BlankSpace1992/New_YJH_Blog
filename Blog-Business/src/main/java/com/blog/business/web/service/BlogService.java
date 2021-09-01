package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.Blog;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @param list 博客列表
     * @return 包含图片/标签/分类的博客列表
     * @author yujunhong
     * @date 2021/8/2 15:03
     */
    void setTagAndSortAndPictureByBlogList(List<Blog> list);

    /**
     * 通过点击博客获取详情信息
     *
     * @param uid 博客UID
     * @param oid 博客OID
     * @return 博客内容
     * @author yujunhong
     * @date 2021/8/9 14:46
     */
    Blog getBlogContentByUid(String uid, Integer oid);

    /**
     * 根据blogId获取相关博客
     *
     * @param blogUid 博客id
     * @return 相关博客信息
     * @author yujunhong
     * @date 2021/8/12 14:19
     */
    IPage<Blog> getSameBlogByBlogUid(String blogUid);

    /**
     * 博客查询
     *
     * @param currentPage 当前页数
     * @param pageSize    每页显示数目
     * @return 博客查询数据
     * @author yujunhong
     * @date 2021/8/31 14:08
     */
    List<Blog> getBlogBySearch(Long currentPage, Long pageSize);

    /**
     * 搜索博客，如需ElasticSearch 需要启动 blog-search
     *
     * @param keywords    关键字
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    Map<String, Object> searchBlog(String keywords, Long currentPage, Long pageSize);

    /**
     * 根据标签获取相关的博客
     *
     * @param tagUid      标签id
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize);

    /**
     * 根据标签获取相关的博客
     *
     * @param blogSortUid 博客分类UID
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    IPage<Blog> searchBlogBySort(String blogSortUid, Long currentPage, Long pageSize);

    /**
     * 根据标签获取相关的博客
     *
     * @param author      作者名称
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    IPage<Blog> searchBlogByAuthor(String author, Long currentPage, Long pageSize);

    /**
     * 获取博客的归档日期
     *
     * @return 获取博客的归档日期
     * @author yujunhong
     * @date 2021/9/1 17:01
     */
    Set<String> getBlogTimeSortList();

    /**
     * 通过月份获取日期
     *
     * @param monthDate 月份
     * @return 当月文章
     * @author yujunhong
     * @date 2021/9/1 17:16
     */
    List<Blog> getArticleByMonth(String monthDate);
}
