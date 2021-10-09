package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.CommentVO;
import com.blog.business.web.domain.Comment;
import com.blog.business.web.service.CommentService;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
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
 * @date 2021/10/8 14:02
 */
@RestController
@RequestMapping(value = "/comment")
@Api(value = "用户评论相关接口", tags = {"用户评论相关接口"})
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表
     *
     * @param commentVO 查询条件
     * @return 获取评论列表
     * @author yujunhong
     * @date 2021/10/8 14:11
     */
    @ApiOperation(value = "获取评论列表")
    @PostMapping(value = "/getList")
    public ResultBody getList(@RequestBody CommentVO commentVO) {
        IPage<Comment> pageList = commentService.getPageList(commentVO);
        return ResultBody.success(pageList);
    }

    /**
     * 增加评论
     *
     * @param commentVO 增加评论实体
     * @return 增加评论
     * @author yujunhong
     * @date 2021/10/8 14:29
     */
    @ApiOperation(value = "增加评论")
    @PostMapping("/add")
    public ResultBody add(@RequestBody CommentVO commentVO) {
        return commentService.addComment(commentVO);
    }

    /**
     * 编辑评论
     *
     * @param commentVO 编辑评论实体
     * @return 编辑评论
     * @author yujunhong
     * @date 2021/10/8 14:34
     */
    @ApiOperation(value = "编辑评论")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody CommentVO commentVO) {
        return commentService.editComment(commentVO);
    }

    /**
     * 删除评论
     *
     * @param commentVO 删除评论实体
     * @return 编辑评论
     * @author yujunhong
     * @date 2021/10/8 14:34
     */
    @ApiOperation(value = "删除评论")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody CommentVO commentVO) {
        return commentService.deleteComment(commentVO);
    }

    /**
     * 删除选中评论
     *
     * @param commentVOList 删除评论实体
     * @return 删除选中评论
     * @author yujunhong
     * @date 2021/10/8 14:34
     */
    @ApiOperation(value = "删除选中评论")
    @PostMapping("/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<CommentVO> commentVOList) {
        if (StringUtils.isEmpty(commentVOList)) {
            return ResultBody.error("请选择需要删除的评论");
        }
        return commentService.deleteBatchComment(commentVOList);
    }
}
