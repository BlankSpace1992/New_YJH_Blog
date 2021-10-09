package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.FeedbackAdminVO;
import com.blog.business.web.domain.Feedback;
import com.blog.business.web.domain.vo.FeedbackVO;
import com.blog.exception.ResultBody;

import java.util.List;

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

    /**
     * 获取反馈列表
     *
     * @param feedbackAdminVO 查询条件
     * @return 获取反馈列表
     * @author yujunhong
     * @date 2021/10/8 15:19
     */
    IPage<Feedback> getPageList(FeedbackAdminVO feedbackAdminVO);

    /**
     * 编辑反馈
     *
     * @param feedbackAdminVO 编辑反馈实体
     * @return 编辑反馈
     * @author yujunhong
     * @date 2021/10/8 15:27
     */
    ResultBody edit(FeedbackAdminVO feedbackAdminVO);

    /**
     * 批量删除反馈
     *
     * @param feedbackVOList 批量删除反馈集合
     * @return 批量删除反馈
     * @author yujunhong
     * @date 2021/10/8 15:27
     */
    ResultBody deleteBatch(List<FeedbackAdminVO> feedbackVOList);
}
