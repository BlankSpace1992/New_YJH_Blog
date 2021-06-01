package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.FeedbackMapper;
import com.blog.business.service.FeedbackService;
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
