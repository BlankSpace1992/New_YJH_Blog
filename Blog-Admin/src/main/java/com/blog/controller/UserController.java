package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.User;
import com.blog.business.web.domain.vo.UserVO;
import com.blog.business.web.service.UserService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/9/28 14:28
 */
@RestController
@Api(value = "用户相关接口", tags = {"用户相关接口"})
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 获取用户列表
     *
     * @param userVO 查询条件实体
     * @return 获取用户列表
     * @author yujunhong
     * @date 2021/9/28 14:42
     */
    @ApiOperation(value = "获取用户列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody UserVO userVO) {
        IPage<User> pageList = userService.getPageList(userVO);
        return ResultBody.success(pageList);
    }

    /**
     * 新增用户
     *
     * @param userVO 新增实体
     * @return 新增用户
     * @author yujunhong
     * @date 2021/9/28 15:19
     */
    @ApiOperation(value = "新增用户")
    @PostMapping("/add")
    public ResultBody add(@RequestBody UserVO userVO) {
        return userService.add(userVO);
    }

    /**
     * 编辑用户
     *
     * @param userVO 编辑用户实体
     * @return 编辑用户
     * @author yujunhong
     * @date 2021/9/28 15:19
     */
    @ApiOperation(value = "编辑用户")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody UserVO userVO) {
        return userService.edit(userVO);
    }

    /**
     * 删除用户
     *
     * @param userVO 删除用户实体
     * @return 删除用户
     * @author yujunhong
     * @date 2021/9/28 15:19
     */
    @ApiOperation(value = "删除用户")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody UserVO userVO) {
        return userService.delete(userVO);
    }

    /**
     * 重置用户密码
     *
     * @param userVO 重置用户密码实体
     * @return 重置用户密码
     * @author yujunhong
     * @date 2021/9/28 15:19
     */
    @ApiOperation(value = "重置用户密码")
    @PostMapping("/resetUserPassword")
    public ResultBody resetUserPassword(@RequestBody UserVO userVO) {
        return userService.resetUserPassword(userVO);
    }
}
