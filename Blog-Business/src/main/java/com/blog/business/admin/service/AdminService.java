package com.blog.business.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.Admin;

/**
 * @author yujunhong
 * @date 2021/5/31 13:47
 */
public interface AdminService extends IService<Admin> {

    /**
     * 通过web端根据用户名获取一个admin
     *
     * @param username 用户名
     * @return admin信息
     * @author yujunhong
     * @date 2021/9/1 16:28
     */
    Admin getAdminByUserName(String username);
}

