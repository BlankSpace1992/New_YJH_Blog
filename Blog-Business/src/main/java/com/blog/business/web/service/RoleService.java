package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.RoleVO;
import com.blog.business.web.domain.Role;
import com.blog.exception.ResultBody;

/**
 * @author yujunhong
 * @date 2021/5/31 14:03
 */
public interface RoleService extends IService<Role> {


    /**
     * 获取角色信息列表
     *
     * @param roleVO 查询条件
     * @return 角色信息列表
     * @author yujunhong
     * @date 2021/9/26 15:21
     */
    IPage<Role> getRoleList(RoleVO roleVO);

    /**
     * 新增角色信息
     *
     * @param roleVO 新增角色信息实体
     * @return 新增角色信息
     * @author yujunhong
     * @date 2021/9/26 15:29
     */
    ResultBody add(RoleVO roleVO);

    /**
     * 更新角色信息
     *
     * @param roleVO 更新角色信息实体
     * @return 更新角色信息
     * @author yujunhong
     * @date 2021/9/26 15:29
     */
    ResultBody edit(RoleVO roleVO);

    /**
     * 删除角色信息
     *
     * @param roleVO 删除角色信息实体
     * @return 删除角色信息
     * @author yujunhong
     * @date 2021/9/26 15:29
     */
    ResultBody delete(RoleVO roleVO);
}

