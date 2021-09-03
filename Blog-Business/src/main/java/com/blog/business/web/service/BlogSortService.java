package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.BlogSort;

import java.util.List;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
public interface BlogSortService extends IService<BlogSort> {

    /**
     * 获取博客分类列表
     *
     * @return 博客分类列表
     * @author yujunhong
     * @date 2021/9/2 16:39
     */
    List<BlogSort> getBlogListByClassify();
}
