package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.BlogSpiderMapper;
import com.blog.business.service.BlogSpiderService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class BlogSpiderServiceImpl implements BlogSpiderService{

    @Resource
    private BlogSpiderMapper blogSpiderMapper;

}