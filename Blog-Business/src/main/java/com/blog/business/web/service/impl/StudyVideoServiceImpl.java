package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.StudyVideoMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.StudyVideoService;
/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class StudyVideoServiceImpl implements StudyVideoService{

    @Resource
    private StudyVideoMapper studyVideoMapper;

}
