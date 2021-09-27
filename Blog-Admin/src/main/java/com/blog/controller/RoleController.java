package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.RoleVO;
import com.blog.business.web.domain.Role;
import com.blog.business.web.service.RoleService;
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
 * @date 2021/9/24 16:41
 */
@RestController
@RequestMapping("/role")
@Api(value = "角色相关接口", tags = {"角色相关接口"})
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 获取角色信息列表
     *
     * @param roleVO 查询条件
     * @return 角色信息列表
     * @author yujunhong
     * @date 2021/9/24 16:42
     */
    @ApiOperation(value = "获取角色信息列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody RoleVO roleVO) {
        IPage<Role> roleList =
                roleService.getRoleList(roleVO);
        return ResultBody.success(roleList);
    }

    /**
     * 新增角色信息
     *
     * @param roleVO 新增角色信息实体
     * @return 新增角色信息
     * @author yujunhong
     * @date 2021/9/26 15:27
     */
    @ApiOperation(value = "新增角色信息")
    @PostMapping("/add")
    public ResultBody add(@RequestBody RoleVO roleVO) {
        return roleService.add(roleVO);
    }

    /**
     * 更新角色信息
     *
     * @param roleVO 更新角色信息实体
     * @return 更新角色信息
     * @author yujunhong
     * @date 2021/9/26 15:35
     */
    @ApiOperation(value = "更新角色信息")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody RoleVO roleVO) {
        return roleService.edit(roleVO);
    }

    /**
     * 删除角色信息
     *
     * @param roleVO 删除角色信息实体
     * @return 删除角色信息
     * @author yujunhong
     * @date 2021/9/26 15:39
     */
    @ApiOperation(value = "删除角色信息")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody RoleVO roleVO) {
        return roleService.delete(roleVO);
    }
}
