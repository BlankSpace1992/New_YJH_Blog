package com.blog.controller;

import com.blog.business.admin.service.AdminService;
import com.blog.business.web.service.WebConfigService;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yujunhong
 * @date 2021/9/17 16:15
 */
@RestController
@RequestMapping(value = "/auth")
@Api(value = "登录相关接口", tags = {"登录相关接口"})
public class LoginController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private WebConfigService webConfigService;

    /**
     * 用户登录
     *
     * @param request      请求
     * @param password     密码
     * @param username     账号
     * @param isRememberMe 是否记住我
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/17 16:51
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public ResultBody login(HttpServletRequest request,
                            @ApiParam(name = "username", value = "用户名或邮箱或手机号") @RequestParam(name = "username",
                                    required = false) String username,
                            @ApiParam(name = "password", value = "密码") @RequestParam(name = "password", required =
                                    false) String password,
                            @ApiParam(name = "isRememberMe", value = "是否记住账号密码") @RequestParam(name = "isRememberMe",
                                    required = false, defaultValue = "false") Boolean isRememberMe) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResultBody.error("账号或密码不能为空");
        }
        return adminService.login(request, username, password, isRememberMe);
    }

    /**
     * 获取用户信息
     *
     * @param request 请求
     * @param token   token值
     * @return 用户信息
     * @author yujunhong
     * @date 2021/9/17 17:33
     */
    @ApiOperation(value = "用户信息")
    @GetMapping(value = "/info")
    public ResultBody info(HttpServletRequest request,
                           @ApiParam(name = "token", value = "token令牌", required = false) @RequestParam(name = "token"
                                   , required = false) String token) {
        return adminService.info(request, token);
    }

    /**
     * 获取当前用户的菜单
     *
     * @param request 请求
     * @return 获取当前用户的菜单
     * @author yujunhong
     * @date 2021/9/18 14:24
     */
    @ApiOperation(value = "获取当前用户的菜单")
    @GetMapping(value = "/getMenu")
    public ResultBody getMenu(HttpServletRequest request) {
        return adminService.getMenu(request);
    }

    /**
     * 获取网站名称
     *
     * @return 获取网站名称
     * @author yujunhong
     * @date 2021/9/18 14:40
     */
    @ApiOperation(value = "获取网站名称")
    @GetMapping(value = "/getWebSiteName")
    public ResultBody getWebSiteName() {
        return webConfigService.getWebSiteName();
    }

    /**
     * 退出登录
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/18 14:45
     */
    @ApiOperation(value = "退出登录")
    @PostMapping(value = "/logout")
    public ResultBody logout() {
        return adminService.logout();
    }
}
