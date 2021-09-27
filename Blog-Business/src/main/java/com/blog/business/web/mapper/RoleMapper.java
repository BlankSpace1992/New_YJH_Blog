package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.RoleVO;
import com.blog.business.web.domain.Role;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 获取角色信息列表
     *
     * @param roleVO 查询条件
     * @param page   分页参数
     * @return 角色信息列表
     * @author yujunhong
     * @date 2021/9/26 15:21
     */
    IPage<Role> getRoleList(@Param("page") IPage<Role> page, @Param("roleVO") RoleVO roleVO);
}
