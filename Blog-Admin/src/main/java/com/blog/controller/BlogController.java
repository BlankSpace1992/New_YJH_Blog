package com.blog.controller;

import com.blog.business.admin.domain.vo.BlogVO;
import com.blog.business.web.service.BlogService;
import com.blog.constants.BaseMessageConf;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/9/17 11:33
 */
@RestController
@RequestMapping(value = "/blog")
@Api(value = "博客相关接口", tags = {"博客相关接口"})
public class BlogController {
    @Autowired
    private BlogService blogService;

    /**
     * 获取博客信息
     *
     * @param blogVO 查询条件vo
     * @return 获取博客信息
     * @author yujunhong
     * @date 2021/9/17 11:34
     */
    @ApiOperation(value = "获取博客信息")
    @PostMapping("/getList")
    public ResultBody getBlogList(@RequestBody BlogVO blogVO) {
        return blogService.getBlogList(blogVO);
    }

    /**
     * 新增博客
     *
     * @param blogVO 博客新增实体对象
     * @return 新增博客
     * @author yujunhong
     * @date 2021/9/17 14:10
     */
    @ApiOperation(value = "新增博客")
    @PostMapping(value = "/add")
    public ResultBody add(@RequestBody BlogVO blogVO) {
        return blogService.add(blogVO);
    }

    /**
     * 本地博客上传
     *
     * @param filedatas 本地上传文件
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/17 14:22
     */
    @ApiOperation(value = "本地博客上传")
    @PostMapping("/uploadLocalBlog")
    public ResultBody uploadLocalBlog(@RequestBody List<MultipartFile> filedatas) {
        return blogService.uploadLocalBlog(filedatas);
    }

    /**
     * 编辑博客
     *
     * @param blogVO 编辑博客实体对象
     * @return 编辑博客
     * @author yujunhong
     * @date 2021/9/17 15:18
     */
    @ApiOperation(value = "编辑博客")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody BlogVO blogVO) {
        return blogService.edit(blogVO);
    }

    /**
     * 推荐博客排序调整
     *
     * @param blogVOList 博客修改集合实体
     * @return 推荐博客排序调整
     * @author yujunhong
     * @date 2021/9/17 15:26
     */
    @ApiOperation(value = "推荐博客排序调整")
    @PostMapping("/editBatch")
    public ResultBody editBatch(@RequestBody List<BlogVO> blogVOList) {
        if (blogVOList.size() <= 0) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return blogService.editBatch(blogVOList);
    }

    /**
     * 删除博客信息
     *
     * @param blogVO 删除博客实体对象
     * @return 删除博客信息
     * @author yujunhong
     * @date 2021/9/17 15:31
     */
    @ApiOperation(value = "删除博客")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody BlogVO blogVO) {
        return blogService.delete(blogVO);
    }

    /**
     * 删除选中博客
     *
     * @param blogVoList 博客集合
     * @return 删除选中博客
     * @author yujunhong
     * @date 2021/9/17 15:37
     */
    @ApiOperation(value = "删除选中博客")
    @PostMapping("/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<BlogVO> blogVoList) {
        if (blogVoList.size() <= 0) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return blogService.deleteBatch(blogVoList);
    }
}
