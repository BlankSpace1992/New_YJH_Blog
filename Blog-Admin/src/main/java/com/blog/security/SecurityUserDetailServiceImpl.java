package com.blog.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.service.AdminService;
import com.blog.business.web.domain.Role;
import com.blog.business.web.service.RoleService;
import com.blog.constants.BaseSysConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 将SpringSecurity中的用户管理和数据库的管理员对应起来
 *
 * @author yujunhong
 * @date 2021/5/31 14:02
 */
@Service
public class SecurityUserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    /**
     * @param username 浏览器输入的用户名【需要保证用户名的唯一性】
     * @return 用户明显
     * @author yujunhong
     * @date 2021/5/31 14:06
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUserName, username);
        wrapper.last(BaseSysConf.LIMIT_ONE);
        // 获取用户信息
        Admin admin =
                Optional.ofNullable(adminService.getOne(wrapper)).orElseThrow(() -> new UsernameNotFoundException(String.format("当前用户:{}不存在", username)));
        // 查询出角色信息封装导admin中
        Role role = roleService.getById(admin.getUid());
        // 角色信息集合
        List<String> roleNames = new ArrayList<>();
        roleNames.add(role.getRoleName());
        admin.setRoleNames(roleNames);
        return SecurityUserFactory.create(admin);
    }
}
