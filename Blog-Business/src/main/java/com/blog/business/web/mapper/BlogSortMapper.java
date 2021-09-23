package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.BlogSortVO;
import com.blog.business.web.domain.BlogSort;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface BlogSortMapper extends BaseMapper<BlogSort> {

    /**
     * 获取博客分类列表
     *
     * @param blogSortVO 查询条件
     * @param page       分页参数
     * @return 博客分类列表
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    IPage<BlogSort> getList(@Param("page") IPage<BlogSort> page, @Param("blogSortVO") BlogSortVO blogSortVO);
}
