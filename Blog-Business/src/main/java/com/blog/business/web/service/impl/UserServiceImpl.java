package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.User;
import com.blog.business.web.mapper.UserMapper;
import com.blog.business.web.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public List<User> getUserListByIds(List<String> userUidList) {
        return baseMapper.getUserListByIds(userUidList);
    }
}
