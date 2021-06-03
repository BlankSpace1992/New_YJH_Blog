package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 评论举报表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:00
 */
@ApiModel(value = "评论举报表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_comment_report")
public class CommentReport {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 举报人uid
     */
    @ApiModelProperty(value = "举报人uid")
    @Excel(name = "举报人uid")
    private String userUid;
    /**
     * 被举报的评论Uid
     */
    @ApiModelProperty(value = "被举报的评论Uid")
    @Excel(name = "被举报的评论Uid")
    private String reportCommentUid;
    /**
     * 被举报的用户uid
     */
    @ApiModelProperty(value = "被举报的用户uid")
    @Excel(name = "被举报的用户uid")
    private String reportUserUid;
    /**
     * 举报的原因
     */
    @ApiModelProperty(value = "举报的原因")
    @Excel(name = "举报的原因")
    private String content;
    /**
     * 进展状态: 0 未查看   1: 已查看  2：已处理
     */
    @ApiModelProperty(value = "进展状态: 0 未查看   1: 已查看  2：已处理")
    @Excel(name = "进展状态: 0 未查看   1: 已查看  2：已处理")
    private Object progress;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Object status;
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
}
