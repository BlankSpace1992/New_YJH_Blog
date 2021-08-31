package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.Blog;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     * 通过推荐等级获取博客列表
     *
     * @param page   分页数据
     * @param level  等级
     * @param status 状态
     * @param isPublish  是否发布
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 13:51
     */
    IPage<Blog> getBlogByLevel(IPage<Blog> page, @Param(value = "level") Integer level,
                               @Param(value = "status") Integer status, @Param(value = "isPublish")String isPublish);

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
}
