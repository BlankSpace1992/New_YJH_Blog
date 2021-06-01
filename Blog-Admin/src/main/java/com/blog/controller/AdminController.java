package com.blog.controller;

import com.blog.annotation.AuthorityVerify;
import com.blog.business.service.AdminService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/5/31 15:07
 */
@RestController
@Api(value = "001 - 管理员模块", tags = "001 - 管理员模块")
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * @param admin 插叙条件
     * @return ResultBody
     * @author yujunhong
     * @date 2021/5/31 15:13
     */
    @AuthorityVerify
    @ApiOperation(value = "获取管理员列表", notes = "获取管理员列表")
    @ApiResponse(response = Admin.class, code = 200, message = "获取管理员列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody Admin admin) {
        return ResultBody.success();
    }

    @AuthorityVerify
    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @PostMapping("/restPwd")
    public ResultBody restPwd( @RequestBody Admin adminVO, BindingResult result) {

        return ResultBody.success();
    }
}
