package com.blog.business.web.domain.vo;

import lombok.*;

/**
 * 反馈表
 *
 * @author yujunhong
 * @date 2021/9/9 15:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackVO {

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

}
