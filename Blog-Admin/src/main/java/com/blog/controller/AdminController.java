package com.blog.controller;

import com.blog.annotation.AuthorityVerify;
import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.domain.vo.AdminVO;
import com.blog.business.admin.service.AdminService;
import com.blog.constants.BaseMessageConf;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 获取管理员列表
     *
     * @param adminVO 查询条件
     * @return ResultBody
     * @author yujunhong
     * @date 2021/5/31 15:13
     */
    @AuthorityVerify
    @ApiOperation(value = "获取管理员列表", notes = "获取管理员列表")
    @ApiResponse(response = Admin.class, code = 200, message = "获取管理员列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody AdminVO adminVO) {
        return adminService.getAllAdminList(adminVO);
    }

    @AuthorityVerify
    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @PostMapping("/restPwd")
    public ResultBody restPwd(@RequestBody AdminVO adminVO) {
        return adminService.resetPassword(adminVO);
    }

    /**
     * 新增管理员
     *
     * @param adminVO 新增管理员实体
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 16:06
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增管理员")
    public ResultBody add(@RequestBody AdminVO adminVO) {
        return adminService.add(adminVO);
    }

    /**
     * 编辑管理员
     *
     * @param adminVO 编辑管理员实体
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 16:06
     */
    @PostMapping(value = "/edit")
    @ApiOperation(value = "编辑管理员")
    public ResultBody edit(@RequestBody AdminVO adminVO) {
        return adminService.edit(adminVO);
    }

    /**
     * 批量删除管理员
     *
     * @param adminUids 管理员id集合
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/16 16:35
     */
    @PostMapping(value = "/delete")
    @ApiOperation(value = "批量删除管理员")
    public ResultBody delete(@ApiParam(name = "adminUids", value = "管理员uid集合", required = true) @RequestParam(name =
            "adminUids", required = true) List<String> adminUids) {
        return adminService.delete(adminUids);
    }

    /**
     * 获取在线管理员列表
     *
     * @param adminVO 查询条件
     * @return 获取在线管理员列表
     * @author yujunhong
     * @date 2021/9/17 11:13
     */
    @ApiOperation(value = "获取在线管理员列表")
    @PostMapping(value = "/getOnlineAdminList")
    public ResultBody getOnlineAdminList(@RequestBody AdminVO adminVO) {
        return adminService.getOnlineAdminList(adminVO);
    }

    /**
     * 强退用户
     *
     * @param tokenUidList 用户携带token
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/17 11:27
     */
    @ApiOperation(value = "强退用户")
    @PostMapping(value = "/forceLogout")
    public ResultBody forceLogout(@ApiParam(name = "tokenUidList", value = "tokenList", required = false) @RequestBody List<String> tokenUidList) {
        if (StringUtils.isEmpty(tokenUidList)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return adminService.forceLogout(tokenUidList);
    }
}
