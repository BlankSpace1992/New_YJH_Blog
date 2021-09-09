package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.Feedback;
import com.blog.business.web.domain.vo.FeedbackVO;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface FeedbackService extends IService<Feedback> {

    /**
     * 获取用户反馈
     *
     * @param userUid 用户id
     * @return 用户反馈
     * @author yujunhong
     * @date 2021/9/9 14:30
     */
    IPage<Feedback> getFeedbackList(String userUid);

    /**
     * 新增用户反馈
     *
     * @param userUid    用户id
     * @param feedbackVO 反馈实体
     * @author yujunhong
     * @date 2021/9/9 15:25
     */
    void addFeedback(String userUid, FeedbackVO feedbackVO);
}
