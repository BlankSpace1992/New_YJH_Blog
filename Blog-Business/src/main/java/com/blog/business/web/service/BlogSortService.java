package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.BlogSortVO;
import com.blog.business.web.domain.BlogSort;
import com.blog.exception.ResultBody;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
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


    /**
     * 获取博客分类列表
     *
     * @param blogSortVO 查询条件
     * @return 博客分类列表
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    IPage<BlogSort> getList(BlogSortVO blogSortVO);

    /**
     * 增加博客分类
     *
     * @param blogSortVO 新增实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    ResultBody add(BlogSortVO blogSortVO);

    /**
     * 编辑博客分类
     *
     * @param blogSortVO 编辑实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    ResultBody edit(BlogSortVO blogSortVO);

    /**
     * 批量删除博客分类
     *
     * @param blogSortVoList 批量删除实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    ResultBody deleteBatch(List<BlogSortVO> blogSortVoList);

    /**
     * 置顶分类
     *
     * @param blogSortVO 置顶分类实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    ResultBody stick(BlogSortVO blogSortVO);

    /**
     * 通过点击量排序博客分类
     *
     * @return 点击量排序博客分类
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    ResultBody blogSortByClickCount();

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:07
     */
    ResultBody blogSortByCite();
}
