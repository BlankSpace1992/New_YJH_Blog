package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 反馈表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:01
 */
@ApiModel(value = "反馈表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_feedback")
public class Feedback {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.ASSIGN_UUID)
    private String uid;
    /**
     * 用户uid
     */
    @ApiModelProperty(value = "用户uid")
    @Excel(name = "用户uid")
    private String userUid;
    /**
     * 反馈的内容
     */
    @ApiModelProperty(value = "反馈的内容")
    @Excel(name = "反馈的内容")
    private String content;
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
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;
    /**
     * 反馈状态： 0：已开启  1：进行中  2：已完成  3：已拒绝
     */
    @ApiModelProperty(value = "反馈状态： 0：已开启  1：进行中  2：已完成  3：已拒绝")
    @Excel(name = "反馈状态： 0：已开启  1：进行中  2：已完成  3：已拒绝")
    private Object feedbackStatus;
    /**
     * 回复
     */
    @ApiModelProperty(value = "回复")
    @Excel(name = "回复")
    private String reply;
    /**
     * 管理员uid
     */
    @ApiModelProperty(value = "管理员uid")
    @Excel(name = "管理员uid")
    private String adminUid;

    /**
     * 反馈用户
     */
    @TableField(exist = false)
    private User user;
}
