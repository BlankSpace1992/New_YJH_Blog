package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.SubjectMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.SubjectService;
/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class SubjectServiceImpl implements SubjectService{

    @Resource
    private SubjectMapper subjectMapper;

}
