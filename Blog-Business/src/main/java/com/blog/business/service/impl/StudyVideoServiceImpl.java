package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.StudyVideoMapper;
import com.blog.business.service.StudyVideoService;
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