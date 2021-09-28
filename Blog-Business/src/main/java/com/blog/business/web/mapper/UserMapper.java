package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.User;
import com.blog.business.web.domain.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface UserMapper extends BaseMapper<User> {
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
     * 获取用户列表
     *
     * @param userVO 查询条件实体
     * @param page   分页参数
     * @return 获取用户列表
     * @author yujunhong
     * @date 2021/9/28 15:01
     */
    IPage<User> getPageList(@Param("page") IPage<User> page, @Param("userVO") UserVO userVO);

}
