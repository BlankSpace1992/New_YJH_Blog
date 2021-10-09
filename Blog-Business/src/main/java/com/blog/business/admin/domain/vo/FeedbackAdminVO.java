package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 反馈表
 * </p>
 *
 * @author yujunhong
 * @date 2021/10/8 15:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackAdminVO {
    /**
     * 唯一UID
     */
    private String uid;
    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 标题
     */
    private String title;

    /**
     * 反馈的内容
     */
    private String content;

    /**
     * 回复
     */
    private String reply;

    /**
     * 反馈状态： 0：已开启  1：进行中  2：已完成  3：已拒绝
     */
    private Integer feedbackStatus;

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
