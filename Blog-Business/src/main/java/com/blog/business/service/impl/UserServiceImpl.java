package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.UserMapper;
import com.blog.business.service.UserService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;

}
