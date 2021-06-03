package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.FeedbackMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.FeedbackService;
/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class FeedbackServiceImpl implements FeedbackService{

    @Resource
    private FeedbackMapper feedbackMapper;

}
