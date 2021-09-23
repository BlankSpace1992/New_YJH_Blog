package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.TagVO;
import com.blog.business.web.domain.Tag;
import com.blog.business.web.service.TagService;
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
 * @date 2021/9/23 15:51
 */
@RestController
@RequestMapping(value = "/tag")
@Api(value = "博客标签相关接口", tags = {"博客标签相关接口"})
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取标签列表
     *
     * @param tagVO 查询条件
     * @return 标签列表
     * @author yujunhong
     * @date 2021/9/23 15:53
     */
    @ApiOperation(value = "获取标签列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody TagVO tagVO) {
        IPage<Tag> pageList =
                tagService.getPageList(tagVO);
        return ResultBody.success(pageList);
    }


    /**
     * 增加标签
     *
     * @param tagVO 新增实体对象
     * @return 增加标签
     * @author yujunhong
     * @date 2021/9/23 16:00
     */
    @ApiOperation(value = "增加标签")
    @PostMapping(value = "/add")
    public ResultBody add(@RequestBody TagVO tagVO) {
        return tagService.add(tagVO);
    }

    /**
     * 编辑标签
     *
     * @param tagVO 编辑标签实体对象
     * @return 编辑标签
     * @author yujunhong
     * @date 2021/9/23 16:00
     */
    @ApiOperation(value = "编辑标签")
    @PostMapping(value = "/edit")
    public ResultBody edit(@RequestBody TagVO tagVO) {
        return tagService.edit(tagVO);
    }

    /**
     * 批量删除标签
     *
     * @param tagVoList 删除集合
     * @return 批量删除标签
     * @author yujunhong
     * @date 2021/9/23 16:17
     */
    @ApiOperation(value = "批量删除标签")
    @PostMapping(value = "/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<TagVO> tagVoList) {
        return tagService.deleteBatch(tagVoList);
    }

    /**
     * 置顶标签
     *
     * @param tagVO 置顶实体对象
     * @return 置顶标签
     * @author yujunhong
     * @date 2021/9/23 17:00
     */
    @ApiOperation(value = "置顶标签")
    @PostMapping("/stick")
    public ResultBody stick(@RequestBody TagVO tagVO) {
        return tagService.stick(tagVO);
    }

    /**
     * 通过点击量排序标签
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 17:07
     */
    @ApiOperation(value = "通过点击量排序标签")
    @PostMapping("/tagSortByClickCount")
    public ResultBody tagSortByClickCount() {
        return tagService.tagSortByClickCount();
    }

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 17:08
     */
    @ApiOperation(value = "通过引用量排序标签")
    @PostMapping("/tagSortByCite")
    public ResultBody tagSortByCite() {
        return tagService.tagSortByCite();
    }
}
