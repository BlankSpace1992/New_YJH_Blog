package com.blog.security;

import com.blog.constants.EnumsStatus;
import com.blog.entity.SecurityUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SpringSecurity用户工厂类
 *
 * @author yujunhong
 * @date 2021/5/31 13:53
 */
public final class SecurityUserFactory {
    /**
     * 禁止new生成工厂类对象
     *
     * @author yujunhong
     * @date 2021/5/31 13:53
     */
    private SecurityUserFactory() {
    }

    /**
     * 通过管理员admin 生成一个SpringSecurity用户
     *
     * @param admin 管理员实体对象
     * @return SpringSecurity中的用户实体类
     * @author yujunhong
     * @date 2021/5/31 13:54
     */
    public static SecurityUser create(Admin admin) {
        boolean enabled = admin.getStatus() == EnumsStatus.ENABLE;
        return new SecurityUser(
                admin.getUid(),
                admin.getUserName(),
                admin.getPassWord(),
                enabled,
                mapToGrantedAuthorities(admin.getRoleNames()));
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
