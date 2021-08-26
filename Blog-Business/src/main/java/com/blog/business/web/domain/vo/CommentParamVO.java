package com.blog.business.web.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 评论查询条件vo
 *
 * @author yujunhong
 * @date 2021/8/12 14:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentParamVO {
    /**
     * 评论uid
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
    @NotBlank(message = "博客uid不允许为空")
    private String blogUid;

    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    private String source;
}
