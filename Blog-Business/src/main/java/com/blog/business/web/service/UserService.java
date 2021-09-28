package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.User;
import com.blog.business.web.domain.vo.UserVO;
import com.blog.exception.ResultBody;

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

    /**
     * 用户登录
     *
     * @param userVO 登录实体对象
     * @return token
     * @author yujunhong
     * @date 2021/9/14 15:15
     */
    ResultBody login(UserVO userVO);


    /**
     * 用户注册
     *
     * @param userVO 注册实体对象
     * @return token
     * @author yujunhong
     * @date 2021/9/14 15:15
     */
    ResultBody register(UserVO userVO);

    /**
     * 用户激活
     *
     * @param token token值
     * @author yujunhong
     * @date 2021/9/14 15:15
     */
    ResultBody activeUser(String token);

    /**
     * 获取用户数量
     *
     * @param enableFlag 可用标志
     * @return 用户数量
     * @author yujunhong
     * @date 2021/9/22 16:38
     */
    Integer getUserCount(int enableFlag);

    /**
     * 获取用户列表
     *
     * @param userVO 查询条件实体
     * @return 获取用户列表
     * @author yujunhong
     * @date 2021/9/28 15:01
     */
    IPage<User> getPageList(UserVO userVO);

    /**
     * 新增用户
     *
     * @param userVO 新增实体
     * @return 新增用户
     * @author yujunhong
     * @date 2021/9/28 15:20
     */
    ResultBody add(UserVO userVO);

    /**
     * 编辑用户
     *
     * @param userVO 编辑用户实体
     * @return 编辑用户
     * @author yujunhong
     * @date 2021/9/28 15:20
     */
    ResultBody edit(UserVO userVO);

    /**
     * 删除用户
     *
     * @param userVO 删除用户实体
     * @return 删除用户
     * @author yujunhong
     * @date 2021/9/28 15:20
     */
    ResultBody delete(UserVO userVO);

    /**
     * 删除用户
     *
     * @param userVO 删除用户实体
     * @return 删除用户
     * @author yujunhong
     * @date 2021/9/28 15:20
     */
    ResultBody resetUserPassword(UserVO userVO);
}
