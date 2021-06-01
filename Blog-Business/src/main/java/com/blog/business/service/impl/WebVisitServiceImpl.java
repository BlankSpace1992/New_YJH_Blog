package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.WebVisitMapper;
import com.blog.business.service.WebVisitService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class WebVisitServiceImpl implements WebVisitService{

    @Resource
    private WebVisitMapper webVisitMapper;

}
