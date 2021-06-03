package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.TodoMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.TodoService;
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
