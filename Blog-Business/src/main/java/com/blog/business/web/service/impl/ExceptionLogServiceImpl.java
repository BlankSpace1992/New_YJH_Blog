package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.ExceptionLogMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.ExceptionLogService;
/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class ExceptionLogServiceImpl implements ExceptionLogService{

    @Resource
    private ExceptionLogMapper exceptionLogMapper;

}
