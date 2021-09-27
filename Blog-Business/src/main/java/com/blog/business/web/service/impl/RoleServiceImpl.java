package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.domain.vo.RoleVO;
import com.blog.business.admin.service.AdminService;
import com.blog.business.web.domain.Role;
import com.blog.business.web.mapper.RoleMapper;
import com.blog.business.web.service.RoleService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.EnumsStatus;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

/**
 * @author yujunhong
 * @date 2021/5/31 14:03
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AdminService adminService;

    @Override
    public IPage<Role> getRoleList(RoleVO roleVO) {
        // 注入分页参数
        IPage<Role> page = new Page<>();
        page.setCurrent(roleVO.getCurrentPage());
        page.setSize(roleVO.getPageSize());
        return baseMapper.getRoleList(page, roleVO);
    }

    @Override
    public ResultBody add(RoleVO roleVO) {
        // 判断当前角色名是否已经存
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getRoleName, roleVO.getRoleName());
        roleWrapper.eq(Role::getStatus, EnumsStatus.ENABLE);
        Role oldRole = this.getOne(roleWrapper);
        if (StringUtils.isNotNull(oldRole)) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        Role role = new Role();
        role.setRoleName(roleVO.getRoleName());
        role.setCategoryMenuUids(roleVO.getCategoryMenuUids());
        role.setSummary(roleVO.getSummary());
        role.setStatus(EnumsStatus.ENABLE);
        this.save(role);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(RoleVO roleVO) {
        // 判断当前角色是否存在
        Role role = this.getById(roleVO.getUid());
        if (StringUtils.isNull(role)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        role.setRoleName(roleVO.getRoleName());
        role.setCategoryMenuUids(roleVO.getCategoryMenuUids());
        role.setSummary(roleVO.getSummary());
        role.setUpdateTime(new Date());
        this.updateById(role);
        deleteAdminVisitUrl();
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(RoleVO roleVO) {
        // 判断当前角色是否已经绑定了管理员
        LambdaQueryWrapper<Admin> adminQueryWrapper = new LambdaQueryWrapper<>();
        adminQueryWrapper.eq(Admin::getStatus, EnumsStatus.ENABLE);
        adminQueryWrapper.eq(Admin::getRoleUid, roleVO.getUid());
        int count = adminService.count(adminQueryWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.ADMIN_UNDER_THIS_ROLE);
        }
        Role role = this.getById(roleVO.getUid());
        role.setStatus(EnumsStatus.DISABLED);
        role.setUpdateTime(new Date());
        this.updateById(role);
        deleteAdminVisitUrl();
        return ResultBody.success();
    }

    /**
     * 删除Redis中管理员的访问路径
     *
     * @author yujunhong
     * @date 2021/9/26 15:38
     */
    private void deleteAdminVisitUrl() {
        Set<Object> keys = redisUtil.keys(BaseRedisConf.ADMIN_VISIT_MENU + "*");
        redisUtil.delete(keys);
    }
}

