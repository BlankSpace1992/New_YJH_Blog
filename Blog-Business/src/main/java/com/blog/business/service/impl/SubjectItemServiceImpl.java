package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.SubjectItemMapper;
import com.blog.business.service.SubjectItemService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class SubjectItemServiceImpl implements SubjectItemService{

    @Resource
    private SubjectItemMapper subjectItemMapper;

}
