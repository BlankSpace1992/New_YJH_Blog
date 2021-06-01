package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.VisitorMapper;
import com.blog.business.service.VisitorService;
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
