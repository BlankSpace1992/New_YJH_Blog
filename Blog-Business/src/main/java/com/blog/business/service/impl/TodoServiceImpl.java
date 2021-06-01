package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.TodoMapper;
import com.blog.business.service.TodoService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class TodoServiceImpl implements TodoService{

    @Resource
    private TodoMapper todoMapper;

}
