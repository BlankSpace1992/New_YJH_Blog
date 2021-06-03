package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.SubjectItemMapper;
import com.blog.business.web.service.SubjectItemService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class SubjectItemServiceImpl implements SubjectItemService {

    @Resource
    private SubjectItemMapper subjectItemMapper;

}
