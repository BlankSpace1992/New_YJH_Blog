package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.SubjectMapper;
import com.blog.business.service.SubjectService;
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
