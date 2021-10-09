package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
  * @author yujunhong
  * @date 2021/10/8 14:14
  */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 回复某条评论的uid
     */
    private String toUid;

    /**
     * 回复某个人的uid
     */
    private String toUserUid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 评论类型： 0: 评论   1: 点赞
     */
    private Integer type;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 博客uid
     */
    private String blogUid;

    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    private String source;
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;
}
