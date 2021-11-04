package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.CommentVO;
import com.blog.business.web.domain.Comment;
import com.blog.business.web.domain.vo.CommentParamVO;
import com.blog.business.web.domain.vo.UserVO;
import com.blog.exception.ResultBody;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface CommentService extends IService<Comment> {
    /**
     * 获取评论列表
     *
     * @param commentVO 查询条件
     * @return 获取评论列表
     * @author yujunhong
     * @date 2021/10/8 14:15
     */
    IPage<Comment> getPageList(CommentVO commentVO);

    /**
     * 获取用户评论列表
     *
     * @param commentParamVO 查询条件vo
     * @return 用户评论信息
     * @author yujunhong
     * @date 2021/8/12 15:02
     */
    IPage<Comment> getCommentList(CommentParamVO commentParamVO);

    /**
     * 获取用户的评论列表以及回复
     *
     * @param userVO  用户信息实体对象
     * @param userUid 用户id
     * @return 用户的评论列表以及回复
     * @author yujunhong
     * @date 2021/8/25 14:44
     */
    Map<String, Object> getListByUser(UserVO userVO, String userUid);

    /**
     * 获取用户点赞信息
     *
     * @param currentPage 当前页数
     * @param pageSize    每页显示数目
     * @param userUid     用户id
     * @return 用户点赞信息
     * @author yujunhong
     * @date 2021/8/25 15:25
     */
    List<Comment> getPraiseListByUser(Long currentPage, Long pageSize, String userUid);

    /**
     * 新增评论
     *
     * @param commentVO 新增评论的实体对象
     * @param userUid   用户id
     * @author yujunhong
     * @date 2021/8/25 16:06
     */
    ResultBody add(CommentParamVO commentVO, String userUid);

    /**
     * 举报评论
     *
     * @param commentVO 举报评论的实体对象
     * @author yujunhong
     * @date 2021/8/26 14:07
     */
    void report(CommentParamVO commentVO);

    /**
     * 删除评论
     *
     * @param commentVO 删除评论的实体对象
     * @author yujunhong
     * @date 2021/8/26 14:07
     */
    void delete(CommentParamVO commentVO);


    /**
     * 获取评论数量
     *
     * @param enableFlag 可用标志
     * @return 评论数量
     * @author yujunhong
     * @date 2021/9/22 16:36
     */
    Integer getCommentCount(int enableFlag);

    /**
     * 获取评论列表
     *
     * @param commentVO 查询条件
     * @return 获取评论列表
     * @author yujunhong
     * @date 2021/10/8 14:32
     */
    ResultBody addComment(CommentVO commentVO);

    /**
     * 编辑评论
     *
     * @param commentVO 编辑评论实体
     * @return 编辑评论
     * @author yujunhong
     * @date 2021/10/8 14:32
     */
    ResultBody editComment(CommentVO commentVO);

    /**
     * 删除评论
     *
     * @param commentVO 删除评论实体
     * @return 编辑评论
     * @author yujunhong
     * @date 2021/10/8 14:32
     */
    ResultBody deleteComment(CommentVO commentVO);

    /**
     * 删除选中评论
     *
     * @param commentVOList 删除评论实体
     * @return 删除选中评论
     * @author yujunhong
     * @date 2021/10/8 14:32
     */
    ResultBody deleteBatchComment(List<CommentVO> commentVOList);

    /**移除该文章下所有评论
     * @param blogUid 博客uid集合
     * @author yujunhong
     * @date 2021/11/4 15:19
     */
    void batchDeleteCommentByBlogUid(List<String> blogUid);
}
