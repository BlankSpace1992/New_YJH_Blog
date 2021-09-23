package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.BlogVO;
import com.blog.business.web.domain.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     * 通过推荐等级获取博客列表
     *
     * @param page      分页数据
     * @param level     等级
     * @param status    状态
     * @param isPublish 是否发布
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 13:51
     */
    IPage<Blog> getBlogByLevel(IPage<Blog> page, @Param(value = "level") Integer level,
                               @Param(value = "status") Integer status, @Param(value = "isPublish") String isPublish);

    /**
     * 获取首页排行博客
     *
     * @param isPublish 是否发布
     * @param page      分页参数
     * @param status    状态
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 15:01
     */
    IPage<Blog> getHotBlog(IPage<Blog> page,
                           @Param(value = "isPublish") String isPublish,
                           @Param(value = "status") Integer status);

    /**
     * 获取最新博客
     *
     * @param isPublish 是否发布
     * @param page      分页参数
     * @param status    状态
     * @return 获取最新博客
     * @author yujunhong
     * @date 2021/6/1 15:59
     */
    IPage<Blog> getNewBlog(IPage<Blog> page,
                           @Param(value = "isPublish") String isPublish,
                           @Param(value = "status") Integer status);

    /**
     * 按时间戳获取博客
     *
     * @param isPublish 是否发布
     * @param page      分页参数
     * @param status    状态
     * @return 按时间戳获取博客
     * @author yujunhong
     * @date 2021/6/1 16:17
     */
    IPage<Blog> getBlogByTime(IPage<Blog> page,
                              @Param(value = "isPublish") String isPublish,
                              @Param(value = "status") Integer status);

    /**
     * 获取博客信息
     *
     * @param blogVO 查询条件vo
     * @param page   分页参数
     * @return 获取博客信息
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    IPage<Blog> getBlogList(IPage<Blog> page, @Param("blogVO") BlogVO blogVO);

    /**
     * 获取每个标签下文章数目
     *
     * @return 获取每个标签下文章数目
     * @author yujunhong
     * @date 2021/9/22 17:29
     */
    List<Map<String, Object>> getBlogCountByTag();

    /**
     * 获取每个分类下文章数量
     *
     * @return 每个分类下文章数量
     * @author yujunhong
     * @date 2021/9/22 17:29
     */
    List<Map<String, Object>> getBlogCountByBlogSort();

    /**
     * 获取一年内的文章贡献数
     *
     * @param startDate 开始时间
     * @param nowDate   当前时间
     * @return 一年内的文章贡献数
     * @author yujunhong
     * @date 2021/9/22 17:29
     */
    List<Map<String, Object>> getBlogContributeCount(@Param("startDate") Date startDate,
                                                     @Param("nowDate") Date nowDate);
}
