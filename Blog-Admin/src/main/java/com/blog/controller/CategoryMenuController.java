package com.blog.controller;

import com.blog.business.admin.domain.vo.CategoryMenuVO;
import com.blog.business.web.domain.CategoryMenu;
import com.blog.business.web.service.CategoryMenuService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/9/26 15:57
 */
@RestController
@RequestMapping("/categoryMenu")
@Api(value = "菜单信息相关接口", tags = {"菜单信息相关接口"})
public class CategoryMenuController {
    @Autowired
    private CategoryMenuService categoryMenuService;

    /**
     * 获取菜单列表
     *
     * @param categoryMenuVO 查询条件
     * @return 菜单列表
     * @author yujunhong
     * @date 2021/9/26 15:58
     */
    @ApiOperation(value = "获取菜单列表")
    @GetMapping(value = "/getList")
    public ResultBody getList(CategoryMenuVO categoryMenuVO) {
        Map<String, Object> categoryMenuList = categoryMenuService.getCategoryMenuList(categoryMenuVO);
        return ResultBody.success(categoryMenuList);
    }


    /**
     * 获取所有菜单列表
     *
     * @param keyword 关键词
     * @return 所有菜单列表
     * @author yujunhong
     * @date 2021/9/27 9:57
     */
    @ApiOperation(value = "获取所有菜单列表")
    @GetMapping(value = "/getAll")
    public ResultBody getAll(@RequestParam(value = "keyword", required = false) String keyword) {
        List<CategoryMenu> categoryMenuServiceAll = categoryMenuService.getAll(keyword);
        return ResultBody.success(categoryMenuServiceAll);
    }

    /**
     * 获取所有二级菜单-按钮列表
     *
     * @param keyword 关键词
     * @return 获取所有二级菜单-按钮列表
     * @author yujunhong
     * @date 2021/9/27 10:15
     */
    @ApiOperation(value = "获取所有二级菜单-按钮列表")
    @GetMapping(value = "/getButtonAll")
    public ResultBody getButtonAll(@RequestParam(value = "keyword", required = false) String keyword) {
        List<CategoryMenu> buttonAll = categoryMenuService.getButtonAll(keyword);
        return ResultBody.success(buttonAll);
    }

    /**
     * 增加菜单
     *
     * @param categoryMenuVO 增加菜单实体
     * @return 增加菜单
     * @author yujunhong
     * @date 2021/9/27 10:20
     */
    @ApiOperation(value = "增加菜单")
    @PostMapping("/add")
    public ResultBody add(@RequestBody CategoryMenuVO categoryMenuVO) {
        return categoryMenuService.add(categoryMenuVO);
    }

    /**
     * 编辑菜单
     *
     * @param categoryMenuVO 编辑菜单实体
     * @return 编辑菜单
     * @author yujunhong
     * @date 2021/9/27 10:23
     */
    @ApiOperation(value = "编辑菜单")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody CategoryMenuVO categoryMenuVO) {
        return categoryMenuService.edit(categoryMenuVO);
    }

    /**
     * 删除菜单
     *
     * @param categoryMenuVO 删除菜单实体
     * @return 删除菜单
     * @author yujunhong
     * @date 2021/9/27 10:28
     */
    @ApiOperation(value = "删除菜单")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody CategoryMenuVO categoryMenuVO) {
        return categoryMenuService.delete(categoryMenuVO);
    }

    /**
     * 置顶菜单
     *
     * @param categoryMenuVO 置顶菜单实体
     * @return 置顶菜单
     * @author yujunhong
     * @date 2021/9/27 10:34
     */
    @ApiOperation(value = "置顶菜单")
    @PostMapping("/stick")
    public ResultBody stick(@RequestBody CategoryMenuVO categoryMenuVO) {
        return categoryMenuService.stick(categoryMenuVO);
    }
}
