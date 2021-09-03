package com.cloud.blog.controller;

import com.blog.business.web.domain.Tag;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.TagService;
import com.blog.constants.BaseSysConf;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/9/2 17:02
 */
@RestController
@RequestMapping(value = "/tag")
@Api(value = "博客标签相关接口", tags = "博客标签相关接口")
public class TagController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private TagService tagService;

    /**
     * 获取标签的信息
     *
     * @return 标签的信息
     * @author yujunhong
     * @date 2021/9/2 17:05
     */
    @ApiOperation(value = "获取标签的信息", notes = "获取标签的信息")
    @GetMapping("/getTagList")
    public ResultBody getTagList() {
        List<Tag> list =
                tagService.getList();
        return ResultBody.success(list);
    }

    /**
     * 通过TagUid获取文章
     *
     * @param tagUid      标签UID
     * @param currentPage 当前页数
     * @param pageSize    每页显示数目
     * @return 文章
     * @author yujunhong
     * @date 2021/9/2 17:10
     */
    @ApiOperation(value = "通过TagUid获取文章", notes = "通过TagUid获取文章")
    @GetMapping("/getArticleByTagUid")
    public ResultBody getArticleByTagUid(@ApiParam(name = "tagUid", value = "标签UID", required = false) @RequestParam(name = "tagUid", required = false) String tagUid,
                                         @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                         @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultBody.error(BaseSysConf.ERROR, "传入TagUid不能为空");
        }
        return ResultBody.success(blogService.searchBlogByTag(tagUid, currentPage, pageSize));
    }

}
