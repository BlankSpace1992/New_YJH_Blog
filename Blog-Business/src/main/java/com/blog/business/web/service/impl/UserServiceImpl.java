package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.UserMapper;
import com.blog.business.web.service.UserService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

}
