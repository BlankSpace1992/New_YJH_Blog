package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.WebNavbarMapper;
import com.blog.business.service.WebNavbarService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class WebNavbarServiceImpl implements WebNavbarService{

    @Resource
    private WebNavbarMapper webNavbarMapper;

}
