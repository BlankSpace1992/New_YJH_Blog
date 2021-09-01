package com.blog.business.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.business.admin.domain.Admin;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     * @author yujunhong
     * @date 2021/9/1 16:36
     */
    Admin getAdminByUsername(@Param("username") String username);
}
