package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.User;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户id查询用户信息
     *
     * @param userUidList 用户uid集合
     * @return 用户信息
     * @author yujunhong
     * @date 2021/8/12 15:36
     */
    List<User> getUserListByIds(List<String> userUidList);
}
