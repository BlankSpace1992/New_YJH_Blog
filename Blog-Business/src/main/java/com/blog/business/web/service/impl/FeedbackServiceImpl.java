package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.business.web.domain.Feedback;
import com.blog.business.web.domain.User;
import com.blog.business.web.domain.vo.FeedbackVO;
import com.blog.business.web.mapper.FeedbackMapper;
import com.blog.business.web.service.FeedbackService;
import com.blog.business.web.service.UserService;
import com.blog.config.rabbit_mq.RabbitMqUtils;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.exception.CommonErrorException;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
    @Autowired
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RabbitMqUtils rabbitMqUtils;

    @Override
    public IPage<Feedback> getFeedbackList(String userUid) {
        // 设置分页参数
        Page<Feedback> page = new Page<>();
        page.setSize(20);
        page.setCurrent(1);
        // 查询用户反馈信息
        LambdaQueryWrapper<Feedback> feedbackWrapper = new LambdaQueryWrapper<>();
        feedbackWrapper.eq(Feedback::getStatus, EnumsStatus.ENABLE);
        feedbackWrapper.eq(Feedback::getUserUid, userUid);
        feedbackWrapper.orderByDesc(Feedback::getCreateTime);
        return this.page(page, feedbackWrapper);
    }

    @Override
    public void addFeedback(String userUid, FeedbackVO feedbackVO) {
        // 获取对应用户信息
        User user = Optional.ofNullable(userService.getById(userUid)).orElseThrow(() -> new CommonErrorException(
                "用户不存在"));
        // 判断该用户是否被禁言，被禁言的用户，也无法进行反馈操作
        if (user.getCommentStatus() == BaseSysConf.ZERO) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.YOU_DONT_HAVE_PERMISSION_TO_FEEDBACK);
        }
        // 判断是否开启邮件通知
        SystemConfig systemConfig = systemConfigService.getsSystemConfig();
        if (systemConfig != null && EnumsStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
            if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                String feedback = "网站收到新的反馈: " + "<br />"
                        + "标题：" + feedbackVO.getTitle() + "<br />" + "<br />"
                        + "内容" + feedbackVO.getContent();
                rabbitMqUtils.sendSimpleEmail(systemConfig.getEmail(), feedback);
            } else {
                log.error("网站没有配置通知接收的邮箱地址！");
            }
        }
        Feedback feedback = new Feedback();
        feedback.setUserUid(userUid);
        feedback.setTitle(feedbackVO.getTitle());
        feedback.setContent(feedbackVO.getContent());
        // 设置反馈已开启
        feedback.setFeedbackStatus(0);
        feedback.setReply(feedbackVO.getReply());
        this.save(feedback);
    }
}
