package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 评论表
 *
 * @author yujunhong
 * @date 2021/06/01 11:08:59
 */
@ApiModel(value = "评论表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_comment")
public class Comment {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 用户uid
     */
    @ApiModelProperty(value = "用户uid")
    @Excel(name = "用户uid")
    private String userUid;
    /**
     * 回复某条评论的uid
     */
    @ApiModelProperty(value = "回复某条评论的uid")
    @Excel(name = "回复某条评论的uid")
    private String toUid;
    /**
     * 回复某个人的uid
     */
    @ApiModelProperty(value = "回复某个人的uid")
    @Excel(name = "回复某个人的uid")
    private String toUserUid;
    /**
     * 评论内容
     */
    @ApiModelProperty(value = "评论内容")
    @Excel(name = "评论内容")
    private String content;
    /**
     * 博客uid
     */
    @ApiModelProperty(value = "博客uid")
    @Excel(name = "博客uid")
    private String blogUid;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Integer status;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;
    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    @ApiModelProperty(value = "评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等")
    @Excel(name = "评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等")
    private String source;
    /**
     * 评论类型 1:点赞 0:评论
     */
    @ApiModelProperty(value = "评论类型 1:点赞 0:评论")
    @Excel(name = "评论类型 1:点赞 0:评论")
    private Object type;
    /**
     * 一级评论UID
     */
    @ApiModelProperty(value = "一级评论UID")
    @Excel(name = "一级评论UID")
    private String firstCommentUid;

    /**
     * 本条评论是哪个用户说的
     */
    @TableField(exist = false)
    private User user;

    /**
     * 发表评论的用户名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 被回复的用户名
     */
    @TableField(exist = false)
    private String toUserName;


    /**
     * 本条评论对哪个用户说的，如果没有则为一级评论
     */
    @TableField(exist = false)
    private User toUser;

    /**
     * 本条评论下的回复
     */
    @TableField(exist = false)
    private List<Comment> replyList;

    /**
     * 本条评论回复的那条评论
     */
    @TableField(exist = false)
    private Comment toComment;

    /**
     * 评论来源名称
     */
    @TableField(exist = false)
    private String sourceName;

    /**
     * 该评论来源的博客
     */
    @TableField(exist = false)
    private Blog blog;
}
