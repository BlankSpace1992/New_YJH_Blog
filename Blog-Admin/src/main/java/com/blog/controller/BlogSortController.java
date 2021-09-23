package com.blog.controller;

import com.blog.business.admin.domain.vo.BlogSortVO;
import com.blog.business.web.service.BlogSortService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/9/23 11:02
 */
@RestController
@RequestMapping(value = "/blogSort")
@Api(value = "博客分类相关接口", tags = "博客分类相关接口")
public class BlogSortController {
    @Autowired
    private BlogSortService blogSortService;

    /**
     * 获取博客分类列表
     *
     * @param blogSortVO 查询条件
     * @return 博客分类列表
     * @author yujunhong
     * @date 2021/9/23 11:03
     */
    @ApiOperation(value = "获取博客分类列表")
    @PostMapping(value = "/getList")
    public ResultBody getList(@RequestBody BlogSortVO blogSortVO) {
        return ResultBody.success(blogSortService.getList(blogSortVO));
    }

    /**
     * 增加博客分类
     *
     * @param blogSortVO 新增实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:17
     */
    @ApiOperation(value = "增加博客分类")
    @PostMapping(value = "/add")
    public ResultBody add(@RequestBody BlogSortVO blogSortVO) {
        return blogSortService.add(blogSortVO);
    }

    /**
     * 编辑博客分类
     *
     * @param blogSortVO 编辑实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:22
     */
    @ApiOperation(value = "编辑博客分类")
    @PostMapping(value = "/edit")
    public ResultBody edit(@RequestBody BlogSortVO blogSortVO) {
        return blogSortService.edit(blogSortVO);
    }


    /**
     * 批量删除博客分类
     *
     * @param blogSortVoList 批量删除实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:30
     */
    @ApiOperation(value = "批量删除博客分类")
    @PostMapping(value = "/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<BlogSortVO> blogSortVoList) {
        return blogSortService.deleteBatch(blogSortVoList);
    }

    /**
     * 置顶分类
     *
     * @param blogSortVO 置顶分类实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 11:46
     */
    @ApiOperation(value = "置顶分类")
    @PostMapping(value = "/stick")
    public ResultBody stick(@RequestBody BlogSortVO blogSortVO) {
        return blogSortService.stick(blogSortVO);
    }


    /**
     * 通过点击量排序博客分类
     *
     * @return 点击量排序博客分类
     * @author yujunhong
     * @date 2021/9/23 14:11
     */
    @ApiOperation(value = "通过点击量排序博客分类")
    @PostMapping(value = "/blogSortByClickCount")
    public ResultBody blogSortByClickCount() {
        return blogSortService.blogSortByClickCount();
    }

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 14:19
     */
    @ApiOperation(value = "通过引用量排序博客分类")
    @PostMapping(value = "/blogSortByCite")
    public ResultBody blogSortByCite() {
        return blogSortService.blogSortByCite();
    }
}
