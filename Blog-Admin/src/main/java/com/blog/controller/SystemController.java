package com.blog.controller;

import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.domain.vo.AdminVO;
import com.blog.business.admin.service.AdminService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yujunhong
 * @date 2021/9/28 11:56
 */
@RestController
@RequestMapping("/system")
@Api(value = "系统设置相关接口", tags = {"系统设置相关接口"})
public class SystemController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取我的信息
     *
     * @return 获取我的信息
     * @author yujunhong
     * @date 2021/9/28 12:00
     */
    @ApiOperation(value = "获取我的信息")
    @GetMapping("/getMe")
    public ResultBody getMe() {
        Admin me = adminService.getMe();
        return ResultBody.success(me);
    }

    /**
     * 编辑我的信息
     *
     * @param adminVO 实体对象
     * @return 编辑我的信息
     * @author yujunhong
     * @date 2021/9/28 14:10
     */
    @ApiOperation(value = "编辑我的信息")
    @PostMapping("/editMe")
    public ResultBody editMe(@RequestBody AdminVO adminVO) {
        return adminService.editMe(adminVO);
    }

    /**
     * 修改密码
     *
     * @param oldPwd 老密码
     * @param newPwd 新密码
     * @return 修改密码
     * @author yujunhong
     * @date 2021/9/28 14:14
     */
    @ApiOperation(value = "修改密码")
    @PostMapping("/changePwd")
    public ResultBody changePwd(@ApiParam(name = "oldPwd", value = "旧密码", required = false) @RequestParam(name =
            "oldPwd", required = false) String oldPwd,
                                @ApiParam(name = "newPwd", value = "新密码", required = false) @RequestParam(name =
                                        "newPwd", required = false) String newPwd) {
        return adminService.changePwd(oldPwd, newPwd);
    }
}
