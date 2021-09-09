package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.User;
import com.blog.business.web.domain.vo.UserVO;

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

    /**
     * 通过source uuid获取用户类
     *
     * @param source 登陆类型
     * @param uuid   uuid
     * @return 用户信息
     * @author yujunhong
     * @date 2021/8/12 15:36
     */
    User getUserBySourceAndUuid(String source, String uuid);

    /**
     * 设置Request相关，如浏览器，IP，IP来源
     *
     * @param user 用户实体
     * @return 设置信息后的用户实体
     * @author yujunhong
     * @date 2021/9/7 15:50
     */
    User setRequestInfo(User user);

    /**
     * 编辑用户信息
     *
     * @param token   token值
     * @param userUid 用户uid
     * @param userVO  用户实体
     * @author yujunhong
     * @date 2021/9/9 15:08
     */
    void editUser(UserVO userVO, String userUid, String token);
}
