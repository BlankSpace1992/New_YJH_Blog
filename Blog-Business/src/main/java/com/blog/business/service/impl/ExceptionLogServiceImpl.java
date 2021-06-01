package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.ExceptionLogMapper;
import com.blog.business.service.ExceptionLogService;
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
