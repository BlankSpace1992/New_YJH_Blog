package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.Comment;
import com.blog.business.web.domain.vo.CommentParamVO;
import com.blog.business.web.domain.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface CommentService extends IService<Comment> {

    /**
     * 获取用户评论列表
     *
     * @param commentParamVO 查询条件vo
     * @return 用户评论信息
     * @author yujunhong
     * @date 2021/8/12 15:02
     */
    List<Comment> getCommentList(CommentParamVO commentParamVO);

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
    void add(CommentParamVO commentVO, String userUid);

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
}
