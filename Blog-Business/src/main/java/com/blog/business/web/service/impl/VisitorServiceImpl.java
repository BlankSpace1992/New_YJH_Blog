package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.VisitorMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.VisitorService;
/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class VisitorServiceImpl implements VisitorService{

    @Resource
    private VisitorMapper visitorMapper;

}
