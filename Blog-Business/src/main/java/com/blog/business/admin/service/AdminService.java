package com.blog.business.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.domain.vo.AdminVO;
import com.blog.exception.ResultBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 获取管理员列表
     *
     * @param adminVO 查询条件
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 14:48
     */
    ResultBody getAllAdminList(AdminVO adminVO);


    /**
     * 重置用户密码
     *
     * @param adminVO 查询条件
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 15:46
     */
    ResultBody resetPassword(AdminVO adminVO);

    /**
     * 新增管理员
     *
     * @param adminVO 新增管理员实体
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 16:09
     */
    ResultBody add(AdminVO adminVO);

    /**
     * 编辑管理员
     *
     * @param adminVO 编辑管理员实体
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 16:09
     */
    ResultBody edit(AdminVO adminVO);

    /**
     * 批量删除管理员
     *
     * @param adminIds 管理员id集合
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 16:09
     */
    ResultBody delete(List<String> adminIds);

    /**
     * 获取在线管理员列表
     *
     * @param adminVO 查询条件
     * @return 获取在线管理员列表
     * @author yujunhong
     * @date 2021/9/17 11:14
     */
    ResultBody getOnlineAdminList(AdminVO adminVO);

    /**
     * 设置在线管理员
     *
     * @param admin            管理员实体
     * @param expirationSecond 过期时间
     * @author yujunhong
     * @date 2021/9/17 17:22
     */
    void addOnlineAdmin(Admin admin, Long expirationSecond);

    /**
     * 强退用户
     *
     * @param tokenUidList 用户携带token
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/17 11:29
     */
    ResultBody forceLogout(List<String> tokenUidList);

    /**
     * 获取当前管理员
     *
     * @return 获取当前管理员
     * @author yujunhong
     * @date 2021/9/17 14:57
     */
    Admin getCurrentAdmin();

    /**
     * 用户登录
     *
     * @param request      请求
     * @param password     密码
     * @param username     账号
     * @param isRememberMe 是否记住我
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/17 16:55
     */
    ResultBody login(HttpServletRequest request, String username, String password, Boolean isRememberMe);


    /**
     * 获取用户信息
     *
     * @param request 请求
     * @param token   token值
     * @return 用户信息
     * @author yujunhong
     * @date 2021/9/17 17:35
     */
    ResultBody info(HttpServletRequest request, String token);

    /**
     * 获取当前用户的菜单
     *
     * @param request 请求
     * @return 获取当前用户的菜单
     * @author yujunhong
     * @date 2021/9/18 14:27
     */
    ResultBody getMenu(HttpServletRequest request);

    /**
     * 退出登录
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/18 14:46
     */
    ResultBody logout();

}

